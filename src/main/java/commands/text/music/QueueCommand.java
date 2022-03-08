package commands.text.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import driver.Config;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.collections4.ListUtils;
import org.jetbrains.annotations.NotNull;
import threads.BotExecutorService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;

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
        // Retrieve: Messages
        Message message = context.getEvent().getMessage();

        // Validate: Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) { return; }

        // Retrieve: Audio Queue
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        // Reply: Empty Queue
        if (queue.isEmpty()) {
            message.replyEmbeds(Helper.generateSimpleEmbed("The queue is currently empty.", "").build()).queue();
            return;
        }

        // Retrieve: Command Arguments
        String[] args = context.getArgs();

        // Validate: Command Arguments
        int page = 1;
        if (args.length != 0) {
            if (!Helper.isNumber(args[0])) {
                // Reply: Argument Error
                message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", "Error: Argument provided must be a whole number.").build());
                return;
            }
            page = Integer.parseInt(args[0]);
            if (page < 1) {
                // Reply: Argument Error
                message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Argument", String.format("Error: Page number provided is invalid [%d].", page)).build()).queue();
                return;
            }
        }

        // Compute: Max Pages
        int totalPages = (int) Math.ceil(queue.size() * 1.0 / 20);

        // Validate: Page Range
        if (totalPages < page) {
            // Reply: Page Range Error
            message.replyEmbeds(Helper.generateSimpleEmbed("Invalid Page", "Error: Argument provided exceeds the current queue size.").build()).queue();
            return;
        }

        // Update: Adjust page index for Arrays
        page--;

        // Reply: Queue Message
        QueuePageButtons eventListener = new QueuePageButtons(queue, page, context.getEvent().getJDA());
        message.replyEmbeds(eventListener.generateQueueEmbed().build())
                .setActionRows(eventListener.generateButtonComponents())
                .queue(msg -> {
                    eventListener.setMessageId(msg.getIdLong());
                    eventListener.setChannelId(msg.getChannel().getIdLong());
                });

        // Apply: Event Listener
        context.getEvent().getJDA().addEventListener(eventListener);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    private class QueuePageButtons extends ListenerAdapter {
        private long messageId;
        private long channelId;
        private int page;
        private int totalPages;
        private final BlockingQueue<AudioTrack> queue;

        private Button backQueueButton = Button.primary("back", "<");
        private Button nextQueueButton = Button.primary("next", ">");
        private final JDA jda;

        private InteractionHook hook;
        private ScheduledFuture<?> future;

        public QueuePageButtons(BlockingQueue<AudioTrack> queue, int page, JDA jda) {
            this.queue = queue;
            this.page = page;
            this.jda = jda;
            this.scheduleExpiry();
        }

        @Override
        public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
            // Validate: Same Queue Message
            if (this.messageId != event.getMessageIdLong()) { return; }
            event.deferEdit().queue();
            this.hook = event.getHook();

            // Update: Current Page
            this.page = (event.getComponentId().equals("back")) ? this.page - 1 : this.page + 1;

            // Update: Queue Message Embed
            this.hook.editOriginalEmbeds(this.generateQueueEmbed().build())
                    .setActionRows(this.generateButtonComponents())
                    .queue();

            // Apply: Queue Button Expiry
            this.cancelExpiry();
            this.scheduleExpiry();
        }

        public void setMessageId(long messageId) {
            this.messageId = messageId;
        }

        public void setChannelId(long channelId) {
            this.channelId = channelId;
        }

        public EmbedBuilder generateQueueEmbed() {
            // Build: Queue List String
            ArrayList<AudioTrack> trackList = new ArrayList<>(this.queue);
            List<List<AudioTrack>> partitions = ListUtils.partition(trackList, 20);

            // Compute: Total Pages
            this.totalPages = partitions.size();

            int index = this.page * 20;
            StringBuilder queueList = new StringBuilder();
            for (AudioTrack track: partitions.get(this.page)) {
                AudioTrackInfo info = track.getInfo();
                queueList.append(String.format("`[%d]` [%s - %s](%s) `[%s]`\n", index + 1, info.author, info.title, info.uri, Helper.formatSongDuration(track.getDuration())));
                index++;
            }

            return new EmbedBuilder()
                    .setTitle("Current Queue")
                    .setColor(new Color(Integer.parseInt(Config.get("DEFAULT_EMBED_COLOR"), 16)))
                    .setDescription(queueList.toString())
                    .setFooter(String.format("Page %d/%d | Displaying tracks %d to %d of %d", this.page + 1, this.totalPages, this.page * 20 + 1, index, trackList.size()));
        }

        public ActionRow generateButtonComponents() {
            this.backQueueButton = (this.page == 0) ? this.backQueueButton.asDisabled() : this.backQueueButton.asEnabled();
            this.nextQueueButton = (this.page == this.totalPages - 1) ? this.nextQueueButton.asDisabled() : this.nextQueueButton.asEnabled();
            return ActionRow.of(this.backQueueButton, this.nextQueueButton);
        }

        private void scheduleExpiry() {
            if (this.future != null) { return; }

            // Apply: Schedule Button Time Out Task
            this.future = BotExecutorService.getInstance().start(() -> {
                if (hook == null) {
                    jda.getTextChannelById(this.channelId).editMessageComponentsById(this.messageId, this.generateButtonComponents().asDisabled()).queue();
                } else {
                    hook.editOriginalComponents().setActionRows(this.generateButtonComponents().asDisabled()).queue();
                }
                jda.removeEventListener(this);
            }, 5000);
        }

        private void cancelExpiry() {
            if (this.future == null) { return; }
            this.future.cancel(true);
            this.future = null;
        }
    }
}
