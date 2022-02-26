package commands.text;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class TextCommandContext {
    private MessageReceivedEvent event;
    private String[] args;

    public TextCommandContext(MessageReceivedEvent event, String[] args) {
        this.event = event;
        this.args = Arrays.copyOfRange(args, 1, args.length);
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String[] getArgs() {
        return args;
    }
}
