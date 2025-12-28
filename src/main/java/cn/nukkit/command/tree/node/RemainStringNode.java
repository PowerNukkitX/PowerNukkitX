package cn.nukkit.command.tree.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses all remaining command arguments as a single {@code String} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#JSON JSON}
 * if no custom {@link IParamNode} is specified. It joins all remaining arguments into a single string value.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Joins all remaining arguments into a single string value.</li>
 *   <li>Resets state between parses.</li>
 *   <li>Used as the default node for JSON-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for JSON parameter parsing.</li>
 *   <li>Automatically selected for JSON parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: '{"key":"value"}' as a String
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#JSON
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 *//**
 * 解析全部剩余参数拼接为{@code String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#JSON JSON}的
 * 如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class RemainStringNode extends ParamNode<String> {
    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (this.paramList.getIndex() != paramList.getParamTree().getArgs().length) TMP.add(arg);
        else {
            TMP.add(arg);
            this.value = String.join("", TMP);
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
