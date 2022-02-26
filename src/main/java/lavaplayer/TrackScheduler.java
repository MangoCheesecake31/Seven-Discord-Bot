package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import driver.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public AudioPlayer player;
    public BlockingQueue<AudioTrack> queue;
    public TextChannel latestChannel;

    public TrackScheduler(AudioPlayer audio_player) {
        this.player = audio_player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queueTrack(AudioTrack track, TextChannel latestChannel) {
        this.latestChannel = latestChannel;
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        // Play next track
        AudioTrack nextTrack = this.queue.poll();
        this.player.startTrack(nextTrack, false);

        // Empty Queue
        if (nextTrack == null) {
            return;
        }

        // Reply
        this.latestChannel.sendTyping().queue();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Now Playing", nextTrack.getInfo().uri)
                .setDescription("**Track**: " + nextTrack.getInfo().author + " - " + nextTrack.getInfo().title)
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
        this.latestChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        if (reason.mayStartNext) {
            nextTrack();
        }
    }
}
