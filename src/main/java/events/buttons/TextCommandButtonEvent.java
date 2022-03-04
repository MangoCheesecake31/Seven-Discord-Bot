package events.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface TextCommandButtonEvent {
    void handle(ButtonInteractionEvent event);
    String getName();
}
