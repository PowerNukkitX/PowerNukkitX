package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.EntitySelector;

import java.util.Collections;
import java.util.stream.Collectors;

public class PlayersNode extends TargetNode<Player> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (EntitySelector.hasArguments(arg)) {
            var entities = EntitySelector.matchEntities(this.parent.getSender(), arg);
            var result = entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
            if (result.isEmpty()) throw new CommandSyntaxException();
            this.value = result;
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player == null) throw new CommandSyntaxException();
            else this.value = Collections.singletonList(player);
        }
    }
}
