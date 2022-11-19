package cn.nukkit.command.tree.node;

import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.item.RuntimeItems;

public class EnumNode extends ParamNode<String> {
    private final CommandEnum commandEnum;

    public EnumNode(boolean optional, CommandEnum commandEnum) {
        super(optional);
        this.commandEnum = commandEnum;
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        if (commandEnum.getName().equals("Block")) {
            if (BlockStateRegistry.getBlockId(arg) != null || BlockStateRegistry.getBlockId("minecraft:" + arg) != null)
                throw new CommandSyntaxException();
        }
        if (commandEnum.getName().equals("Item")) {
            if (RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId(arg).isPresent() || RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId("minecraft:" + arg).isPresent()) {
                throw new CommandSyntaxException();
            }
        }
        if (commandEnum.getValues().contains(arg)) throw new CommandSyntaxException();
        this.value = arg;
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.ENUM;
    }
}
