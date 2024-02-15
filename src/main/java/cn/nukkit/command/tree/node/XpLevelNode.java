package cn.nukkit.command.tree.node;

/**
 * 验证经验值或等级并解析为{@link Integer}值
 * <p>
 * 不会默认使用，需要手动指定
 */
public class XpLevelNode extends ParamNode<Integer> {
    @Override
    public void fill(String arg) {
        if (arg.endsWith("l") || arg.endsWith("L")) {
            try {
                this.value = Integer.parseInt(arg.substring(0, arg.length() - 1));
            } catch (NumberFormatException e) {
                this.error();
            }
        } else error();
    }
}
