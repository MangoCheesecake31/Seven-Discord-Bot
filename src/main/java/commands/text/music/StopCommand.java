package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StopCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public StopCommand() {
        this.name = "stop";
        this.aliases = new ArrayList<>();
        this.aliases.add("leave");
    }

    @Override
    public void handle(TextCommandContext context) {

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();
        Message message = context.getEvent().getMessage();

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) {
            return;
        }

        // Stop current track and clear queue
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();

        // Disconnect from Voice Channel
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();
        audioManager.closeAudioConnection();

        // Reply
        messageChannel.sendTyping().queue();

        EmbedBuilder eb = Helper.generateSimpleEmbed("Disconnected from Voice Channel!", "");
        message.replyEmbeds(eb.build()).queue();
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
