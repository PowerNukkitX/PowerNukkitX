package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.Collections;

/**
 * 解析为{@code List<Entity>}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#TARGET TARGET}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntitiesNode extends TargetNode<Entity> {

    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelectorAPI.getAPI().checkValid(arg)) {
            var entities = EntitySelectorAPI.getAPI().matchEntities(this.parent.parent.getSender(), arg);
            if (entities != null) this.value = entities;
            else error("commands.generic.noTargetMatch");
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player != null) this.value = Collections.singletonList(player);
            else error("commands.generic.player.notFound");
        }
    }
}
