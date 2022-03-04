package events.buttons;

import events.buttons.text.music.BackQueueButtonEvent;
import events.buttons.text.music.NextQueueButtonEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;

public class TextCommandButtonHandler {
    private final HashMap<String, TextCommandButtonEvent> events;

    public TextCommandButtonHandler() {
        this.events = new HashMap<>();

        // Populate Text Command Button Events
        this.addTextCommandButtonEvent(new BackQueueButtonEvent());
        this.addTextCommandButtonEvent(new NextQueueButtonEvent());
    }

    public void addTextCommandButtonEvent(TextCommandButtonEvent newButtonEvent) {
        if (this.events.get(newButtonEvent.getName()) == null) {
            // Add to Maps
            events.put(newButtonEvent.getName(), newButtonEvent);
            return;
        }
        throw new IllegalArgumentException("An event with this name already exist! [" + newButtonEvent.getName() + "]");
    }

    public TextCommandButtonEvent getEvent(String query) {
        return this.events.get(query);
    }

    public void handle(ButtonInteractionEvent event) {
        TextCommandButtonEvent invokedEvent = this.getEvent(event.getComponentId());
        invokedEvent.handle(event);
    }
}
