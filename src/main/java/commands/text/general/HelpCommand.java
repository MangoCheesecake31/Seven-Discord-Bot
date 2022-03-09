package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import commands.text.TextCommandHandler;
import helpers.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
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
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Retrieve: Command Arguments
        String args[] = context.getArgs();

        // Retrieve: Queried Command
        String command = (args.length == 0) ? "help" : args[0].toLowerCase();

        // Retrieve: Text Command
        TextCommand textCommand = TextCommandHandler.commands.get(command);
        textCommand = (textCommand == null) ? TextCommandHandler.aliases.get(command) : textCommand;

        // Validate: Existing Command
        if (textCommand == null) {
            // Reply: Invalid Command
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", "Error: Queried command does not exist.").build()).queue();
            return;
        }

        // Reply: Command Help Info
        message.replyEmbeds(textCommand.getHelpEmbed().build()).queue();
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
