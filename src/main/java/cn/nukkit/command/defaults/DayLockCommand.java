package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;

import java.util.Map;


public class DayLockCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public DayLockCommand(String name) {
        super(name, "commands.daylock.description", "", new String[]{"alwaysday"});
        this.setPermission("nukkit.command.daylock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("lock", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        boolean $2 = true;

        if (list.hasResult(0)) lock = list.getResult(0);

        Level $3 = sender.getPosition().getLevel();
        level = level == null ? sender.getServer().getDefaultLevel() : level;
        GameRules $4 = level.getGameRules();

        if (lock) {
            rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            level.stopTime();
            level.setTime(5000);
            log.addSuccess("commands.always.day.locked").output();
        } else {
            rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            level.startTime();
            log.addSuccess("commands.always.day.unlocked").output();
        }
        return 1;
    }
}
