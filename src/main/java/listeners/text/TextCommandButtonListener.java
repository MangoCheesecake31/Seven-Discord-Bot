package listeners.text;

import events.buttons.TextCommandButtonHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TextCommandButtonListener extends ListenerAdapter {
    private TextCommandButtonHandler handler;

    public TextCommandButtonListener() {
        this.handler = new TextCommandButtonHandler();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        // Handle Button Event
        this.handler.handle(event);
    }
}
