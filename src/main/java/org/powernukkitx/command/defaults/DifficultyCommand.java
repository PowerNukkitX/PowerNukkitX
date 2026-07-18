package org.powernukkitx.command.defaults;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.protocol.bedrock.packet.SetDifficultyPacket;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class DifficultyCommand extends VanillaCommand {

    public DifficultyCommand(String name) {
        super(name, "commands.difficulty.description", "%commands.difficulty.usage");
        this.setPermission("nukkit.command.difficulty");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("difficulty", CommandParamType.INT)
        });
        this.commandParameters.put("byString", new CommandParameter[]{
                CommandParameter.newEnum("difficulty", new CommandEnum("Difficulty", "peaceful", "p", "easy", "e", "normal", "n", "hard", "h"))
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        int difficulty;
        switch (result.getKey()) {
            case "default" -> {
                difficulty = list.getResult(0);
            }
            case "byString" -> {
                String str = list.getResult(0);
                difficulty = Server.getDifficultyFromString(str);
            }
            default -> {
                return 0;
            }
        }
        if (sender.getServer().isHardcore()) {
            difficulty = 3;
        }
        if (difficulty != -1) {
            sender.getServer().setDifficulty(difficulty);
            final SetDifficultyPacket pk = new SetDifficultyPacket();
            pk.setDifficulty(sender.getServer().getDifficulty());
            Server.broadcastPacket(new ArrayList<>(sender.getServer().getOnlinePlayers().values()), pk);
            log.addSuccess("commands.difficulty.success", String.valueOf(difficulty)).output(true);
            return 1;
        } else {
            log.addSyntaxErrors(0).output();
            return 0;
        }
    }
}
