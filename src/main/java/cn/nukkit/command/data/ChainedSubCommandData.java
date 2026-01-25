package cn.nukkit.command.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

/**
 * Represents a chained sub-command structure for command systems.
 * <p>
 * This class is used to model a sub-command that can have multiple associated value pairs, allowing
 * for flexible command chaining and argument mapping. Each instance holds a name and a list of value pairs.
 * <p>
 * Features:
 * <ul>
 *   <li>Stores the name of the chained sub-command.</li>
 *   <li>Maintains a list of {@link Value} pairs for argument mapping.</li>
 *   <li>Uses {@link ObjectArrayList} for efficient value storage.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a name to represent a sub-command.</li>
 *   <li>Add {@link Value} pairs to the {@link #values} list for argument mapping.</li>
 *   <li>Use in command parsing and execution systems to support complex command chains.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * ChainedSubCommandData data = new ChainedSubCommandData("subcmd");
 * data.getValues().add(new ChainedSubCommandData.Value("arg1", "arg2"));
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */
@Data
public class ChainedSubCommandData {
    /**
     * The name of the chained sub-command.
     */
    private final String name;
    /**
     * The list of value pairs associated with this sub-command.
     */
    private final List<Value> values = new ObjectArrayList<>();

    /**
     * Represents a value pair for argument mapping in chained sub-commands.
     * <p>
     * Each Value holds two strings, typically representing argument names or values.
     * <p>
     * Usage:
     * <ul>
     *   <li>Instantiate with two strings to represent a mapping.</li>
     *   <li>Use in the {@link ChainedSubCommandData#values} list.</li>
     * </ul>
     * <p>
     * Example:
     * <pre>
     * new ChainedSubCommandData.Value("firstArg", "secondArg");
     * </pre>
     *
     * @param first  The first value in the pair (e.g., argument name or value).
     * @param second The second value in the pair (e.g., argument name or value).
     * @author PowerNukkitX Project Team
     * @since PowerNukkitX 2.0.0
     */
        public record Value(String first, String second) {
    }
}
