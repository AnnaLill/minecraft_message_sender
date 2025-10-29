package org.example.server.db.service;

import java.util.UUID;

/**
 * Сервис для работы с сообщениями.
 *
 * <p>Абстракция поверх слоя репозитория. Реализация может выполнять
 * валидацию/нормализацию и делегировать сохранение.</p>
 */
public interface MessageService {
    void saveMessage(UUID uuid, String text);
}
