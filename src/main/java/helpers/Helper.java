package helpers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommandContext;
import driver.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
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

    /*
        Generates an Embed Builder for simple messages
    */
    public static EmbedBuilder generateSimpleEmbed(String title, String message) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
        return (message.length() != 0) ? eb.setDescription(message) : eb;
    }

    /*
        Generate an Embed Builder for Now Playing messages
    */
    public static EmbedBuilder generateNowPlayingEmbed(AudioTrack track, boolean showTime) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Now Playing", track.getInfo().uri)
                .setDescription("**Track**: " + track.getInfo().author + " - " + track.getInfo().title)
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
        return (showTime) ? eb.setFooter(Helper.formatSongDuration(track.getPosition()) + " / " + Helper.formatSongDuration(track.getDuration())) : eb;
    }

    /*
        Validate user Voice States for Music Text Commands
    */
    public static boolean validateUserMusicVoiceState(TextCommandContext context, boolean isPlayCommand) {
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Retrieve: Voice States
        GuildVoiceState selfVoiceState = context.getEvent().getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberVoiceState = context.getEvent().getMember().getVoiceState();

        // Validate: Voice States
        if (!memberVoiceState.inAudioChannel()) {
            message.replyEmbeds(generateSimpleEmbed("You need to be in a Voice Channel.","").build()).queue();
            return false;
        }

        // Apply: Connect Bot to User Voice Channel
        if (isPlayCommand && !selfVoiceState.inAudioChannel()) {
            AudioManager audioManager = context.getEvent().getGuild().getAudioManager();
            audioManager.openAudioConnection(memberVoiceState.getChannel());
            selfVoiceState = memberVoiceState;
        }

        // Validate: Voice States
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            message.replyEmbeds(generateSimpleEmbed("You need to be in the same Voice Channel.", "").build()).queue();
            return false;
        }
        return true;
    }

}
