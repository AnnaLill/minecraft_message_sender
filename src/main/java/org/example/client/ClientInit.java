package org.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.example.ExampleMod;
import org.example.client.gui.MessageSenderScreen;
import org.lwjgl.glfw.GLFW;

/**
 * Клиентский инициализатор.
 *
 * <p>Регистрирует {@link KeyMapping} (клавиша M) для открытия экрана отправки
 * сообщений и обработчик подтверждений с сервера, показывающий текст в чате
 * через {@code displayClientMessage}.</p>
 */
public class ClientInit implements ClientModInitializer {
    private static KeyMapping openMessageScreenKey;

    @Override
    public void onInitializeClient() {
        System.out.println("ClientInit: Начинаем инициализацию...");

        openMessageScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.minecraft_message_sender.open_screen",
                GLFW.GLFW_KEY_M,
                "category.minecraft_message_sender"
        ));

        System.out.println("Клавиша M (английская) зарегистрирована для открытия экрана сообщений");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMessageScreenKey.consumeClick()) {
                System.out.println("Клавиша M нажата!");
                if (client.player != null) {
                    System.out.println("Открываем экран отправки сообщений");
                    client.setScreen(new MessageSenderScreen());
                } else {
                    System.out.println("Игрок не найден, экран не открыт");
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.ConfirmationPayload.TYPE, (payload, context) -> {
            var client = context.client();
            client.execute(() -> {
                if (client.player != null) {
                    client.player.displayClientMessage(Component.literal(payload.message()), false);
                }
            });
        });

        System.out.println("ClientInit инициализирован успешно");
    }
}