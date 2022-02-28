package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    public AudioPlayer player;
    public TrackScheduler scheduler;
    private AudioPlayerSendHandler handler;

    public GuildMusicManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.player.setVolume(20);
        this.scheduler = new TrackScheduler(this.player);
        this.player.addListener(this.scheduler);
        this.handler = new AudioPlayerSendHandler(this.player);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return this.handler;
    }
}
