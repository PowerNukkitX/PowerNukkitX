package cn.nukkit.command.tree.node;

/**
 * Parses a command parameter as a relative {@link Float} value for PowerNukkitX command trees.
 * <p>
 * This node extends {@link RelativeNumberNode} for float values, supporting both absolute and relative (~) notation.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses float arguments, supporting both absolute and relative (~) notation.</li>
 *   <li>Returns the value relative to a base float if specified.</li>
 *   <li>Used for float parameters requiring relative support.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for relative float parameter parsing.</li>
 *   <li>Extend for custom relative float parameter types.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "~1.5" as 1.5f relative, "2.0" as absolute
 * </pre>
 *
 * @author daoge_cmd
 * @see RelativeNumberNode
 * @since PowerNukkitX 1.19.50
 */
public class RelativeFloatNode extends RelativeNumberNode<Float> {
    @Override
    public void fill(String arg) {
        if (arg.startsWith("~")) {
            if (arg.length() == 1) {
                this.value = 0f;
            } else {
                try {
                    this.value = Float.parseFloat(arg.substring(1));
                } catch (NumberFormatException e) {
                    this.error();
                }
            }
        } else {
            try {
                this.value = Float.parseFloat(arg);
            } catch (NumberFormatException e) {
                this.error();
            }
        }
    }

    @Override
    public Float get(Float base) {
        return base + this.value;
    }
}
