package events.buttons.text.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import events.buttons.TextCommandButtonEvent;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.concurrent.BlockingQueue;

public class BackQueueButtonEvent implements TextCommandButtonEvent {
    private String name = "backTextQueue";

    @Override
    public void handle(ButtonInteractionEvent event) {
        // Get AudioTrack Queue
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        // Sudo Solution: Obtain page numbers
        String footer = event.getMessage().getEmbeds().get(0).getFooter().getText();
        String pageNumbers[] = footer.substring(5, footer.indexOf("|") - 1).split("/");
        int currentPage = Integer.parseInt(pageNumbers[0]);
        int totalPage = Integer.parseInt(pageNumbers[1]);

        // Edit Queue Embed
        EmbedBuilder eb = Helper.generateQueueEmbed(queue, currentPage - 1);
        MessageAction messageAction = event.getMessage().editMessageEmbeds(eb.build());

        // Sudo Solution: Enable/Disable Buttons
        // Create Action Rows
        Button backQueueButton = Button.primary("backTextQueue", "<");
        Button nextQueueButton = Button.primary("nextTextQueue", ">");

        // Update Action Rows
        backQueueButton = (currentPage - 1 == 0) ? backQueueButton.asDisabled() : backQueueButton.asEnabled();
        nextQueueButton = (currentPage - 1 == totalPage) ? nextQueueButton.asDisabled() : nextQueueButton.asEnabled();

        // Reply
        messageAction.setActionRow(backQueueButton, nextQueueButton);
        messageAction.queue();
        event.deferEdit().queue();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
