package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.permission.Permissible;

import java.util.HashSet;
import java.util.Set;

public class PlayerDuplicatedLoginEvent extends PlayerEvent implements Cancellable {
    public PlayerDuplicatedLoginEvent(Player player) {
        this.player = player;
    }
}
