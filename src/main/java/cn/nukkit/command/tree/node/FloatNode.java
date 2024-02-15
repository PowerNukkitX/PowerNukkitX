package cn.nukkit.command.tree.node;


/**
 * 解析为{@link Float}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
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
