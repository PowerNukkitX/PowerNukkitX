package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;

/**
 * Parses all remaining command arguments as a single {@code String} value for PowerNukkitX command trees, supporting entity selector replacement.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#MESSAGE MESSAGE}
 * if no custom {@link IParamNode} is specified. It joins all remaining arguments, replaces valid entity selectors with player names,
 * and sets the result as the node value.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Joins all remaining arguments with spaces to form a message string.</li>
 *   <li>Replaces valid entity selectors with a comma-separated list of player/entity names.</li>
 *   <li>Handles selector syntax errors and highlights them in the output.</li>
 *   <li>Resets state between parses.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for message parameter parsing.</li>
 *   <li>Automatically selected for message parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: say @a Hello -> "Steve, Alex Hello"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#MESSAGE
 * @see EntitySelectorAPI
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class MessageStringNode extends ParamNode<String> {

    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (this.paramList.getIndex() != paramList.getParamTree().getArgs().length) TMP.add(arg);
        else {
            TMP.add(arg);

            String str = String.join(" ", TMP);
            Matcher match = EntitySelectorAPI.ENTITY_SELECTOR.matcher(str);
            this.value = match.replaceAll(r -> {
                int start = Math.max(0, match.start() - 1);
                int end = Math.min(str.length(), match.end());
                if (start != 0) {
                    char before = str.charAt(start);
                    if (before == '”' || before == '\'' || before == '\\' || before == ';') return match.group();
                }
                if (end != str.length()) {
                    char after = str.charAt(end);
                    if (after == '”' || after == '\'' || after == '\\' || after == ';') return match.group();
                }
                String m = match.group();
                if (EntitySelectorAPI.getAPI().checkValid(m)) {
                    StringJoiner join = new StringJoiner(", ");
                    try {
                        for (Entity entity : EntitySelectorAPI.getAPI().matchEntities(paramList.getParamTree().getSender(), m)) {
                            String name = entity.getName();
                            if (name.isBlank()) name = entity.getOriginalName();
                            join.add(name);
                        }
                    } catch (SelectorSyntaxException e) {
                        error(e.getMessage());
                    }
                    return join.toString();
                } else {
                    return m;
                }
            });
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
