package io.github.brainage04.twitchplaysminecraft.command.util.feedback;

import net.minecraft.command.CommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class FeedbackBuilder<T extends CommandSource> {
    protected T source;
    protected MutableText text = Text.empty();
    protected SoundEvent soundEvent = SoundEvents.UI_BUTTON_CLICK.value();
    protected float volume = 1;
    protected float pitch = 1;
    private MessageType messageType = MessageType.NONE;
    protected boolean sendInTwitchChat = false;

    public FeedbackBuilder<T> source(T source) {
        this.source = source;
        return this;
    }

    public FeedbackBuilder<T> text(String text) {
        this.text = Text.literal(text);
        return this;
    }

    public FeedbackBuilder<T> text(MutableText text) {
        this.text = text;
        return this;
    }

    public FeedbackBuilder<T> sound(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
        return this;
    }

    public FeedbackBuilder<T> sound(SoundEvent soundEvent, float volume, float pitch) {
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
        return this;
    }

    public FeedbackBuilder<T> messageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public FeedbackBuilder<T> sendInTwitchChat(boolean sendInTwitchChat) {
        this.sendInTwitchChat = sendInTwitchChat;
        return this;
    }

    private void preProcessText() {
        switch (messageType) {
            case INFO -> text = text.formatted(Formatting.YELLOW);
            case ERROR -> text = text.formatted(Formatting.RED);
            case SUCCESS -> text = text.formatted(Formatting.GREEN);
        }
    }

    protected abstract void sendFeedback();

    public void execute() {
        preProcessText();
        sendFeedback();
    }
}
