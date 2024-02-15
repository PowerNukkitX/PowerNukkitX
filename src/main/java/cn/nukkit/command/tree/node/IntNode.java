package cn.nukkit.command.tree.node;

/**
 * 解析为{@link cn.nukkit.level.Position Integer}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#INT INT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class IntNode extends ParamNode<Integer> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            this.error();
        }
    }

}
