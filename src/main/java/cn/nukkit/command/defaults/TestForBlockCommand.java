package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.Map;
import java.util.NoSuchElementException;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TestForBlockCommand extends VanillaCommand {

    public TestForBlockCommand(String name) {
        super(name, "commands.testforblock.description");
        this.setPermission("nukkit.command.testforblock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("position", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("dataValue", true, CommandParamType.INT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position position = list.getResult(0);
        Block tileName = list.getResult(1);
        int tileId = tileName.getId();
        int dataValue = 0;
        if (list.hasResult(2)) {
            dataValue = list.getResult(2);
        }
        try {
            GlobalBlockPalette.getOrCreateRuntimeId(tileId, dataValue);
        } catch (NoSuchElementException e) {
            log.addError("commands.give.block.notFound", String.valueOf(tileId)).output();
            return 0;
        }

        Level level = position.getLevel();

        if (level.getChunkIfLoaded(position.getChunkX(), position.getChunkZ()) == null) {
            log.addError("commands.testforblock.outOfWorld").output();
            return 0;
        }

        Block block = level.getBlock(position, false);
        int id = block.getId();
        int meta = block.getDamage();

        if (id == tileId && meta == dataValue) {
            log.addSuccess("commands.testforblock.success", String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ())).output();
            return 1;
        } else {
            log.addError("commands.testforblock.failed.tile", String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ()), String.valueOf(id), String.valueOf(tileId))
                    .output();
            return 0;
        }
    }
}
