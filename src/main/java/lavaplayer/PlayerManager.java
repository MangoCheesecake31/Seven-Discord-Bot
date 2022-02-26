package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import driver.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private Map<Long, GuildMusicManager> musicManager;
    private AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManager = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManager.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, User author) {
        GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Queue Track
                musicManager.scheduler.queueTrack(track, channel);

                // Reply
                channel.sendTyping().queue();

                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Queued", track.getInfo().uri)
                        .setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title)
                        .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                        .setFooter(author.getName(), author.getAvatarUrl());
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();

                if (playlist.isSearchResult()) {
                    // Queue Top Track from Query Result
                    AudioTrack track = tracks.get(0);
                    musicManager.scheduler.queueTrack(track, channel);

                    // Reply
                    channel.sendTyping().queue();

                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Queued", track.getInfo().uri)
                            .setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title)
                            .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                            .setFooter(author.getName(), author.getAvatarUrl());
                    channel.sendMessageEmbeds(eb.build()).queue();
                } else {
                    // Queue Entire Track Playlist
                    for (AudioTrack track: tracks) {
                        musicManager.scheduler.queueTrack(track, channel);
                    }

                    // Reply
                    channel.sendTyping().queue();

                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Queued")
                            .setDescription("**Playlist**: " + playlist.getName())
                            .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                            .setFooter(author.getName(), author.getAvatarUrl());
                    channel.sendMessageEmbeds(eb.build()).queue();
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
