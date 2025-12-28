package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.selector.args.ISelectorArgument;

/**
 * Abstract base class for coordinate selector arguments (e.g., x, y, z) in PowerNukkitX.
 * <p>
 * This class provides a foundation for implementing selector arguments that represent coordinates in Minecraft selectors.
 * Typical subclasses include arguments for the x, y, and z coordinates used in entity selection (e.g., @e[x=100,y=64,z=200]).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Defines a common priority for all coordinate arguments (priority = 1).</li>
 *   <li>Implements the {@link cn.nukkit.command.selector.args.ISelectorArgument} interface for integration with the selector system.</li>
 *   <li>Intended to be subclassed for specific coordinate argument implementations.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class to implement a coordinate argument (e.g., X, Y, or Z).</li>
 *   <li>Override required methods from {@link cn.nukkit.command.selector.args.ISelectorArgument} to provide argument-specific logic.</li>
 *   <li>Register the subclass with the selector API to enable coordinate-based filtering.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class X extends CoordinateArgument {
 *     @Override
 *     public String getKeyName() { return "x"; }
 *     // Implement predicate/filter logic...
 * }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.selector.args.ISelectorArgument
 * @see cn.nukkit.command.selector.EntitySelectorAPI
 * @since PowerNukkitX 2.0.0
 */


public abstract class CoordinateArgument implements ISelectorArgument {

    @Override
    public int getPriority() {
        return 1;
    }
}
