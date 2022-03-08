package helpers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommandContext;
import driver.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

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

    public static boolean isNumber(char number) {
        if (number == '\0') {
            return false;
        }

        try {
            Long.parseLong(String.valueOf(number));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
        Generates an Embed Builder for view queue messages
    */



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

    /*
        Validate user Voice States for Music Text Commands
    */
    public static boolean validateUserMusicVoiceState(TextCommandContext context, boolean isPlayCommand) {

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();
        Message message = context.getEvent().getMessage();

        // Obtain Voice States of User & Bot
        GuildVoiceState selfVoiceState = context.getEvent().getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberVoiceState = context.getEvent().getMember().getVoiceState();

        // Check if User is in Voice Channel
        if (!memberVoiceState.inAudioChannel()) {
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("You need to be in a Voice Channel!", "");
            message.replyEmbeds(eb.build()).queue();
            return false;
        }

        // Connect Bot to User Voice Channel when needed
        if (isPlayCommand && !selfVoiceState.inAudioChannel()) {
            AudioManager audioManager = context.getEvent().getGuild().getAudioManager();
            audioManager.openAudioConnection(memberVoiceState.getChannel());
            selfVoiceState = memberVoiceState;
        }

        // Check if the User and the Bot are in the same Voice Channel
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("You need to be in the same Voice Channel!", "");
            message.replyEmbeds(eb.build()).queue();
            return false;
        }
        return true;
    }

}
