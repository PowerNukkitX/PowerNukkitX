package org.powernukkitx.command.defaults;

import org.powernukkitx.block.Block;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Map;


public class TestForBlockCommand extends VanillaCommand {

    public TestForBlockCommand(String name) {
        super(name, "commands.testforblock.description");
        this.setPermission("nukkit.command.testforblock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("dataValue", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position position = list.getResult(0);
        Block tileName = list.getResult(1);
        String tileId = tileName.getId();
        int dataValue = 0;
        if (list.hasResult(2)) {
            dataValue = list.getResult(2);
        }
        if (!Block.get(tileId).getProperties().containBlockState((short) dataValue)) {
            log.addError("commands.give.block.notFound", tileId).output();
            return 0;
        }

        Level level = position.getLevel();

        if (level.getChunkIfLoaded(position.getChunkX(), position.getChunkZ()) == null) {
            log.addError("commands.testforblock.outOfWorld").output();
            return 0;
        }

        Block block = level.getBlock(position, false);
        String id = block.getId();
        int meta = block.getBlockState().specialValue();

        if (id.equals(tileId) && meta == dataValue) {
            log.addSuccess("commands.testforblock.success", String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ())).output();
            return 1;
        } else {
            log.addError("commands.testforblock.failed.tile", String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ()), String.valueOf(id), String.valueOf(tileId))
                    .output();
            return 0;
        }
    }
}
