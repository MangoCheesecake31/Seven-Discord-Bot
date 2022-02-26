package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class SkipCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public SkipCommand() {
        this.name = "skip";
        this.aliases = new ArrayList<>();
        this.aliases.add("next");
    }

    @Override
    public void handle(TextCommandContext context) {
        // Retrive variables
        GuildVoiceState selfVoiceState = context.getEvent().getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberVoiceState = context.getEvent().getMember().getVoiceState();
        MessageChannel messageChannel = context.getEvent().getChannel();

        // Check if User is in Voice Channel
        if (!memberVoiceState.inAudioChannel()) {
            messageChannel.sendTyping().queue();
            messageChannel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        // Check if the User and the Bot are in the same Voice Channel
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            messageChannel.sendTyping().queue();
            messageChannel.sendMessage("You need to be in the same voice channel!").queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        AudioPlayer audioPlayer = musicManager.player;

        // Check if there is track playing
        if (audioPlayer.getPlayingTrack() == null) {
            messageChannel.sendTyping().queue();
            messageChannel.sendMessage("There is no track currently playing!").queue();
            return;
        }

        // Skip Track
        musicManager.scheduler.nextTrack();
        messageChannel.sendTyping().queue();
        messageChannel.sendMessage("Skipped track!").queue();
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
