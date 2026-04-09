package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DestroyBlockParticle;

import java.util.Map;

public class SetBlockCommand extends VanillaCommand {

    public SetBlockCommand(String name) {
        super(name, "commands.setblock.description");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.addCommandParameters("setblock-with-states", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("blockStates", false, CommandParamType.BLOCK_STATES),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"}),
        });
        this.addCommandParameters("setblock-no-states", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"}),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position position = list.getResult(0);
        Block block = list.getResult(1);

        boolean withStates = result.getKey().equals("setblock-with-states");
        if (withStates) {
            BlockState state = list.getResult(2);
            block = state.toBlock();
        }

        int modeIdx = withStates ? 3 : 2;
        String oldBlockHandling = "replace";
        if (list.hasResult(modeIdx)) {
            oldBlockHandling = list.getResult(modeIdx);
        }

        if (!sender.getPosition().level.isYInRange((int) position.y)) {
            log.addError("commands.setblock.outOfWorld").output();
            return 0;
        }

        Dimension level = sender.getPosition().getLevel();
        Block current = level.getBlock(position);
        if (current.getId().equals(block.getId()) && current.getBlockState() == block.getBlockState()) {
            log.addError("commands.setblock.noChange").output();
            return 0;
        }
        if (!(current instanceof BlockAir)) {
            switch (oldBlockHandling) {
                case "destroy" -> {
                    level.addParticle(new DestroyBlockParticle(current.add(0.5), current));
                    if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        for (Item drop : current.getDrops(Item.AIR)) {
                            if (drop.getCount() > 0) {
                                level.dropItem(current.add(0.5, 0.5, 0.5), drop);
                            }
                        }
                        int exp = current.getDropExp();
                        if (exp > 0) {
                            level.dropExpOrb(current.add(0.5, 0.5, 0.5), exp);
                        }
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
