package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.util.NoSuchElementException;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TestForBlockCommand extends VanillaCommand {

    public TestForBlockCommand(String name) {
        super(name, "commands.testforblock.description");
        this.setPermission("nukkit.command.testforblock");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("position",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName",false, CommandEnum.ENUM_BLOCK),
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
            String tileName = parser.parseString();
            int tileId = BlockState.of(tileName.startsWith("minecraft:") ? tileName : "minecraft:" + tileName).getBlockId();
            int dataValue = 0;

            if (parser.hasNext()) {
                dataValue = parser.parseInt();
            }

            try {
                GlobalBlockPalette.getOrCreateRuntimeId(tileId, dataValue);
            } catch (NoSuchElementException e) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.block.notFound", String.valueOf(tileId)));
                return false;
            }

            Level level = position.getLevel();

            if (level.getChunkIfLoaded(position.getChunkX(), position.getChunkZ()) == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                return false;
            }

            Block block = level.getBlock(position, false);
            int id = block.getId();
            int meta = block.getDamage();

            if (id == tileId && meta == dataValue) {
                sender.sendMessage(new TranslationContainer("commands.testforblock.success", String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ())));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.failed.tile",String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ()), String.valueOf(id),String.valueOf(tileId)));
                return false;
            }
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
             return false;
        }
    }
}
