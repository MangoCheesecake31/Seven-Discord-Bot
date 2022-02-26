package commands.text;

import commands.text.general.HelpCommand;
import commands.text.general.InfoCommand;
import commands.text.general.PingCommand;
import commands.text.music.NowPlayingCommand;
import commands.text.music.PlayCommand;
import commands.text.music.SkipCommand;
import commands.text.music.StopCommand;
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

        this.addTextCommand(new PingCommand());
        this.addTextCommand(new InfoCommand());
        this.addTextCommand(new HelpCommand());
        this.addTextCommand(new PlayCommand());
        this.addTextCommand(new StopCommand());
        this.addTextCommand(new SkipCommand());
        this.addTextCommand(new NowPlayingCommand());
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

        if (invokeCommand != null) {
            invokeCommand.handle(new TextCommandContext(event, args));
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Not a command motherfucker!").queue();
        }
    }
}