package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public QueueCommand() {
        this.name = "queue";
        this.aliases = new ArrayList<>();
        this.aliases.add("q");
    }

    @Override
    public void handle(TextCommandContext context) {

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) {
            return;
        }

        // Get AudioTrack Queue
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        // Check if queue is empty
        if (queue.isEmpty()) {
            // Reply
            EmbedBuilder eb = Helper.generateSimpleEmbed("The queue is currently empty.", "");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Retrieve arguments
        String[] args = context.getArgs();

        // Check if arguments were provided
        int page = 0;
        if (args.length != 0) {

            // Check if arguments are valid
            if (!Helper.isNumber(args[0])) {
                // Reply Error
                messageChannel.sendTyping().queue();

                EmbedBuilder eb = Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be a whole number.");
                messageChannel.sendMessageEmbeds(eb.build()).queue();
                return;
            }
            page = Integer.parseInt(args[0]);
        }

        // Check maximum pages
        int totalPages = (int) Math.ceil(queue.size() * 1.0 / 20);
        if (page >= totalPages) {
            // Reply Error
            messageChannel.sendTyping().queue();

            EmbedBuilder eb = Helper.generateSimpleEmbed("Invalid Page", "Error: Argument provided exceeds the current queue size.");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Reply
        messageChannel.sendTyping().queue();

        EmbedBuilder eb = Helper.generateQueueEmbed(queue, page);

        // Add Action Rows
        MessageAction messageAction = messageChannel.sendMessageEmbeds(eb.build());
        messageAction.setActionRow(Button.primary("backTextQueue", "<"), Button.primary("nextTextQueue", ">"));
        messageAction.queue();
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
