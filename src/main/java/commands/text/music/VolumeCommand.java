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
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Validate: Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) { return; }

        // Retrieve: AudioPlayer
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;

        // Retrieve: Command Arguments
        String[] args = context.getArgs();

        // Validate: Command Arguments
        if (args.length == 0) {
            // Reply: Current Volume
            message.replyEmbeds(Helper.generateSimpleEmbed(String.format("Current Volume: %d", audioPlayer.getVolume()), "").build()).queue();
            return;
        }

        try {
            int volume = Integer.parseInt(args[0]);

            // Validate: Volume Range
            if (volume < 0 || 100 < volume) {
                throw new IllegalArgumentException("Error: Volume provided must be value in the range of 0 - 100.");
            }

            // Apply: Adjust AudioPlayer Volume
            audioPlayer.setVolume(volume);

            // Reply: Success
            message.replyEmbeds(Helper.generateSimpleEmbed(String.format("Volume set to %d", volume), "").build()).queue();

        } catch (NumberFormatException e) {
            // Reply: Argument Error
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", "Error: Volume provided must be a whole number between 0 - 100.").build()).queue();

        } catch (IllegalArgumentException e) {
            // Reply: Argument Error
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", e.getMessage()).build()).queue();

        }
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
