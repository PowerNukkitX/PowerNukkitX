package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class SeedCommand extends VanillaCommand {

    public SeedCommand(String name) {
        super(name, "Show the level's seed");//no translation in client
        this.setPermission("nukkit.command.seed");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        long seed;
        if (sender instanceof Player) {
            seed = ((Player) sender).getLevel().getSeed();
        } else {
            seed = sender.getServer().getDefaultLevel().getSeed();
        }
        log.addSuccess("commands.seed.success", String.valueOf(seed)).output();
        return 1;
    }
}
