package lavaplayer;

import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import driver.Config;
import helpers.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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
    private final int maxQueueSize = Integer.parseInt(Config.get("MAX_QUEUE_SIZE"));

    public PlayerManager() {
        this.musicManager = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        // Spotify
        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(Config.get("SPOTIFY_CLIENT_ID"));
        spotifyConfig.setClientSecret(Config.get("SPOTIFY_CLIENT_SECRET"));
        spotifyConfig.setCountryCode("US");
        this.audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, spotifyConfig, this.audioPlayerManager));

        // SoundCloud
        this.audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());

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

    public void loadAndPlay(Message message, String trackUrl, User author) {
        GuildMusicManager musicManager = this.getMusicManager(message.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Retrieve: Current Queue Size
                int totalTracks = musicManager.scheduler.queue.size();

                // Validate: Queue Size
                if (validateQueueSize(totalTracks, message)) {
                    // Apply: Queue Track
                    musicManager.scheduler.queueTrack(track, message.getTextChannel());

                    // Reply: Success
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Queued", track.getInfo().uri)
                            .setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title)
                            .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                            .setFooter(author.getName(), author.getAvatarUrl());
                    message.replyEmbeds(eb.build()).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Retrieve: List of Tracks
                List<AudioTrack> tracks = playlist.getTracks();

                // Retrieve: Current Queue Size
                int totalTracks = musicManager.scheduler.queue.size();
                int totalQueued = 0;

                if (playlist.isSearchResult()) {
                    // Retrieve: Top entry in the Playlist
                    AudioTrack track = tracks.get(0);

                    // Validate: Queue Size
                    if (validateQueueSize(totalTracks, message)) {
                        // Apply: Queue Track
                        totalQueued++;
                        musicManager.scheduler.queueTrack(track, message.getTextChannel());

                        // Reply: Success
                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("Queued", track.getInfo().uri)
                                .setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title)
                                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                                .setFooter(author.getName() + " | " + totalQueued + " track(s).", author.getAvatarUrl());
                        message.replyEmbeds(eb.build()).queue();
                    }
                } else {
                    // Apply: Queue Tracks in Playlist
                    for (AudioTrack track: tracks) {
                        // Validate: Queue Size
                        if (!validateQueueSize(totalTracks, message)) {
                            break;
                        }
                        totalTracks++;
                        totalQueued++;
                        musicManager.scheduler.queueTrack(track, message.getTextChannel());
                    }

                    // Reply: Success
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Queued")
                            .setDescription("**Playlist**: " + playlist.getName())
                            .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                            .setFooter(author.getName() + " | " + totalQueued + " track(s).", author.getAvatarUrl());
                    message.replyEmbeds(eb.build()).queue();
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

    private boolean validateQueueSize(int newTotalTracks, Message message) {
        if (this.maxQueueSize < newTotalTracks + 1) {
            // Reply: Max Queue Size
            message.replyEmbeds(Helper.generateSimpleEmbed(String.format("I can no longer add any more tracks as the max queue size of **%d** has been reached.", maxQueueSize), "").build()).queue();
            return false;
        }
        return true;
    }
}
