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

public class PauseCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public PauseCommand() {
        this.name = "pause";
        this.aliases = new ArrayList<>();
        this.aliases.add("ps");
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

        // Reply: Player Playback State
        String title = (audioPlayer.isPaused()) ? "The current song has been unpaused." : "The current song has been paused";
        audioPlayer.setPaused(!audioPlayer.isPaused());
        message.replyEmbeds(Helper.generateSimpleEmbed(title, "").build()).queue();
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
        sb.append("Description: ").append("Pause or unpause the player.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
    }
}
