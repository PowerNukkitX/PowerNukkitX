package cn.nukkit.command.tree.node;

import java.util.regex.Pattern;

public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?(-?\\d+)|[~^]");

    public IntPositionNode(boolean optional) {
        super(optional, INT_POS_PATTERN);
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.INT_POS;
    }
}
