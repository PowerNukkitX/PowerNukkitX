package cn.nukkit.command.tree.node;

import cn.nukkit.command.tree.ParamNodeType;

import java.util.regex.Pattern;

public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?(-?\\d+)|[~^]");

    public IntPositionNode() {
        super(INT_POS_PATTERN);
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.BLOCK_POSITION;
    }
}
