package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        GuildVoiceState selfVoiceState = context.getEvent().getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberVoiceState = context.getEvent().getMember().getVoiceState();
        MessageChannel messageChannel = context.getEvent().getChannel();
        Message message = context.getEvent().getMessage();

        // Check if User is in Voice Channel
        if (!memberVoiceState.inAudioChannel()) {
            messageChannel.sendTyping().queue();
            message.reply("You need to be in a Voice Channel!").queue();
            return;
        }

        // Check if the User and the Bot are in the same Voice Channel
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            messageChannel.sendTyping().queue();
            message.reply("You need to be in the same Voice Channel!").queue();
            return;
        }

        // Get current audio track
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        // Check if the track is playing
        if (playingTrack == null) {
            messageChannel.sendTyping().queue();
            message.reply("There is no track playing!").queue();
            return;
        }

        // Reply
        messageChannel.sendTyping().queue();

        // Compute Time
        String currentPosition = Helper.formatSongDuration(playingTrack.getPosition());
        String fullPosition = Helper.formatSongDuration(playingTrack.getDuration());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Now Playing", playingTrack.getInfo().uri)
                .setDescription("**Track**: " + playingTrack.getInfo().author + " - " + playingTrack.getInfo().title)
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                .setFooter(currentPosition + " / " + fullPosition);
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
