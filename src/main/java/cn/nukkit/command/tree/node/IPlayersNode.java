package cn.nukkit.command.tree.node;

import cn.nukkit.IPlayer;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Parses a command parameter as a {@code List<IPlayer>} value for PowerNukkitX command trees.
 * <p>
 * This node is not used by default and must be manually specified. It supports entity selectors and offline player lookup,
 * returning a list of matching {@link IPlayer} instances or triggering an error if no match is found.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses entity selectors (e.g., @a, @p) using {@link EntitySelectorAPI}.</li>
 *   <li>Filters matched entities to {@link IPlayer} instances only.</li>
 *   <li>Supports offline player lookup by name as a fallback.</li>
 *   <li>Returns a list of matching players or triggers an error if no match is found.</li>
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
 * @see IPlayer
 * @see EntitySelectorAPI
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */


public class IPlayersNode extends ParamNode<List<IPlayer>> {
    @Override
    public void fill(String arg) {
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            List<Entity> entities;
            List<IPlayer> result;
            try {
                entities = EntitySelectorAPI.getAPI().matchEntities(paramList.getParamTree().getSender(), arg);
            } catch (SelectorSyntaxException exception) {
                error(exception.getMessage());
                return;
            }
            result = entities.stream().filter(entity -> entity instanceof IPlayer).map(entity -> (IPlayer) entity).collect(Collectors.toList());
            if (!result.isEmpty())
                this.value = result;
            else error("commands.generic.noTargetMatch");
        } else {
            IPlayer player = Server.getInstance().getOfflinePlayer(arg);
            if (player != null) {
                this.value = Collections.singletonList(player);
            } else error("commands.generic.player.notFound");
        }
    }
}
