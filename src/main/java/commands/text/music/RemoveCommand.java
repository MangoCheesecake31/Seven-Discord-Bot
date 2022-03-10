package commands.text.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import commands.text.TextCommandHandler;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RemoveCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public RemoveCommand() {
        this.name = "remove";
        this.aliases = new ArrayList<>();
        this.aliases.add("rm");
        this.aliases.add("rq");
    }

    @Override
    public void handle(TextCommandContext context) {
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Validate: Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) { return; }

        // Retrieve: Command Argument
        String args[] = context.getArgs();

        // Validate: Command Argument
        if (args.length == 0) {
            message.replyEmbeds(Helper.generateSimpleEmbed("Missing Argument", "Syntax: remove <track number A>, <track number B>... or <track number A>-<track number B>").build()).queue();
            return;
        }

        // Retrieve: AudioPlayer
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        // Validate: Command Arguments
        String regex = "[0-9]*(-[0-9]+)?";
        String expressions[] = String.join("", args).replace(" ", "").split(",");
        Set<Integer> indexes = new HashSet<>();

        try {
            for (String exp : expressions) {
                if (exp.matches(regex)) {
                    // Range Expression
                    if (exp.contains("-")) {
                        String range[] = exp.split("-");
                        int start = Integer.parseInt(range[0]);
                        int end = Integer.parseInt(range[1]);

                        // Validate: Range Format
                        if (start > end) {
                            throw new IllegalArgumentException(String.format("Error: Range indexes are not expressed correctly [%d-%d].", start, end));
                        }

                        // Validate: Range Indexes
                        if (start < 1) {
                            throw new IllegalArgumentException(String.format("Error: Range indexes are invalid [%d-%d].", start, end));
                        }

                        // Validate: Range Indexes
                        if (queue.size() < start || queue.size() < end) {
                            throw new IllegalArgumentException(String.format("Error: Range indexes exceeds queue size [%d-%d].", start, end));
                        }

                        // Apply: Populate the number set that should remove the tracks in the queue
                        for (int i = start; i <= end; i++) {
                            indexes.add(i - 1);
                        }
                    } else {
                        int index = Integer.parseInt(exp);

                        // Validate: Index
                        if (index < 1) {
                            throw new IllegalArgumentException(String.format("Error: Index is invalid [%d].", index));
                        }

                        // Validate: Index
                        if (queue.size() < index) {
                            throw new IllegalArgumentException(String.format("Error: Index exceeds queue size [%d].", index));
                        }

                        // Apply: Populate the number set that should remove the tracks in the queue
                        indexes.add(index - 1);
                    }
                } else {
                    throw new IllegalArgumentException(String.format("Error: [%s].", exp));
                }
            }

            // Process: List of Track indexes to be removed
            ArrayList<Integer> removeIndexes = new ArrayList<>();
            removeIndexes.addAll(indexes);
            Collections.sort(removeIndexes);
            Collections.reverse(removeIndexes);

            // Apply: Remove Tracks
            ArrayList<AudioTrack> trackList = new ArrayList<>(queue);
            for (int i: removeIndexes) {
                trackList.remove(i);
            }

            // Update: Current Queue
            BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();
            for (AudioTrack track: trackList) {
                newQueue.offer(track);
            }
            musicManager.scheduler.queue = newQueue;

            // Reply: Total tracks successfully removed
            message.replyEmbeds(Helper.generateSimpleEmbed(String.format("Removed %d Track(s) from Queue", removeIndexes.size()), "").build()).queue();

        } catch (IllegalArgumentException e) {
            // Reply: Argument Error
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", e.getMessage()).build()).queue();

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

    @Override
    public EmbedBuilder getHelpEmbed() {
        // Build: Help Description
        StringBuilder sb = new StringBuilder();
        sb.append("Command: `").append(this.getName()).append("`\n");
        sb.append("Aliases: `").append(String.join("`, `", this.getAliases())).append("`\n");
        sb.append("```");
        sb.append("Description: ").append("Removes one or more songs in queue based on its position.").append("\n");
        sb.append("Syntax:      ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Song No.]").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Song No.]-[Song No.]").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Song No.], [Song No.]...").append("\n");
        sb.append("             ").append(TextCommandHandler.BOT_PREFIX).append(this.getName()).append(" [Song No.], [Song No.]-[Song No.]...").append("\n");
        sb.append("```");

        return new EmbedBuilder()
                .setTitle("Help Command")
                .setDescription(sb.toString())
                .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)));
    }
}
