package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PositionNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ExecuteCommandOld extends VanillaCommand {

    public ExecuteCommandOld(String name) {
        super(name, "old execute command", "commands.execute.usage");
        this.setPermission("nukkit.command.executeold");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("command", CommandParamType.COMMAND)
        });
        this.commandParameters.put("detect", new CommandParameter[]{
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("detect", new String[]{"detect"}),
                CommandParameter.newType("detectPos", CommandParamType.POSITION),
                CommandParameter.newType("block", CommandParamType.INT),
                CommandParameter.newType("data", CommandParamType.INT),
                CommandParameter.newType("command", CommandParamType.COMMAND)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        int num = 0;
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        Position pos = list.getResult(1);
        switch (result.getKey()) {
            case "default" -> {
                String command = list.getResult(2);
                for (Entity entity : entities) {
                    ExecutorCommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(pos));
                    int n = executeSender.getServer().executeCommand(executeSender, command);
                    if (n == 0) {
                        log.addError("commands.execute.failed", command, entity.getName());
                    } else num += n;
                }
            }
            case "detect" -> {
                Position detect = ((PositionNode) list.get(3)).get(pos);
                int blockid = list.getResult(4);
                int meta = list.getResult(5);
                String command = list.getResult(6);
                for (Entity entity : entities) {
                    if (detect.getLevelBlock().getId() == blockid && detect.getLevelBlock().getDamage() == meta) {
                        ExecutorCommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(pos));
                        int n = executeSender.getServer().executeCommand(executeSender, command);
                        if (n == 0) {
                            log.addError("commands.execute.failed", command, entity.getName());
                        } else num += n;
                    }
                }
            }
        }
        log.output();
        return num;
    }
}
