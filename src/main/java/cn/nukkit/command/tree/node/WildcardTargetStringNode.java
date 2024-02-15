package cn.nukkit.command.tree.node;


/**
 * 解析为{@link String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#WILDCARD_TARGET WILDCARD_TARGET}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class WildcardTargetStringNode extends StringNode {

    @Override
    public void fill(String arg) {
        //WILDCARD_TARGET不可能解析错误
        this.value = arg;
    }

}
