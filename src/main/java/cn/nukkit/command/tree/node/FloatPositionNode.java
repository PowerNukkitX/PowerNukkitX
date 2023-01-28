package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.regex.Pattern;

/**
 * 验证是否为浮点坐标并解析为{@link cn.nukkit.level.Position Position}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#POSITION POSITION}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public class FloatPositionNode extends PositionNode {
    private static final Pattern FLOAT_POS_PATTERN = Pattern.compile("[~^]?[-+]?\\d+(?:\\.\\d+)?|[~^]");

    public FloatPositionNode() {
        super(FLOAT_POS_PATTERN);
    }

}
