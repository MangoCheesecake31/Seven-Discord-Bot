package commands.text;

import commands.text.general.DevCommand;
import commands.text.general.HelpCommand;
import commands.text.general.InfoCommand;
import commands.text.general.PingCommand;
import commands.text.music.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

import java.util.regex.Pattern;

public class TextCommandHandler  {
    public final static HashMap<String, TextCommand> commands = new HashMap<>();
    public final static HashMap<String, TextCommand> aliases = new HashMap<>();
    public static String BOT_PREFIX;

    public TextCommandHandler(String prefix) {
        BOT_PREFIX = prefix;

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
        this.addTextCommand(new DevCommand());
    }

    private void addTextCommand(TextCommand newCommand) {
        // Check command names and aliases
        if (commands.get(newCommand.getName()) == null && aliases.get(newCommand.getName()) == null) {
            for (String alias: newCommand.getAliases()) {
                if (commands.get(alias) != null || aliases.get(alias) != null) {
                    throw new IllegalArgumentException("A command with this name/alias already exist! [" + alias + "]");
                }
            }

            // Add to Maps
            commands.put(newCommand.getName(), newCommand);
            for (String alias: newCommand.getAliases()) {
                aliases.put(alias, newCommand);
            }
            return;
        }
        throw new IllegalArgumentException("A command with this name/alias already exist! [" + newCommand.getName() + "]");
    }

    private TextCommand getCommand(String query) {
        TextCommand invokedCommand =  commands.get(query);
        if (invokedCommand == null) {
            invokedCommand = aliases.get(query);
        }
        return invokedCommand;
    }

    public void handle(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().replaceFirst(Pattern.quote(BOT_PREFIX), "").split("\\s");
        TextCommand invokeCommand = this.getCommand(args[0].toLowerCase());

        if (invokeCommand == null) { return; }
        invokeCommand.handle(new TextCommandContext(event, args));
    }
}
