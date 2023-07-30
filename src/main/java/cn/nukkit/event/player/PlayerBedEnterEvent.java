package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;

public class PlayerBedEnterEvent extends PlayerEvent implements Cancellable {

    private final Block bed;

    public PlayerBedEnterEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    public Block getBed() {
        return bed;
    }
}
