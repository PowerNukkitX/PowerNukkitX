package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

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
                CommandParameter.newType("tileData",true,CommandParamType.INT),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"})
        });
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        if (parser.matchCommandForm() == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        Position position;
        Block block;
        String tileName = null;
        try {
            position = parser.parsePosition();
            tileName = parser.parseString();
            tileName = tileName.startsWith("minecraft:") ? tileName : "minecraft:" + tileName;
            int tileId = BlockStateRegistry.getBlockId(tileName);
            block = Block.get(tileId);
            if (parser.hasNext()) {
                block.setDamage(parser.parseInt());
            }
        } catch (CommandSyntaxException ignored) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        } catch (IndexOutOfBoundsException ignored) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.setblock.notFound", tileName));
            return false;
        }

        String oldBlockHandling = "replace";
        if (parser.hasNext()) {
            try {
                oldBlockHandling = parser.parseString();
            } catch (CommandSyntaxException e) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
            switch (oldBlockHandling) {
                case "destroy":
                case "keep":
                case "replace":
                    break;
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
            }
        }

        if (!sender.getPosition().level.isYInRange((int) position.y)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.setblock.outOfWorld"));
            return false;
        }

        Level level = sender.getPosition().getLevel();

        Block current = level.getBlock(position);

        if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.setblock.noChange"));
            return false;
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
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.setblock.noChange"));
                    return false;
                }
            }
        }


        level.setBlock(position, block);
        sender.sendMessage(new TranslationContainer("commands.setblock.success"));
        return true;
    }
}
