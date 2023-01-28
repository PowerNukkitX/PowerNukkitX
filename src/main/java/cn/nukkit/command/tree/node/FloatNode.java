package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


/**
 * 解析为{@link Float}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public class FloatNode extends ParamNode<Float> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Float.parseFloat(arg);
        } catch (Exception e) {
            this.error();
        }
    }
}
