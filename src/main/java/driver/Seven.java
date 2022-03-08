package driver;

import listeners.text.TextCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.security.auth.login.LoginException;

public class Seven {
    public static JDA jda;
    public static final String BOT_PREFIX = Config.get("DEFAULT_PREFIX");

    public static void main(String[] args) throws LoginException {
        // Build Bot
        jda = JDABuilder.createDefault(Config.get("BOT_TOKEN")).build();

        // Set Presence
        jda.getPresence().setStatus(OnlineStatus.IDLE);
        jda.getPresence().setActivity(Activity.playing("Discord"));

        // Set Listeners
        jda.addEventListener(new TextCommandListener(BOT_PREFIX));

        // Settings
        MessageAction.setDefaultMentionRepliedUser(false);
    }
}
