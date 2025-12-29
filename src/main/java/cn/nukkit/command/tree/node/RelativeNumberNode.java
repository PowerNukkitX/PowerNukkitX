package cn.nukkit.command.tree.node;

/**
 * Abstract base class for relative number parameter nodes in PowerNukkitX command trees.
 * <p>
 * Provides a contract for parsing and retrieving relative number values (e.g., ~1, ~2.5) for numeric types.
 * Subclasses must implement the logic for retrieving the value relative to a base value.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Defines an abstract method for retrieving the value relative to a base.</li>
 *   <li>Throws {@link UnsupportedOperationException} for the default get() method.</li>
 *   <li>Intended for extension by numeric parameter node types requiring relative support.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class for custom relative number parameter nodes.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class MyRelativeIntNode extends RelativeNumberNode<Integer> { ... }
 * </pre>
 *
 * @author daoge_cmd
 * @see ParamNode
 * @since PowerNukkitX 1.19.50
 */
public abstract class RelativeNumberNode<T extends Number> extends ParamNode<T> {
    @Override
    public <E> E get() {
        throw new UnsupportedOperationException();
    }

    public abstract T get(T base);
}
