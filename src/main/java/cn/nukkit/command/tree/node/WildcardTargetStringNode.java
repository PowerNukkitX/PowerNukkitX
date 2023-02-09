package cn.nukkit.command.tree.node;


import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.selector.EntitySelectorAPI;


/**
 * 解析为{@link String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#WILDCARD_TARGET WILDCARD_TARGET}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public class WildcardTargetStringNode extends StringNode {

    @Override
    public void fill(String arg) {
        //WILDCARD_TARGET不可能解析错误
        this.value = arg;
    }

}
