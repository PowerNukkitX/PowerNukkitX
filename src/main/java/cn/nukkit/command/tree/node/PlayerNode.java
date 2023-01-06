package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class PlayerNode extends ParamNode<Player> {
    @Override
    public void fill(String arg) {
        Player player = Server.getInstance().getPlayerExact(arg);
        if (player == null) this.error();
        else this.value = player;
    }
}
