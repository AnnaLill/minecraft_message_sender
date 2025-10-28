package org.example.server.db.repository;

import org.example.server.db.entity.MessageEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

public class MessageRepository {

    private static SessionFactory sessionFactory = null;
    private static boolean initializationAttempted = false;

    /**
     * Ленивая инициализация Hibernate SessionFactory.
     * Позволяет модеру работать без БД, сохраняя сообщения в лог.
     */
    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null && !initializationAttempted) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
                System.out.println("Hibernate подключен к PostgreSQL!");
            } catch (Exception e) {
                System.err.println("PostgreSQL не доступна, сообщения будут логироваться в консоль");
                System.err.println("Запустите PostgreSQL или создайте БД 'minecraft_messages'");
            } finally {
                initializationAttempted = true;
            }
        }
        return sessionFactory;
    }

    /**
     * Сохраняет сообщение в базу данных PostgreSQL.
     * Если БД недоступна, логирует сообщение в консоль сервера.
     *
     * @param uuid UUID игрока
     * @param text Текст сообщения
     */
    public static void saveMessage(UUID uuid, String text) {
        System.out.println("Сообщение от игрока: " + uuid);
        System.out.println("Текст: " + text);
        
        SessionFactory factory = getSessionFactory();
        
        if (factory == null) {
            System.out.println("Сообщение успешно получено сервером (БД недоступна, сохранено в лог)!");
            return;
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            
            MessageEntity entity = new MessageEntity(uuid, text);
            session.persist(entity);
            
            session.getTransaction().commit();
            System.out.println("Сообщение сохранено в БД!");
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }
}
