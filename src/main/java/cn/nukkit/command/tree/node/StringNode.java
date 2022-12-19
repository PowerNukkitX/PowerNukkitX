package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;


/**
 * 结果值为String的节点,对参数不会进行任何验证和处理
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#STRING STRING}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class StringNode extends ParamNode<String> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
        this.value = arg;
    }

}
