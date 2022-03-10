package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import commands.text.TextCommandHandler;
import driver.Config;
import helpers.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
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
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", String.format("Error: Queried command does not exist. [%s]", command)).build()).queue();
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
        // Build: Help Description
        StringBuilder sb = new StringBuilder();
        sb.append("Command: `").append(this.getName()).append("`\n");
        sb.append("Aliases: `").append(String.join("`, `", this.getAliases())).append("`\n");
        sb.append("```");
        sb.append("Description: ").append("Displays information about a command.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Command Name]").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Command Alias]").append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                .setFooter(String.format("Use %scommand to view list of commands.", TextCommandHandler.BOT_PREFIX));
    }
}
