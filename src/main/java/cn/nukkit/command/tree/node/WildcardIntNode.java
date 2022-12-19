package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;

/**
 * 代表一个可以输入通配符 * 的{@link IntNode},当输入通配符时，将会解析结果将变成默认值{@link #defaultV}
 * <br>
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#WILDCARD_INT WILDCARD_INT}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class WildcardIntNode extends ParamNode<Integer> {
    private final int defaultV;

    public WildcardIntNode() {
        this(Integer.MIN_VALUE);
    }

    public WildcardIntNode(int defaultV) {
        this.defaultV = defaultV;
    }

    @Override
    public void fill(String arg) throws CommandSyntaxException {
        try {
            if (arg.length() == 1 && arg.charAt(0) == '*') {
                this.value = defaultV;
            } else this.value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException();
        }
    }

}
