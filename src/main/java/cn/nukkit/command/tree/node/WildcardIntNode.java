package cn.nukkit.command.tree.node;

/**
 * 代表一个可以输入通配符 * 的{@link IntNode},当输入通配符时，将会解析结果将变成默认值{@link #defaultV}
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#WILDCARD_INT WILDCARD_INT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 * <p>
 * {@code defaultV = Integer.MIN_VALUE}
 */
public class WildcardIntNode extends ParamNode<Integer> {
    private final int defaultV;

    public WildcardIntNode() {
        this(Integer.MIN_VALUE);
    }

    public WildcardIntNode(int defaultV) {
        this.defaultV = defaultV;
    }

    @Override
    public void fill(String arg) {
        if (arg.length() == 1 && arg.charAt(0) == '*') {
            this.value = defaultV;
        } else {
            try {
                this.value = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                this.error();
            }
        }
    }

}
