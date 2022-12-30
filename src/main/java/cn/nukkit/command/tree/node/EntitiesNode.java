package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;

import java.util.Collections;

/**
 * 可以从玩家名或者目标选择器解析出一个{@link Entity} {@link java.util.List List},不可能为空，当为空时会抛出{@link CommandSyntaxException}<br>
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#TARGET TARGET}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntitiesNode extends TargetNode<Entity> {

    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        if (EntitySelector.hasArguments(arg)) {
            var result = EntitySelector.matchEntities(this.parent.parent.getSender(), arg);
            if (result.isEmpty()) this.parent.error();
            this.value = result;
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player == null) this.parent.error();
            else this.value = Collections.singletonList(player);
        }
    }
}
