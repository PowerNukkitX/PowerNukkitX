package cn.nukkit.command.tree.node;

import cn.nukkit.command.utils.RawText;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import com.google.gson.JsonSyntaxException;


/**
 * Parses a command parameter as a {@link RawText} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#RAWTEXT RAWTEXT}
 * if no custom {@link IParamNode} is specified. It attempts to parse the argument as RawText, handling JSON syntax errors
 * and providing detailed error messages with context.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses the argument as RawText using {@link RawText#fromRawText(String)}.</li>
 *   <li>Handles JSON syntax errors and provides detailed error messages with context.</li>
 *   <li>Used as the default node for rawtext-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for rawtext parameter parsing.</li>
 *   <li>Automatically selected for rawtext parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: '{"rawtext":[{"text":"Hello"}]}' as RawText
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see RawText
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class RawTextNode extends ParamNode<RawText> {

    @Override
    public void fill(String arg) {
        try {
            this.value = RawText.fromRawText(arg);
        } catch (JsonSyntaxException e) {
            int index;
            String s = e.getMessage();
            s = s.substring(s.indexOf("column") + 7, s.indexOf("path") - 1);
            try {
                index = Integer.parseInt(s);
            } catch (NumberFormatException ignore) {
                this.error();
                return;
            }
            if (index == arg.length() + 1) {
                this.error(new CommandOutputMessage("JSON parsing error:"),
                        new CommandOutputMessage(arg.substring(0, arg.length() - 1) + "" + arg.substring(arg.length() - 1) + "§f<<"));
                return;
            } else if (index == 1) {
                this.error(new CommandOutputMessage("JSON parsing error:"),
                        new CommandOutputMessage("§f>>§c" + arg.charAt(0) + arg.substring(1)));
                return;
            }
            index -= 2;
            this.error(new CommandOutputMessage("JSON parsing error:"),
                    new CommandOutputMessage(arg.substring(0, index) + "§f<<§c" + arg.charAt(index) + arg.substring(index + 1, arg.length() - 1)));
        }
    }
}
