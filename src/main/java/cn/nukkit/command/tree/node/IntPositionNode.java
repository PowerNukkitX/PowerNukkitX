package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.regex.Pattern;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?(-?\\d+)|[~^]");

    public IntPositionNode() {
        super(INT_POS_PATTERN);
    }

}
