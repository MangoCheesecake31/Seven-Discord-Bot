package commands.text.music;

import commands.text.TextCommand;
import commands.text.TextCommandContext;

import java.util.ArrayList;
import java.util.List;

public class VolumeCommand implements TextCommand {
    private String name;
    private ArrayList<String> aliases;

    public VolumeCommand() {
        this.name = "volume";
        this.aliases = new ArrayList<>();
        this.aliases.add("vol");
    }

    @Override
    public void handle(TextCommandContext context) {

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
