package cn.nukkit.command.tree.node;

import com.google.common.collect.Sets;

import java.util.HashSet;

/**
 * 验证是否为操作参数，解析对应参数为{@link String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#OPERATOR OPERATOR}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class OperatorStringNode extends StringNode {
    private static final HashSet<String> OPERATOR = Sets.newHashSet("+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");

    @Override
    public void fill(String arg) {
        if (OPERATOR.contains(arg)) this.value = arg;
        else this.error();
    }

}
