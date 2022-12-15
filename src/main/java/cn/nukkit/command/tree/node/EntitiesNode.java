package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;

import java.util.Collections;

public class EntitiesNode extends TargetNode<Entity> {

    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (EntitySelector.hasArguments(arg)) {
            var result = EntitySelector.matchEntities(this.parent.getSender(), arg);
            if (result.isEmpty()) throw new CommandSyntaxException();
            this.value = result;
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player == null) throw new CommandSyntaxException();
            else this.value = Collections.singletonList(player);
        }
    }
}
