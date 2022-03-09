package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class PauseCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public PauseCommand() {
        this.name = "pause";
        this.aliases = new ArrayList<>();
    }

    @Override
    public void handle(TextCommandContext context) {

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) {
            return;
        }

        // Get AudioPlayer
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;

        // Reply
        messageChannel.sendTyping().queue();

        String title;
        if (audioPlayer.isPaused()) {
            audioPlayer.setPaused(false);
            title = "The current song has been unpaused.";
        } else {
            audioPlayer.setPaused(true);
            title = "The current song has been paused";
        }

        EmbedBuilder eb = Helper.generateSimpleEmbed(title, "");
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

    @Override
    public EmbedBuilder getHelpEmbed() {
        return null;
    }
}
