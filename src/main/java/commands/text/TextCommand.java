package commands.text;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface TextCommand {
    void handle(TextCommandContext context);
    String getName();
    List<String> getAliases();
    EmbedBuilder getHelpEmbed();
}
