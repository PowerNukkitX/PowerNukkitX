package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


/**
 * 解析对应参数为{@link Double}值<br>
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT} {@link cn.nukkit.command.data.CommandParamType#VALUE VALUE}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class FloatNode extends ParamNode<Double> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Double.parseDouble(arg);
        } catch (Exception e) {
            this.error();
        }
    }

}
