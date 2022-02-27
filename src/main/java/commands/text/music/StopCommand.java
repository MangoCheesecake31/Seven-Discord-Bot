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
