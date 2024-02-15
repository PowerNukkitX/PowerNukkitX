package cn.nukkit.command.tree.node;

import java.util.regex.Pattern;

/**
 * 验证是否为整数坐标并将参数解析为{@link cn.nukkit.level.Position Position}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#BLOCK_POSITION BLOCK_POSITION}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?([-+]?\\d+)|[~^]");

    public IntPositionNode() {
        super(INT_POS_PATTERN);
    }

}
