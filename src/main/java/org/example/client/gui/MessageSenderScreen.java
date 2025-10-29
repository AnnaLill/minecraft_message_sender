package org.example.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Экран для ввода и отправки сообщения на сервер.
 *
 * <p>Построен на Mojang GUI API: {@link EditBox}, {@link Button},
 * рендер через {@link GuiGraphics}. Закрывается по ESC и после отправки.</p>
 */
public class MessageSenderScreen extends Screen {
    private EditBox messageInput;
    private Button sendButton;

    public MessageSenderScreen() {
        super(Component.literal("Send message"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        messageInput = new EditBox(this.font, centerX - 100, centerY - 10, 200, 20, Component.literal("Введите сообщение"));
        messageInput.setMaxLength(256);
        this.addRenderableWidget(messageInput);

        sendButton = Button.builder(Component.literal("Отправить"), b -> {
            String text = messageInput.getValue().trim();
            if (!text.isEmpty()) {
                org.example.client.networking.ClientPacketHandler.sendMessageToServer(text);
                this.onClose();
            }
        }).bounds(centerX - 50, centerY + 20, 100, 20).build();
        this.addRenderableWidget(sendButton);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    }

    public void renderBackground(GuiGraphics g) {
        g.fillGradient(0, 0, this.width, this.height, 0x01000000, 0x5F000000);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}