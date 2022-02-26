package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public HelpCommand() {
        this.name = "help";
        this.aliases = new ArrayList<>();
    }

    @Override
    public void handle(TextCommandContext context) {

        // TODO: Fancy Smancy Embed

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
