package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;

public class ScoreboardCommand extends VanillaCommand {

    public ScoreboardCommand(String name) {
        super(name,"commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        //todo: add parameters
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
        //todo: implement it
    }
}
