package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import commands.text.TextCommandHandler;
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
        // Build: Help Description
        StringBuilder sb = new StringBuilder();
        sb.append("Command: `").append(this.getName()).append("`\n");
        sb.append("Aliases: `").append(String.join("`, `", this.getAliases())).append("`\n");
        sb.append("```");
        sb.append("Description: ").append("Plays or queues a song.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [YouTube URL]").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Query]").append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
    }
}
