package org.example.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.example.ExampleMod;
import org.example.proto.MessageProto;


/**
 * Утилита отправки клиентских сообщений на сервер.
 *
 * <p>Собирает Protobuf-сообщение и отправляет его как
 * {@link org.example.ExampleMod.MessagePayload} через Fabric API.</p>
 */
public class ClientPacketHandler {
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