package org.example.server.db.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

/**
 * Тесты для Entity сообщений.
 * 
 * <p>Проверяют корректность работы JPA Entity
 * и соответствие схеме базы данных.</p>
 */
@DisplayName("Message Entity Tests")
class MessageEntityTest {

    @Test
    @DisplayName("Должен создавать Entity с корректными параметрами")
    void shouldCreateEntityWithCorrectParameters() {
        UUID testUuid = UUID.randomUUID();
        String testText = "Тестовое сообщение";

        MessageEntity entity = new MessageEntity(testUuid, testText);

        assertThat(entity.getUuid()).isEqualTo(testUuid);
        assertThat(entity.getText()).isEqualTo(testText);
        assertThat(entity.getId()).isNull();
    }

    @Test
    @DisplayName("Должен создавать пустой Entity через конструктор по умолчанию")
    void shouldCreateEmptyEntityWithDefaultConstructor() {
        MessageEntity entity = new MessageEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getUuid()).isNull();
        assertThat(entity.getText()).isNull();
    }

    @Test
    @DisplayName("Должен корректно устанавливать и получать ID")
    void shouldSetAndGetIdCorrectly() {
        MessageEntity entity = new MessageEntity();
        Long testId = 123L;

        entity.setId(testId);

        assertThat(entity.getId()).isEqualTo(testId);
    }

    @Test
    @DisplayName("Должен корректно устанавливать и получать UUID")
    void shouldSetAndGetUuidCorrectly() {
        MessageEntity entity = new MessageEntity();
        UUID testUuid = UUID.randomUUID();

        entity.setUuid(testUuid);

        assertThat(entity.getUuid()).isEqualTo(testUuid);
    }

    @Test
    @DisplayName("Должен корректно устанавливать и получать текст")
    void shouldSetAndGetTextCorrectly() {
        MessageEntity entity = new MessageEntity();
        String testText = "Новый текст сообщения";

        entity.setText(testText);

        assertThat(entity.getText()).isEqualTo(testText);
    }

    @Test
    @DisplayName("Должен обрабатывать пустой текст")
    void shouldHandleEmptyText() {
        UUID testUuid = UUID.randomUUID();
        String emptyText = "";

        MessageEntity entity = new MessageEntity(testUuid, emptyText);

        assertThat(entity.getText()).isEmpty();
        assertThat(entity.getUuid()).isEqualTo(testUuid);
    }

    @Test
    @DisplayName("Должен обрабатывать null значения")
    void shouldHandleNullValues() {
        MessageEntity entity = new MessageEntity();

        entity.setId(null);
        entity.setUuid(null);
        entity.setText(null);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getUuid()).isNull();
        assertThat(entity.getText()).isNull();
    }

    @Test
    @DisplayName("Должен обрабатывать длинный текст")
    void shouldHandleLongText() {
        UUID testUuid = UUID.randomUUID();
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("Очень длинный текст ");
        }

        MessageEntity entity = new MessageEntity(testUuid, longText.toString());

        assertThat(entity.getText()).hasSize(longText.length());
        assertThat(entity.getText()).startsWith("Очень длинный текст");
    }
}

