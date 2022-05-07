package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

public class ReplaceItemCommand extends VanillaCommand {

    public ReplaceItemCommand(String name) {
        super(name, "commands.replaceitem.description");
        this.setPermission("nukkit.command.replaceitem");
        this.commandParameters.clear();
        this.commandParameters.put("block", new CommandParameter[]{
                CommandParameter.newEnum("block",false,new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container",false,new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
        this.commandParameters.put("block-oldItemHandling", new CommandParameter[]{
                CommandParameter.newEnum("block",false,new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container",false,new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("oldItemHandling",false,new String[]{"destroy","keep"}),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }
}
