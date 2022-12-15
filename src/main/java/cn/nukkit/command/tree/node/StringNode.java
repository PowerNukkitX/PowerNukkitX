package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;

public class StringNode extends ParamNode<String> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
        this.value = arg;
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.STRING;
    }
}
