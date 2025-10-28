package org.example.server.db.service;

import java.util.UUID;

/**
 * Сервис для работы с сообщениями
 */
public interface MessageService {
    void saveMessage(UUID uuid, String text);
}
