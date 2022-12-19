package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;

import java.util.Collections;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntitiesNode extends TargetNode<Entity> {

    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (EntitySelector.hasArguments(arg)) {
            var result = EntitySelector.matchEntities(this.parent.parent.getSender(), arg);
            if (result.isEmpty()) throw new CommandSyntaxException();
            this.value = result;
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player == null) throw new CommandSyntaxException();
            else this.value = Collections.singletonList(player);
        }
    }
}
