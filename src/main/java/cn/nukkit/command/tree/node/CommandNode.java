package cn.nukkit.command.tree.node;

import cn.nukkit.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses all remaining command arguments as a single {@link String} value, joining them with spaces.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#COMMAND COMMAND}
 * if no custom {@link IParamNode} is specified. It validates the first argument as a known command and accumulates
 * subsequent arguments, joining them into a single string for execution.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Joins all remaining arguments with spaces to form a single command string.</li>
 *   <li>Validates the first argument as a registered command.</li>
 *   <li>Handles quoted arguments containing spaces.</li>
 *   <li>Resets state between parses.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for parsing command-type parameters.</li>
 *   <li>Automatically selected for command parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: say Hello World -> "say Hello World"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#COMMAND
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class CommandNode extends ParamNode<String> {
    private final List<String> TMP = new ArrayList<>();

    private boolean first = true;

    @Override
    public void fill(String arg) {
        if (arg.contains(" ")) {
            arg = "\"" + arg + "\"";
        }
        if (first && !Server.getInstance().getCommandMap().getCommands().containsKey(arg)) {
            this.error("commands.generic.unknown", arg);
            return;
        }
        if (this.paramList.getIndex() != this.paramList.getParamTree().getArgs().length) {
            first = false;
            TMP.add(arg);
        } else {
            TMP.add(arg);
            this.value = String.join(" ", TMP);
            first = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
