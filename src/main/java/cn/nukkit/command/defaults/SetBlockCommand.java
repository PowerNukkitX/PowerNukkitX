package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

@PowerNukkitOnly
public class SetBlockCommand extends VanillaCommand {
    @PowerNukkitOnly
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

        if (args.length < 4) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));

            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        Position position;
        int data = 0;
        try {
            position = parser.parsePosition();
            if (args.length > 4) {
                data = Integer.parseInt(args[4]);
            }
        } catch (IndexOutOfBoundsException | CommandSyntaxException ignored) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return true;
        }

        String oldBlockHandling = "replace";
        if (args.length > 5) {
            oldBlockHandling = args[5].toLowerCase();
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

        Block block;
        try {
            int blockId = Integer.parseInt(args[3]);
            block = Block.get(blockId, data);
        } catch (NullPointerException|NumberFormatException|IndexOutOfBoundsException ignored) {
            try {
                int blockId = BlockState.of(args[3].startsWith("minecraft:") ? args[3] : args[3].contains(":") ? args[3] : "minecraft:" + args[3]).getBlockId();
                block = Block.get(blockId, data);
            } catch (NullPointerException|IndexOutOfBoundsException ignored2) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.setblock.notFound", args[3]));
                return true;
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
