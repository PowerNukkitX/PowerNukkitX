package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;

import java.util.NoSuchElementException;

public class TestForBlockCommand extends VanillaCommand {

    public TestForBlockCommand(String name) {
        super(name, "Tests whether a certain block is in a specific location.", "/testforblock <position: x y z> <tileId: int> [dataValue: int]");
        this.setPermission("nukkit.command.testforblock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("position",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("tileId",false, CommandParamType.INT),
                CommandParameter.newType("dataValue",true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Position position = parser.parsePosition();
            int tileId = parser.parseInt();
            int dataValue = 0;

            if (args.length > 4) {
                dataValue = parser.parseInt();
            }

            try {
                GlobalBlockPalette.getOrCreateRuntimeId(tileId, dataValue);
            } catch (NoSuchElementException e) {
                sender.sendMessage(String.format(TextFormat.RED + "There is no such block with ID %1$s:%2$s", tileId, dataValue));
                return false;
            }

            Level level = position.getLevel();

            if (level.getChunkIfLoaded(position.getChunkX(), position.getChunkZ()) == null) {
                sender.sendMessage(TextFormat.RED + "Cannot test for block outside of the world");
                return false;
            }

            Block block = level.getBlock(position, false);
            int id = block.getId();
            int meta = block.getDamage();

            if (id == tileId && meta == dataValue) {
                sender.sendMessage(String.format("Successfully found the block at %1$d,%2$d,%3$d.", position.getFloorX(), position.getFloorY(), position.getFloorZ()));
                return true;
            } else {
                sender.sendMessage(String.format(TextFormat.RED + "The block at %1$d,%2$d,%3$d is %4$d:%5$d (expected: %6$d:%7$d).", position.getFloorX(), position.getFloorY(), position.getFloorZ(), id, meta, tileId, dataValue));
                return false;
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }

        return true;
    }
}
