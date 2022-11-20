package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;

public class WildcardIntNode extends ParamNode<Integer> {
    private final int defaultV;


    public WildcardIntNode(boolean optional) {
        super(optional);
        this.defaultV = Integer.MIN_VALUE;
    }

    public WildcardIntNode(int defaultV, boolean optional) {
        super(optional);
        this.defaultV = defaultV;
    }

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        try {
            if (arg.length() == 1 && arg.charAt(0) == '*') {
                this.value = defaultV;
            } else this.value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException();
        }
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.WILDCARD_INT;
    }
}
