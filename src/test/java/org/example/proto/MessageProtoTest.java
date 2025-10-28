package org.example.proto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Тесты для Protobuf сообщений.
 * 
 * <p>Проверяют корректность сериализации и десериализации
 * сообщений между клиентом и сервером.</p>
 */
@DisplayName("Protobuf Message Tests")
class MessageProtoTest {

    @Test
    @DisplayName("Должен корректно создавать и сериализовать сообщение")
    void shouldCreateAndSerializeMessage() {
        String testText = "Тестовое сообщение";

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(testText)
                .build();
        assertThat(message.getText()).isEqualTo(testText);
        assertThat(message.toByteArray()).isNotEmpty();
    }

    @Test
    @DisplayName("Должен корректно десериализовать сообщение из байтов")
    void shouldDeserializeMessageFromBytes() throws Exception {
        String testText = "Сообщение для десериализации";
        MessageProto.Message originalMessage = MessageProto.Message.newBuilder()
                .setText(testText)
                .build();
        byte[] messageBytes = originalMessage.toByteArray();

        MessageProto.Message deserializedMessage = MessageProto.Message.parseFrom(messageBytes);

        assertThat(deserializedMessage.getText()).isEqualTo(testText);
        assertThat(deserializedMessage).isEqualTo(originalMessage);
    }

    @Test
    @DisplayName("Должен обрабатывать пустые сообщения")
    void shouldHandleEmptyMessages() {
        String emptyText = "";

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(emptyText)
                .build();

        assertThat(message.getText()).isEmpty();
        byte[] bytes = message.toByteArray();
        assertThatCode(() -> {
            MessageProto.Message deserialized = MessageProto.Message.parseFrom(bytes);
            assertThat(deserialized.getText()).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обрабатывать длинные сообщения")
    void shouldHandleLongMessages() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("Очень длинное сообщение номер ").append(i).append(" ");
        }

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(longText.toString())
                .build();

        assertThat(message.getText()).hasSize(longText.length());
        assertThat(message.getText()).startsWith("Очень длинное сообщение номер 0");
    }

    @Test
    @DisplayName("Должен обрабатывать специальные символы")
    void shouldHandleSpecialCharacters() {
        String specialText = "Специальные символы: !@#$%^&*()_+-=[]{}|;':\",./<>?";

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(specialText)
                .build();

        assertThat(message.getText()).isEqualTo(specialText);

        byte[] bytes = message.toByteArray();
        assertThat(bytes).isNotEmpty();
    }
}
