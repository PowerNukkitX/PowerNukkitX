package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;
import cn.nukkit.command.tree.ParamTree;

public class EnumNode implements IParamNode<String> {
    protected String value = null;
    protected boolean optional;
    private CommandEnum commandEnum;

    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (commandEnum.isSoft()) {
            this.value = arg;
            return;
        }
//        if (commandEnum.getName().equals("Block")) {
//            if (BlockStateRegistry.getBlockId(arg) != null || BlockStateRegistry.getBlockId("minecraft:" + arg) != null)
//                throw new CommandSyntaxException();
//        }
//        if (commandEnum.getName().equals("Item")) {
//            if (RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId(arg).isPresent() || RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId("minecraft:" + arg).isPresent()) {
//                throw new CommandSyntaxException();
//            }
//        }
        if (!commandEnum.getValues().contains(arg)) throw new CommandSyntaxException();
        this.value = arg;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E get() {
        if (this.isOptional()) {
            if (value == null) return null;
            else return (E) value;
        } else return (E) value;
    }

    @Override
    public boolean hasResult() {
        return value != null;
    }

    @Override
    public void reset() {
        this.value = null;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public IParamNode<String> init(ParamTree parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.commandEnum = enumData;
        this.optional = optional;
        return this;
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.ENUM;
    }
}
