package commands.text.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.text.TextCommand;
import commands.text.TextCommandContext;
import helpers.Helper;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

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

        // Retrieve variables
        MessageChannel messageChannel = context.getEvent().getChannel();

        // Validate Voice States
        if (!Helper.validateUserMusicVoiceState(context, false)) {
            return;
        }

        // Check if arguments were provided
        String args[] = context.getArgs();

        if (args.length == 0) {
            EmbedBuilder eb = Helper.generateSimpleEmbed("Missing Argument", "Syntax: remove <track number A>, <track number B>...");
            messageChannel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String arg = String.join(" ", args);
        int indexes;


        Helper.validateRemoveQueueArguments(arg);



//
//        // Get AudioPlayer
//        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getEvent().getGuild());
//        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
//
//        ArrayList<AudioTrack> trackList = new ArrayList<>(queue);





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
