package org.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.example.ExampleMod;
import org.example.client.gui.MessageSenderScreen;
import org.lwjgl.glfw.GLFW;

/**
 * Клиентская инициализация мода Minecraft Message Sender.
 * 
 * <p>Отвечает за:</p>
 * <ul>
 *   <li>Регистрацию клавиши M для открытия GUI экрана</li>
 *   <li>Обработку подтверждений от сервера</li>
 *   <li>Управление клиентскими событиями</li>
 * </ul>
 * 
 * @author Minecraft Message Sender Team
 * @version 1.0.0
 * @since 1.21.7
 */
public class ClientInit implements ClientModInitializer {
    /** Клавиша для открытия экрана отправки сообщений */
    private static KeyBinding openMessageScreenKey;

    /**
     * Инициализация клиентской части мода.
     * 
     * <p>Регистрирует:</p>
     * <ul>
     *   <li>Клавишу M для открытия GUI</li>
     *   <li>Обработчик подтверждений от сервера</li>
     * </ul>
     */
    @Override
    public void onInitializeClient() {
        System.out.println("ClientInit: Начинаем инициализацию...");

        openMessageScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.minecraft_message_sender.open_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.minecraft_message_sender"
        ));

        System.out.println("Клавиша M (английская) зарегистрирована для открытия экрана сообщений");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMessageScreenKey.wasPressed()) {
                System.out.println("Клавиша M нажата!");
                if (client.player != null) {
                    System.out.println("Открываем экран отправки сообщений");
                    try {
                        client.setScreen(new MessageSenderScreen());
                        System.out.println("MessageSenderScreen успешно создан");
                    } catch (Exception e) {
                        System.err.println("ОШИБКА создания MessageSenderScreen: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Игрок не найден, экран не открыт");
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.ConfirmationPayload.ID, (payload, context) -> {
            if (context.client().player != null) {
                context.client().player.sendMessage(Text.literal(payload.message()), false);
            }
        });
        
        System.out.println("ClientInit инициализирован успешно");
    }
}



