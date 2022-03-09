package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import driver.Config;
import helpers.Helper;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
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

    @Override
    public EmbedBuilder getHelpEmbed() {
        return new EmbedBuilder()
                .setTitle("Command: play")
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                .setDescription("Syntax: `$play <Youtube Video/Playlist URL>`\n")
                .setFooter("Aliases: p");
    }
}
