package helpers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import driver.Config;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static String formatSongDuration(long time) {
        // Compute Hours, Minutes, Seconds
        long currentHours = TimeUnit.MILLISECONDS.toHours(time);
        long currentMinutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        long currentSeconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;

        // Format and Return
        if (currentHours == 0) {
            return String.format("%02d:%02d", currentMinutes, currentSeconds);
        } else {
            return String.format("%02d:%02d:%02d", currentHours, currentMinutes, currentSeconds);
        }
    }

    public static boolean isUrl(String link) {
        try {
            new URL(link);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isNumber(String number) {
        if (number == null) {
            return false;
        }

        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
        Generates an Embed Builder for view queue messages
    */
    public static EmbedBuilder generateQueueEmbed(BlockingQueue<AudioTrack> queue, int page) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Current Queue");
        eb.setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));

        // Build Queue List String
        ArrayList<AudioTrack> trackList = new ArrayList<>(queue);
        int queueSize = trackList.size();
        int startIndex = page * 20;
        int endIndex = startIndex  + 20;

        String list = "";
        for (int i = startIndex; i < queueSize && i < endIndex; i++) {
            AudioTrack track = trackList.get(i);
            list += String.format("%d | %s - %s [%9s]\n", i, track.getInfo().author, track.getInfo().title, Helper.formatSongDuration(track.getDuration()));
        }
        eb.setDescription(list);
        return eb;
    }

    /*
        Generates an Embed Builder for simple messages
    */
    public static EmbedBuilder generateSimpleEmbed(String title, String message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
        if (message.length() != 0) {
            eb.setDescription(message);
        }
        return eb;
    }

    /*
        Generate an Embed Builder for Now Playing messages
    */
    public static EmbedBuilder generateNowPlayingEmbed(AudioTrack track, boolean showTime) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Now Playing");
        eb.setTitle("Now Playing", track.getInfo().uri);
        eb.setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title);
        eb.setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));

        // Add optional time
        if (showTime) {
            String currentPosition = Helper.formatSongDuration(track.getPosition());
            String fullPosition = Helper.formatSongDuration(track.getDuration());
            eb.setFooter(currentPosition + " / " + fullPosition);
        }
        return eb;
    }
}
