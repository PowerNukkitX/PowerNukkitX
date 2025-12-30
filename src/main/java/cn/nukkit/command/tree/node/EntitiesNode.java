package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Parses a command parameter as a {@code List<Entity>} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#TARGET TARGET}
 * if no custom {@link IParamNode} is specified. It supports entity selectors, player names, and will be extended to support UUID or xuid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses entity selectors (e.g., @a, @e[type=player], etc.) using {@link EntitySelectorAPI}.</li>
 *   <li>Supports direct player name lookup as a fallback.</li>
 *   <li>Returns a list of matched entities or triggers an error if invalid.</li>
 *   <li>TODO: Add support for UUID or xuid.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for entity list parameter parsing.</li>
 *   <li>Automatically selected for target parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: @a, Steve, @e[type=zombie]
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#TARGET
 * @see EntitySelectorAPI
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class EntitiesNode extends TargetNode<Entity> {

    // TODO: Support UUID or xuid
    @Override
    public void fill(String arg) {
        List<Entity> entities;
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            try {
                entities = EntitySelectorAPI.getAPI().matchEntities(paramList.getParamTree().getSender(), arg);
            } catch (SelectorSyntaxException exception) {
                error(exception.getMessage());
                return;
            }
            this.value = entities;
        } else {
            entities = Lists.newArrayList();
            Player player = Server.getInstance().getPlayer(arg);
            if (player != null) {
                entities.add(player);
            }
            this.value = entities;
        }
    }
}
