package cn.nukkit.command.tree.node;


import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.utils.EntitySelector;


/**
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#WILDCARD_TARGET WILDCARD_TARGET}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class WildcardTargetStringNode extends StringNode {

    @Override
    public void fill(String arg) {
        if (arg.equals("*") || EntitySelector.hasArguments(arg) || Server.getInstance().getPlayer(arg) != null) {//temp not support entity uuid query
            this.value = arg;
        } else this.error();
    }

}
