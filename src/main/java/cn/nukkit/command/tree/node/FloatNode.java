package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;

public class FloatNode extends ParamNode<Double> {
    public FloatNode(boolean optional) {
        super(optional);
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        try {
            this.value = Double.parseDouble(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.FLOAT;
    }
}
