package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 解析为{@link Double}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#VALUE VALUE}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
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
