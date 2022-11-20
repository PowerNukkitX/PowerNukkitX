package cn.nukkit.command.tree.node;

import cn.nukkit.command.tree.ParamNodeType;

public class StringNode extends ParamNode<String> {
    public StringNode(boolean optional) {
        super(optional);
    }

    @Override
    public void fill(String arg, Object... extras) {
        this.value = arg;
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.STRING;
    }
}
