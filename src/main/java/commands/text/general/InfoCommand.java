package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public InfoCommand() {
        this.name = "info";
        this.aliases = new ArrayList<>();
    }

    @Override
    public void handle(TextCommandContext context) {
        context.getEvent().getChannel().sendTyping().queue();
        context.getEvent().getChannel().sendMessage("I got nothing boss!").queue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }
}
