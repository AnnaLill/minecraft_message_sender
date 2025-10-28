package org.example.integration;

import org.example.ExampleMod;
import org.example.proto.MessageProto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

/**
 * Интеграционные тесты для полного цикла работы мода.
 * 
 * <p>Проверяют взаимодействие между компонентами:
 * Protobuf сериализация → Payload → Обработка на сервере</p>
 */
@DisplayName("Integration Tests")
class IntegrationTest {

    @Test
    @DisplayName("Должен корректно обрабатывать полный цикл отправки сообщения")
    void shouldHandleFullMessageSendingCycle() {
        String testText = "Интеграционный тест сообщения";
        UUID testUuid = UUID.randomUUID();

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(testText)
                .build();

        assertThat(message.getText()).isEqualTo(testText);
        assertThat(message.toByteArray()).isNotEmpty();

        byte[] messageBytes = message.toByteArray();
        assertThatCode(() -> {
            MessageProto.Message deserializedMessage = MessageProto.Message.parseFrom(messageBytes);
            assertThat(deserializedMessage.getText()).isEqualTo(testText);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен корректно создавать MessagePayload")
    void shouldCreateMessagePayloadCorrectly() {
        String testText = "Тест MessagePayload";
        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(testText)
                .build();
        byte[] messageBytes = message.toByteArray();

        ExampleMod.MessagePayload payload = new ExampleMod.MessagePayload(messageBytes);

        assertThat(payload.data()).isEqualTo(messageBytes);
        assertThat(payload.getId()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно создавать ConfirmationPayload")
    void shouldCreateConfirmationPayloadCorrectly() {
        String confirmationMessage = "✅ Сообщение получено!";

        ExampleMod.ConfirmationPayload payload = new ExampleMod.ConfirmationPayload(confirmationMessage);

        assertThat(payload.message()).isEqualTo(confirmationMessage);
        assertThat(payload.getId()).isNotNull();
    }

    @Test
    @DisplayName("Должен обрабатывать различные типы сообщений")
    void shouldHandleVariousMessageTypes() {
        String[] testMessages = {
            "Обычное сообщение",
            "Сообщение с цифрами: 123456",
            "Сообщение с символами: !@#$%^&*()",
            "Сообщение с эмодзи: 😀🎮⚡",
            "Сообщение на русском языке",
            "English message",
            "Сообщение с переносами строк\nВторая строка",
            ""
        };

        for (String testMessage : testMessages) {
            assertThatCode(() -> {
                MessageProto.Message message = MessageProto.Message.newBuilder()
                        .setText(testMessage)
                        .build();
                
                byte[] bytes = message.toByteArray();
                MessageProto.Message deserialized = MessageProto.Message.parseFrom(bytes);
                
                assertThat(deserialized.getText()).isEqualTo(testMessage);
            }).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Должен проверять соответствие MOD_ID")
    void shouldVerifyModIdConsistency() {
        String modId = ExampleMod.MOD_ID;

        assertThat(modId).isEqualTo("minecraft_message_sender");
        assertThat(modId).isNotEmpty();
        assertThat(modId).matches("^[a-z_]+$");
    }

    @Test
    @DisplayName("Должен проверять корректность Payload ID")
    void shouldVerifyPayloadIdConsistency() {
        var messageId = ExampleMod.MessagePayload.ID;
        var confirmationId = ExampleMod.ConfirmationPayload.ID;

        assertThat(messageId).isNotNull();
        assertThat(confirmationId).isNotNull();
        assertThat(messageId.toString()).contains("minecraft_message_sender");
        assertThat(confirmationId.toString()).contains("minecraft_message_sender");
    }
}
