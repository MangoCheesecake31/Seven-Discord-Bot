package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import commands.text.TextCommandHandler;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeekCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public SeekCommand() {
        this.name = "seek";
        this.aliases = new ArrayList<>();
        this.aliases.add("jump");
    }

    @Override
    public void handle(TextCommandContext context) {
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Validate: Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) { return; }

        // Retrieve: Command Arguments
        String[] args = context.getArgs();

        // Validate: Command Arguments
        if (args.length == 0) {
            // Reply: Missing Arguments
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", "Error: Missing Argument").build()).queue();
            return;
        }

        try {
            long milliseconds = Long.parseLong(String.valueOf(args[0])) * 1000;

            // Retrieve: AudioPlayer
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
            AudioPlayer audioPlayer = musicManager.player;

            // Validate: Seek Position
            if (milliseconds > audioPlayer.getPlayingTrack().getDuration()) {
                throw new IllegalArgumentException(String.format("Error Argument provided exceeds the current song duration [%s].", args[0]));
            }

            // Validate: Seek Position
            if (milliseconds < 0) {
                throw new IllegalArgumentException(String.format("Error Argument provided is not a valid song position [%s].", args[0]));
            }

            // Apply: Seek Song
            audioPlayer.getPlayingTrack().setPosition(milliseconds);

            // Reply: Success
            message.replyEmbeds(Helper.generateSimpleEmbed("Seeking", String.format("Track sough to %s", Helper.formatSongDuration(milliseconds))).build()).queue();

        } catch (NumberFormatException e) {
            // Reply: Argument Error
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be a whole number.").build()).queue();

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
        // Build: Help Description
        StringBuilder sb = new StringBuilder();
        sb.append("Command: `").append(this.getName()).append("`\n");
        sb.append("Aliases: `").append(String.join("`, `", this.getAliases())).append("`\n");
        sb.append("```");
        sb.append("Description: ").append("Seeks the playing track to the specified time.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Seconds]").append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
    }
}
