package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.regex.Pattern;

/**
 * 整数坐标节点，对应参数类型{@link cn.nukkit.command.data.CommandParamType#BLOCK_POSITION BLOCK_POSITION},只能接受整数参数或者通配符输入
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?(-?\\d+)|[~^]");

    public IntPositionNode() {
        super(INT_POS_PATTERN);
    }

}
