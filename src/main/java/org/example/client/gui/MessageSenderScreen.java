package org.example.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * GUI экран для отправки сообщений на сервер.
 * 
 * <p>Предоставляет пользовательский интерфейс с:</p>
 * <ul>
 *   <li>Поле ввода текста сообщения (максимум 256 символов)</li>
 *   <li>Кнопка "Отправить" для отправки сообщения</li>
 *   <li>Автоматическое закрытие экрана после отправки</li>
 * </ul>
 * 
 * <p>Сообщения отправляются через {@link org.example.client.networking.ClientPacketHandler}
 * с использованием Protobuf сериализации.</p>
 * 
 * @author Minecraft Message Sender Team
 * @version 1.0.0
 * @since 1.21.7
 */
public class MessageSenderScreen extends Screen {
    /** Поле ввода текста сообщения */
    private TextFieldWidget messageInput;
    /** Кнопка отправки сообщения */
    private ButtonWidget sendButton;

    /**
     * Создает новый экран отправки сообщений.
     */
    public MessageSenderScreen() {
        super(Text.literal("Send message"));
    }

    /**
     * Инициализирует элементы интерфейса.
     * 
     * <p>Создает:</p>
     * <ul>
     *   <li>Поле ввода в центре экрана</li>
     *   <li>Кнопку отправки под полем ввода</li>
     * </ul>
     */
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        messageInput = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 10, 200, 20, Text.literal("Введите сообщение"));
        messageInput.setMaxLength(256);
        this.addDrawableChild(messageInput);

        sendButton = ButtonWidget.builder(Text.literal("Отправить"), button -> {
            String text = messageInput.getText().trim();
            if (!text.isEmpty()) {
                org.example.client.networking.ClientPacketHandler.sendMessageToServer(text);
                this.close();
            }
        }).dimensions(centerX - 50, centerY + 20, 100, 20).build();
        this.addDrawableChild(sendButton);
    }

    /**
     * Отрисовывает полупрозрачный фон экрана.
     * 
     * @param context контекст отрисовки
     * @param mouseX координата X мыши
     * @param mouseY координата Y мыши
     * @param delta время между кадрами
     */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0x01000000, 0x5F000000);
    }

    /**
     * Отрисовывает содержимое экрана.
     * 
     * @param context контекст отрисовки
     * @param mouseX координата X мыши
     * @param mouseY координата Y мыши
     * @param delta время между кадрами
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    }

    /**
     * Определяет, должен ли экран закрываться при нажатии Escape.
     * 
     * @return true - экран закрывается при нажатии Escape
     */
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}