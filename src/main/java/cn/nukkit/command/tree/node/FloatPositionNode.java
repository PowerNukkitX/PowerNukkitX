package cn.nukkit.command.tree.node;

import java.util.regex.Pattern;

/**
 * Parses and validates floating-point position arguments as {@link cn.nukkit.level.Position} values for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#POSITION POSITION}
 * if no custom {@link IParamNode} is specified. It uses a regex pattern to validate and parse floating-point coordinates,
 * supporting absolute, relative (~), and local (^) notation.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates and parses floating-point position arguments using a regex pattern.</li>
 *   <li>Supports absolute, relative (~), and local (^) coordinate notation.</li>
 *   <li>Used as the default node for position-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for floating-point position parameter parsing.</li>
 *   <li>Automatically selected for position parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "~1.5", "^2.0", "100.0"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.level.Position
 * @see cn.nukkit.command.data.CommandParamType#POSITION
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class FloatPositionNode extends PositionNode {
    private static final Pattern FLOAT_POS_PATTERN = Pattern.compile("[~^]?[-+]?\\d+(?:\\.\\d+)?|[~^]");

    public FloatPositionNode() {
        super(FLOAT_POS_PATTERN);
    }

}
