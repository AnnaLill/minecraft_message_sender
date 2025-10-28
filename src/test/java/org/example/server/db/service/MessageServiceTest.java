package org.example.server.db.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

/**
 * Тесты для сервиса сообщений.
 * 
 * <p>Проверяют корректность работы бизнес-логики
 * сохранения и обработки сообщений.</p>
 */
@DisplayName("Message Service Tests")
class MessageServiceTest {

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageServiceImpl();
    }

    @Test
    @DisplayName("Должен сохранять сообщение с корректными параметрами")
    void shouldSaveMessageWithCorrectParameters() {
        UUID testUuid = UUID.randomUUID();
        String testText = "Тестовое сообщение";

        assertThatCode(() -> messageService.saveMessage(testUuid, testText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обрабатывать пустые сообщения")
    void shouldHandleEmptyMessages() {
        UUID testUuid = UUID.randomUUID();
        String emptyText = "";

        assertThatCode(() -> messageService.saveMessage(testUuid, emptyText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обрабатывать null UUID")
    void shouldHandleNullUuid() {
        UUID nullUuid = null;
        String testText = "Тестовое сообщение";

        assertThatCode(() -> messageService.saveMessage(nullUuid, testText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обрабатывать null текст")
    void shouldHandleNullText() {
        UUID testUuid = UUID.randomUUID();
        String nullText = null;

        assertThatCode(() -> messageService.saveMessage(testUuid, nullText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обрабатывать длинные сообщения")
    void shouldHandleLongMessages() {
        UUID testUuid = UUID.randomUUID();
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("Очень длинное сообщение ");
        }

        assertThatCode(() -> messageService.saveMessage(testUuid, longText.toString()))
                .doesNotThrowAnyException();
    }
}
