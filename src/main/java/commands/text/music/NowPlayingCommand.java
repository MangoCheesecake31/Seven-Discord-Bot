package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
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
            messageChannel.sendMessage("There is no track playing!").queue();
            return;
        }

        // Reply
        AudioTrackInfo trackInfo = playingTrack.getInfo();
        messageChannel.sendMessageFormat("Now playing %s by %s", trackInfo.title, trackInfo.author).queue();
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
