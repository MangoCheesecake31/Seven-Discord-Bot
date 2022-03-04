package listeners.text;

import commands.text.TextCommandHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TextCommandListener extends ListenerAdapter {
    private String bot_prefix;
    private TextCommandHandler handler;

    public TextCommandListener(String bot_prefix) {
        this.bot_prefix = bot_prefix;
        this.handler = new TextCommandHandler(bot_prefix);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore Bots
        if (event.getAuthor().isBot() && event.isWebhookMessage()) {
            return;
        }

        // Handle Command
        if (event.getMessage().getContentRaw().startsWith(this.bot_prefix)) {
            handler.handle(event);
        }
    }
}
