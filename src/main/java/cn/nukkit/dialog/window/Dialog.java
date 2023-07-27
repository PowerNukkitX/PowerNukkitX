package cn.nukkit.dialog.window;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.player.Player;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface Dialog {
    public void send(Player player);
}
