package org.powernukkitx.command.defaults;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Map;

public class SetBlockCommand extends VanillaCommand {

    public SetBlockCommand(String name) {
        super(name, "commands.setblock.description");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.addCommandParameters("setblock-with-states", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("blockStates", false, CommandParamType.BLOCK_STATE),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"}),
        });
        this.addCommandParameters("setblock-no-states", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.POSITION),
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

        Level level = sender.getPosition().getLevel();
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