package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;

public class IntNode extends ParamNode<Integer> {
    public IntNode(boolean optional) {
        super(optional);
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        try {
            this.value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException();
        }
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.INT;
    }
}
