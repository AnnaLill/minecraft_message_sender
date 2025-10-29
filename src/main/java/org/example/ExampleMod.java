package org.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.example.server.db.service.MessageService;
import org.example.server.db.service.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Основной серверный мод-инициализатор.
 *
 * <p>Регистрирует сетевые типы пакетов на Mojang API
 * через {@link CustomPacketPayload.Type} и {@link StreamCodec},
 * обрабатывает входящие клиентские сообщения в формате Protobuf и
 * отправляет подтверждение обратно клиенту.</p>
 */
public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "minecraft_message_sender";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final MessageService messageService = new MessageServiceImpl();

    /**
     * Клиент → Сервер полезная нагрузка с сериализованным Protobuf-сообщением.
     */
    public record MessagePayload(byte[] data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<MessagePayload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "message"));

        /**
         * Кодек для сериализации/десериализации полезной нагрузки в сетевой буфер.
         */
        public static final StreamCodec<FriendlyByteBuf, MessagePayload> CODEC = StreamCodec.of(
                (buf, payload) -> buf.writeByteArray(payload.data()),
                buf -> new MessagePayload(buf.readByteArray())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Сервер → Клиент подтверждение доставки/сохранения сообщения.
     */
    public record ConfirmationPayload(String message) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ConfirmationPayload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "confirmation"));

        /**
         * Кодек для строки подтверждения.
         */
        public static final StreamCodec<FriendlyByteBuf, ConfirmationPayload> CODEC = StreamCodec.of(
                (buf, payload) -> buf.writeUtf(payload.message()),
                buf -> new ConfirmationPayload(buf.readUtf())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Minecraft Message Sender Initialized");
        System.out.println("ExampleMod: Серверная часть инициализирована");

        PayloadTypeRegistry.playC2S().register(MessagePayload.TYPE, MessagePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfirmationPayload.TYPE, ConfirmationPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MessagePayload.TYPE, (payload, context) -> {
            MinecraftServer server = context.server();
            ServerPlayer player = context.player();

            server.execute(() -> {
                try {
                    org.example.proto.MessageProto.Message message =
                            org.example.proto.MessageProto.Message.parseFrom(payload.data());
                    LOGGER.info("Received message from player {}: {}", player.getName().getString(), message.getText());
                    messageService.saveMessage(player.getUUID(), message.getText());

                    ConfirmationPayload confirmation = new ConfirmationPayload("Ваше сообщение получено и сохранено!");
                    ServerPlayNetworking.send(player, confirmation);
                } catch (Exception e) {
                    LOGGER.error("Failed parsing protobuf message", e);
                }
            });
        });

        System.out.println("ExampleMod: Все обработчики зарегистрированы");
    }
}