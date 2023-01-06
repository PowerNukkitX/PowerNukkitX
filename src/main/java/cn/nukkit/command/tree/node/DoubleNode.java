package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class DoubleNode extends ParamNode<Double> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Double.parseDouble(arg);
        } catch (Exception e) {
            this.error();
        }
    }
}
