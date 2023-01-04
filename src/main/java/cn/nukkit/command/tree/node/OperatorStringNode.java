package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.common.collect.Sets;

import java.util.HashSet;

/**
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#OPERATOR OPERATOR}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class OperatorStringNode extends StringNode {
    private static final HashSet<String> OPERATOR = Sets.newHashSet("+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");

    @Override
    public void fill(String arg) {
        if (OPERATOR.contains(arg)) this.value = arg;
        else this.error();
    }

}
