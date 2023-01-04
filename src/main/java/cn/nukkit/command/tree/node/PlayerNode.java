package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;

public class PlayerNode extends ParamNode<Player> {
    @Override
    public void fill(String arg) {
        Player player = Server.getInstance().getPlayerExact(arg);
        if (player == null) this.error();
        else this.value = player;
    }
}
