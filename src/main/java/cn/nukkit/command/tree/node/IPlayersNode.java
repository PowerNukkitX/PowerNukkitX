package cn.nukkit.command.tree.node;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 解析为{@code List<IPlayer>}值
 * <p>
 * 不会默认使用，需要手动指定
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class IPlayersNode extends ParamNode<List<IPlayer>> {
    @Override
    public void fill(String arg) {
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            List<Entity> entities = null;
            try {
                entities = EntitySelectorAPI.getAPI().matchEntities(this.parent.parent.getSender(), arg);
            } catch (SelectorSyntaxException exception) {
                error(exception.getMessage());
                return;
            }
            if (!entities.isEmpty())
                this.value = entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
            else error("commands.generic.noTargetMatch");
        } else {
            IPlayer player = Server.getInstance().getOfflinePlayer(arg);
            if (player != null) {
                this.value = Collections.singletonList(player);
            } else error("commands.generic.player.notFound");
        }
    }
}
