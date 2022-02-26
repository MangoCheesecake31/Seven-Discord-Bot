package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public PlayCommand() {
        this.name = "play";
        this.aliases = new ArrayList<>();
        this.aliases.add("p");
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

        // Connect Bot to User Voice Channel when needed
        if (!selfVoiceState.inAudioChannel()) {
            AudioManager audioManager = context.getEvent().getGuild().getAudioManager();
            audioManager.openAudioConnection(memberVoiceState.getChannel());
            selfVoiceState = memberVoiceState;
        }

        // Check if the User and the Bot are in the same Voice Channel
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            messageChannel.sendTyping().queue();
            message.reply("You need to be in the same Voice Channel!").queue();
            return;
        }

        // Create link
        String link = String.join(" ", String.join(" ", context.getArgs()));

        // Validate URL
        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        // Queue Track
        PlayerManager.getInstance().loadAndPlay(context.getEvent().getTextChannel(), link, context.getEvent().getAuthor());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    private boolean isUrl(String link) {
        try {
            new URL(link);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
