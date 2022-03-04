package commands.text;

import commands.text.general.HelpCommand;
import commands.text.general.InfoCommand;
import commands.text.general.PingCommand;
import commands.text.music.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

import java.util.regex.Pattern;

public class TextCommandHandler  {
    private final HashMap<String, TextCommand> commands;
    private final HashMap<String, TextCommand> aliases;
    private final String BOT_PREFIX;

    public TextCommandHandler(String prefix) {
        this.BOT_PREFIX = prefix;
        this.commands = new HashMap<>();
        this.aliases = new HashMap<>();

        // Populate Text Commands
        this.addTextCommand(new PingCommand());
        this.addTextCommand(new InfoCommand());
        this.addTextCommand(new HelpCommand());
        this.addTextCommand(new PlayCommand());
        this.addTextCommand(new StopCommand());
        this.addTextCommand(new SkipCommand());
        this.addTextCommand(new NowPlayingCommand());
        this.addTextCommand(new PauseCommand());
        this.addTextCommand(new SeekCommand());
        this.addTextCommand(new QueueCommand());
        this.addTextCommand(new VolumeCommand());
        this.addTextCommand(new RemoveCommand());
    }

    private void addTextCommand(TextCommand newCommand) {
        // Check command names and aliases
        if (this.commands.get(newCommand.getName()) == null && this.aliases.get(newCommand.getName()) == null) {
            for (String alias: newCommand.getAliases()) {
                if (this.commands.get(alias) != null || this.aliases.get(alias) != null) {
                    throw new IllegalArgumentException("A command with this name/alias already exist! [" + alias + "]");
                }
            }

            // Add to Maps
            this.commands.put(newCommand.getName(), newCommand);
            for (String alias: newCommand.getAliases()) {
                this.aliases.put(alias, newCommand);
            }
            return;
        }
        throw new IllegalArgumentException("A command with this name/alias already exist! [" + newCommand.getName() + "]");
    }

    private TextCommand getCommand(String query) {
        TextCommand invokedCommand =  this.commands.get(query);
        if (invokedCommand == null) {
            invokedCommand = this.aliases.get(query);
        }
        return invokedCommand;
    }

    public void handle(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().replaceFirst(Pattern.quote(BOT_PREFIX), "").split("\\s");
        TextCommand invokeCommand = this.getCommand(args[0].toLowerCase());

        if (invokeCommand != null) {
            invokeCommand.handle(new TextCommandContext(event, args));
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Not a command motherfucker!").queue();
        }
    }
}
