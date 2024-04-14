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
 * 解析为{@code List<Player>}值
 * <p>
 * 不会默认使用，需要手动指定
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
