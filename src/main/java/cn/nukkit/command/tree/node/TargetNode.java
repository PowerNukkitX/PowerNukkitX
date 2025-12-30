package cn.nukkit.command.tree.node;

import java.util.List;

/**
 * Abstract base class for target parameter nodes in PowerNukkitX command trees.
 * <p>
 * Represents a node that parses a list of target values (e.g., entities, players) for command parameters.
 * Subclasses should specify the type of target and implement the parsing logic.
 *
 * @param <T> the type of target (e.g., Entity, Player)
 * @author PowerNukkitX Project Team
 * @see ParamNode
 * @since PowerNukkitX 1.19.50
 */
public abstract class TargetNode<T> extends ParamNode<List<T>> {
}
