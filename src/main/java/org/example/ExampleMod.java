package org.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.example.server.db.service.MessageService;
import org.example.server.db.service.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Основной класс мода Minecraft Message Sender.
 * 
 * <p>Мод предоставляет функциональность для отправки сообщений от клиента к серверу
 * с использованием Protobuf сериализации и сохранением в PostgreSQL через Hibernate.</p>
 * 
 * <p>Основные возможности:</p>
 * <ul>
 *   <li>GUI экран для ввода сообщений (клавиша M)</li>
 *   <li>Сериализация сообщений через Protobuf</li>
 *   <li>Сохранение в PostgreSQL с помощью Hibernate</li>
 *   <li>Подтверждения от сервера клиенту</li>
 * </ul>
 * 
 * @author Minecraft Message Sender Team
 * @version 1.0.0
 * @since 1.21.7
 */
public class ExampleMod implements ModInitializer {
    /** Идентификатор мода */
    public static final String MOD_ID = "minecraft_message_sender";
    /** Логгер для записи событий мода */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    /** Сервис для работы с сообщениями */
    private final MessageService messageService = new MessageServiceImpl();

    /**
     * Payload для отправки сообщений от клиента к серверу.
     * Содержит сериализованные данные Protobuf сообщения.
     */
    public record MessagePayload(byte[] data) implements CustomPayload {
        /** Идентификатор payload для регистрации в сети */
        public static final Id<MessagePayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "message"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        /** Кодек для сериализации/десериализации payload */
        public static final PacketCodec<RegistryByteBuf, MessagePayload> CODEC = PacketCodec.of(
                (payload, buf) -> buf.writeBytes(payload.data()),
                buf -> {
                    byte[] data = new byte[buf.readableBytes()];
                    buf.readBytes(data);
                    return new MessagePayload(data);
                }
        );
    }

    /**
     * Payload для отправки подтверждения от сервера клиенту.
     * Содержит простую строку с подтверждением получения сообщения.
     */
    public record ConfirmationPayload(String message) implements CustomPayload {
        /** Идентификатор payload для регистрации в сети */
        public static final Id<ConfirmationPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "confirmation"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        /** Кодек для сериализации/десериализации payload */
        public static final PacketCodec<RegistryByteBuf, ConfirmationPayload> CODEC = PacketCodec.of(
                (payload, buf) -> buf.writeString(payload.message()),
                buf -> new ConfirmationPayload(buf.readString())
        );
    }

    /**
     * Инициализация мода при загрузке.
     * 
     * <p>Регистрирует:</p>
     * <ul>
     *   <li>Payload типы для сетевого взаимодействия</li>
     *   <li>Обработчик входящих сообщений от клиентов</li>
     * </ul>
     */
    @Override
    public void onInitialize() {
        LOGGER.info("Minecraft Message Sender Initialized");
        System.out.println("ExampleMod: Серверная часть инициализирована");

        PayloadTypeRegistry.playC2S().register(MessagePayload.ID, MessagePayload.CODEC);

        PayloadTypeRegistry.playS2C().register(ConfirmationPayload.ID, ConfirmationPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MessagePayload.ID, (payload, context) -> {
            MinecraftServer server = context.server();
            ServerPlayerEntity player = context.player();

            server.execute(() -> {
                try {
                    org.example.proto.MessageProto.Message message = org.example.proto.MessageProto.Message.parseFrom(payload.data());
                    LOGGER.info("Received message from player {}: {}", player.getName().getString(), message.getText());
                    messageService.saveMessage(player.getUuid(), message.getText());

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