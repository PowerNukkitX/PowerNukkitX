package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;

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
