package cn.nukkit.command.tree.node;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;


/**
 * Represents a node for parsing chained subcommands in the context of the {@link cn.nukkit.command.defaults.ExecuteCommand}.
 * <p>
 * This node is used to parse and build chained command arguments (e.g., "execute as at run ...") for advanced command execution.
 * It validates each segment against allowed chained keywords and accumulates them, joining the final result as a single command string.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates and accumulates chained command keywords (e.g., run, as, at, positioned, etc.).</li>
 *   <li>Handles quoted arguments containing spaces.</li>
 *   <li>Builds the final command string for execution.</li>
 *   <li>Resets state between parses.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for parsing chained subcommands in execute-like commands.</li>
 *   <li>Automatically selected for chained command parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: execute as Steve at ~ ~ ~ run say Hello
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.defaults.ExecuteCommand
 * @since PowerNukkitX 1.19.50
 */


public class ChainedCommandNode extends EnumNode {
    private static final HashSet<String> CHAINED = Sets.newHashSet("run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");
    private boolean remain = false;

    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (arg.contains(" ")) {
            arg = "\"" + arg + "\"";
        }
        if (!remain) {
            if (!CHAINED.contains(arg)) {
                this.error();
                return;
            }
            TMP.add(arg);
            remain = true;
        } else {
            if (this.paramList.getIndex() != this.paramList.getParamTree().getArgs().length) TMP.add(arg);
            else {
                TMP.add(arg);
                var join = new StringJoiner(" ", "execute ", "");
                TMP.forEach(join::add);
                this.value = join.toString();
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
        this.remain = false;
    }
}
