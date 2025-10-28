package org.example.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.example.ExampleMod;
import org.example.proto.MessageProto;

/**
 * Обработчик сетевых пакетов на клиентской стороне.
 * 
 * <p>Отвечает за отправку сообщений на сервер с использованием:</p>
 * <ul>
 *   <li>Protobuf сериализации для структурированных данных</li>
 *   <li>CustomPayload API Fabric для сетевого взаимодействия</li>
 *   <li>Обработку ошибок при отправке</li>
 * </ul>
 * 
 * @author Minecraft Message Sender Team
 * @version 1.0.0
 * @since 1.21.7
 */
public class ClientPacketHandler {

    /**
     * Отправляет текстовое сообщение на сервер.
     * 
     * <p>Процесс отправки:</p>
     * <ol>
     *   <li>Создает Protobuf сообщение из текста</li>
     *   <li>Сериализует в байтовый массив</li>
     *   <li>Упаковывает в MessagePayload</li>
     *   <li>Отправляет через ClientPlayNetworking</li>
     * </ol>
     * 
     * @param text текст сообщения для отправки
     * @throws RuntimeException если произошла ошибка при сериализации или отправке
     */
    public static void sendMessageToServer(String text) {
        try {
            MessageProto.Message message = MessageProto.Message.newBuilder()
                    .setText(text)
                    .build();

            byte[] messageBytes = message.toByteArray();

            ExampleMod.MessagePayload payload = new ExampleMod.MessagePayload(messageBytes);
            ClientPlayNetworking.send(payload);
        } catch (Exception e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
