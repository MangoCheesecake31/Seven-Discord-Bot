package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class PingCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public PingCommand() {
        this.name = "ping";
        this.aliases = new ArrayList<>();
        this.aliases.add("peng");
        this.aliases.add("pong");
    }

    @Override
    public void handle(TextCommandContext context) {
        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();
        Message message = context.getEvent().getMessage();

        // Reply
        messageChannel.sendTyping().queue();
        message.reply("Pong!").queue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public EmbedBuilder getHelpEmbed() {
        return null;
    }
}
