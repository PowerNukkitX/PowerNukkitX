package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses a command parameter as a {@code List<Player>} value for PowerNukkitX command trees.
 * <p>
 * This node is not used by default and must be manually specified. It supports entity selectors and player name lookup,
 * returning a list of matching {@link Player} instances.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses entity selectors (e.g., @a, @p) using {@link EntitySelectorAPI}.</li>
 *   <li>Filters matched entities to {@link Player} instances only.</li>
 *   <li>Supports player name lookup as a fallback.</li>
 *   <li>Returns a list of matching players.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for player list parameter parsing when manually specified.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: @a, Steve, @p
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Player
 * @see EntitySelectorAPI
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class PlayersNode extends TargetNode<Player> {
    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            List<Entity> entities;
            List<Player> result;
            try {
                entities = EntitySelectorAPI.getAPI().matchEntities(paramList.getParamTree().getSender(), arg);
            } catch (SelectorSyntaxException exception) {
                error(exception.getMessage());
                return;
            }
            result = entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
            this.value = result;
        } else {
            this.value = Collections.singletonList(Server.getInstance().getPlayer(arg));
        }
    }
}
