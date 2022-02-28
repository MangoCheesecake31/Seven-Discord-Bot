package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class VolumeCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public VolumeCommand() {
        this.name = "volume";
        this.aliases = new ArrayList<>();
        this.aliases.add("vol");
    }

    @Override
    public void handle(TextCommandContext context) {

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();
        Message message = context.getEvent().getMessage();

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) {
            return;
        }

        // Get AudioPlayer
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;

        // Retrieve arguments
        String[] args = context.getArgs();

        // Check if arguments were provided
        if (args.length == 0) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed(String.format("Current Volume: %d", audioPlayer.getVolume()), "");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Check if argument are valid
        if (!Helper.isNumber(args[0])) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be a whole number (0 - 100).");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        int level = Integer.parseInt(context.getArgs()[0]);
        // Check if argument is a valid level
        if (level < 0 || 100 < level) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be value in the range (0 - 100).");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Set Volume
        audioPlayer.setVolume(level);

        // Reply
        messageChannel.sendTyping().queue();

        EmbedBuilder eb = Helper.generateSimpleEmbed(String.format("Volume set to %d", level), "");
        messageChannel.sendMessageEmbeds(eb.build()).queue();
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
