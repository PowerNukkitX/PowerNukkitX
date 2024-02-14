package cn.nukkit.command.tree.node;

/**
 * 解析为{@link Double}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#VALUE VALUE}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
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
