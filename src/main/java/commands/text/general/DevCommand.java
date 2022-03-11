package commands.text.general;

import commands.text.TextCommand;
import commands.text.TextCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class DevCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public DevCommand() {
        this.name = "dev";
        this.aliases = new ArrayList<>();
        this.aliases.add("dv");
    }

    @Override
    public void handle(TextCommandContext context) {
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Validate: Permission
        String admin = "352808225868873730";
        if (!admin.equals(context.getEvent().getMessage().getAuthor().getId())) {
            return;
        }

        // Compute: Runtime Memory Usage
        long usage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double memory = usage * 0.00000095367;

        // Reply: Memory Usage
        message.reply(String.format("Memory: %.2f MB", memory)).queue();
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
        return null;
    }
}
