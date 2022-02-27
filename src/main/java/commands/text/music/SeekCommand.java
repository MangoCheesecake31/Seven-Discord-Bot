package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class SeekCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public SeekCommand() {
        this.name = "seek";
        this.aliases = new ArrayList<>();
        this.aliases.add("jump");
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

        // Retrieve arguments
        String[] args = context.getArgs();

        // Check if arguments were provided
        if (args.length == 0) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Missing Argument", "Syntax: seek <seconds>.");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Check if arguments are valid
        long milliseconds;
        if (!Helper.isNumber(args[0])) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be a whole number.");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }
        milliseconds = Long.parseLong(String.valueOf(context.getArgs()[0])) * 1000;

        // Get AudioPlayer
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;

        // Check seek duration
        if (milliseconds <= audioPlayer.getPlayingTrack().getDuration()) {
            // Seek
            audioPlayer.getPlayingTrack().setPosition(milliseconds);

            // Reply
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Seeking", String.format("Track sought to %s", Helper.formatSongDuration(milliseconds)));
            messageChannel.sendMessageEmbeds(eb.build()).queue();
        } else {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Argument Out of Bounds", "Error: Argument provided exceeds the current song duration.");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
        }
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