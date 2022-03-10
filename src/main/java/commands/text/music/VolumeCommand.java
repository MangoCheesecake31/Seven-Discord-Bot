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

import java.awt.*;
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
        // Build: Help Description
        StringBuilder sb = new StringBuilder();
        sb.append("Command: `").append(this.getName()).append("`\n");
        sb.append("Aliases: `").append(String.join("`, `", this.getAliases())).append("`\n");
        sb.append("```");
        sb.append("Description: ").append("Change or display the volume.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Volume]").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
    }
}
