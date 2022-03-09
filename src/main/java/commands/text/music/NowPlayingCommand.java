package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

public class NowPlayingCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public NowPlayingCommand() {
        this.name = "nowplaying";
        this.aliases = new ArrayList<>();
        this.aliases.add("np");
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

        // Get current audio track
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        // Check if the track is playing
        if (playingTrack == null) {
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("There is no track playing!", "");
            message.replyEmbeds(eb.build()).queue();
            return;
        }

        // Reply
        messageChannel.sendTyping().queue();

        EmbedBuilder eb = Helper.generateNowPlayingEmbed(playingTrack, true);
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
