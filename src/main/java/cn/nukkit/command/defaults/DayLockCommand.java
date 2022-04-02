package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;

public class DayLockCommand extends VanillaCommand {

    public DayLockCommand(String name) {
        super(name, "commands.daylock.description", "commands.daylock.usage", new String[]{"alwaysday"});
        this.setPermission("nukkit.command.daylock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("lock", true,new String[]{"true","false"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            boolean lock = true;

            if (args.length > 0) {
                lock = parser.parseBoolean();
            }

            Level level = parser.getTargetLevel();
            GameRules rules = level.getGameRules();

            if (lock) {
                rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                level.stopTime();
                level.setTime(5000);
                sender.sendMessage("Day-Night cycle locked");
            } else {
                rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                level.startTime();
                sender.sendMessage("Day-Night cycle unlocked");
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }
}
