package cn.nukkit.command.tree.node;

import java.util.regex.Pattern;

/**
 * Parses and validates integer position arguments as {@link cn.nukkit.level.Position} values for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#BLOCK_POSITION BLOCK_POSITION}
 * if no custom {@link IParamNode} is specified. It uses a regex pattern to validate and parse integer coordinates,
 * supporting absolute, relative (~), and local (^) notation.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates and parses integer position arguments using a regex pattern.</li>
 *   <li>Supports absolute, relative (~), and local (^) coordinate notation.</li>
 *   <li>Used as the default node for block position-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for integer position parameter parsing.</li>
 *   <li>Automatically selected for block position parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "~1", "^2", "100"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.level.Position
 * @see cn.nukkit.command.data.CommandParamType#BLOCK_POSITION
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class IntPositionNode extends PositionNode {
    private static final Pattern INT_POS_PATTERN = Pattern.compile("[~^]?([-+]?\\d+)|[~^]");

    public IntPositionNode() {
        super(INT_POS_PATTERN);
    }

}
