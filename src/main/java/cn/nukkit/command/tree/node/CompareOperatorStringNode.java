package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.common.collect.Sets;

import java.util.HashSet;


/**
 * 负责解析ExecuteCommand中的比较操作，解析为{@link String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#COMPARE_OPERATOR COMPARE_OPERATOR}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public class CompareOperatorStringNode extends StringNode {
    private static final HashSet<String> COMPARE_OPERATOR = Sets.newHashSet("<", "<=", "=", ">=", ">");

    @Override
    public void fill(String arg) {
        if (COMPARE_OPERATOR.contains(arg)) this.value = arg;
        else this.error();
    }

}
