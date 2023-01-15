package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SetBlockCommand extends VanillaCommand {

    public SetBlockCommand(String name) {
        super(name, "commands.setblock.description");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("tileData", true, CommandParamType.INT),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"})
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position position = list.getResult(0);
        Block block = list.getResult(1);
        try {
            if (list.hasResult(2)) {
                int data = list.getResult(2);
                block.setDamage(data);
            }
        } catch (IndexOutOfBoundsException | InvalidBlockStateException ignored) {
            log.addError("commands.setblock.notFound", block.getPersistenceName()).output();
            return 0;
        }
        String oldBlockHandling = "replace";
        if (list.hasResult(3)) {
            oldBlockHandling = list.getResult(3);
        }
        if (!sender.getPosition().level.isYInRange((int) position.y)) {
            log.addError("commands.setblock.outOfWorld").output();
            return 0;
        }

        Level level = sender.getPosition().getLevel();
        Block current = level.getBlock(position);
        if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
            log.addError("commands.setblock.noChange").output();
            return 0;
        }
        if (current.getId() != Block.AIR) {
            switch (oldBlockHandling) {
                case "destroy" -> {
                    if (sender.isPlayer()) {
                        level.useBreakOn(position, null, Item.get(Item.AIR), sender.asPlayer(), true, true);
                    } else {
                        level.useBreakOn(position);
                    }
                }
                case "keep" -> {
                    log.addError("commands.setblock.noChange").output();
                    return 0;
                }
            }
        }
        level.setBlock(position, block);
        log.addSuccess("commands.setblock.success").output();
        return 1;
    }
}