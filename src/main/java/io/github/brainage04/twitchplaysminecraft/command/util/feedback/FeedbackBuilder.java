package io.github.brainage04.twitchplaysminecraft.command.util.feedback;

import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class FeedbackBuilder<T extends CommandSource> {
    protected T source;
    protected MutableText text = Text.literal("");
    private MessageType messageType = MessageType.NONE;

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

    public FeedbackBuilder<T> messageType(MessageType messageType) {
        this.messageType = messageType;
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

        // todo: send feedback in twitch chat as well
    }
}
