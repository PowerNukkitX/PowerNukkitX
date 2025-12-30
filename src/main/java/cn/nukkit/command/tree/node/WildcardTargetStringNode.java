package cn.nukkit.command.tree.node;


/**
 * Parses a command parameter as a {@link String} value for wildcard target parameters in PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#WILDCARD_TARGET WILDCARD_TARGET}
 * if no custom {@link IParamNode} is specified. It simply sets the argument as the node value and does not perform validation.
 *
 * @author PowerNukkitX Project Team
 * @see StringNode
 * @since PowerNukkitX 1.19.50
 * <p>
 * Parsed as {@link String} value
 * <p>
 * All command parameters are of type {@link cn.nukkit.command.data.CommandParamType#WILDCARD_TARGET WILDCARD_TARGET}. If no {@link IParamNode} is manually specified, this parser is used by default.
 */
public class WildcardTargetStringNode extends StringNode {

    @Override
    public void fill(String arg) {
        //WILDCARD_TARGET不可能解析错误
        this.value = arg;
    }

}
