package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.utils.EntitySelector;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 可以从玩家名或者目标选择器解析出一个{@link Player} {@link java.util.List List},不可能为空,当没有匹配时会返回一个空List<br>
 * 没有默认的对应参数类型，如果要使用需要手动指定
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class PlayersNode extends TargetNode<Player> {
    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        if (EntitySelector.hasArguments(arg)) {
            var entities = EntitySelector.matchEntities(this.parent.parent.getSender(), arg);
            this.value = entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player == null) this.value = Collections.emptyList();
            else this.value = Collections.singletonList(player);
        }
    }
}
