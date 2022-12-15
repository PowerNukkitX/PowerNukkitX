package cn.nukkit.command.tree.node;

import cn.nukkit.command.tree.ParamNodeType;

import java.util.regex.Pattern;


public class FloatPositionNode extends PositionNode {
    // 这个正则可以提取参数中的坐标 也可以用来验证(通过比较字符长度)
    private static final Pattern FLOAT_POS_PATTERN = Pattern.compile("[~^]?-?\\d+(?:\\.\\d+)?|[~^]");

    public FloatPositionNode() {
        super(FLOAT_POS_PATTERN);
    }

    @Override
    public ParamNodeType type() {
        return ParamNodeType.POSITION;
    }
}
