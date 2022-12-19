package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;


/**
 * 解析对应参数为{@link Double}值，当解析失败时抛出{@link CommandSyntaxException}<br>
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT} {@link cn.nukkit.command.data.CommandParamType#VALUE VALUE}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class FloatNode extends ParamNode<Double> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
        try {
            this.value = Double.parseDouble(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

}
