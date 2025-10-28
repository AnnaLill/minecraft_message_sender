package org.example.server.db.service;

import org.example.server.db.repository.MessageRepository;
import java.util.UUID;

/**
 * Реализация сервиса для работы с сообщениями
 */
public class MessageServiceImpl implements MessageService {
    @Override
    public void saveMessage(UUID uuid, String text) {
        MessageRepository.saveMessage(uuid, text);
    }
}