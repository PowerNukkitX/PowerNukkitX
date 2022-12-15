package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;

public class IntNode extends ParamNode<Integer> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
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
