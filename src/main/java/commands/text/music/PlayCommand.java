package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import helpers.Helper;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

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

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, true)) {
            return;
        }

        // Create link
        String link = String.join(" ", String.join(" ", context.getArgs()));

        // Validate URL
        if (!Helper.isUrl(link)) {
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
}
