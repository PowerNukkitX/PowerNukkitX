package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.player.Player;

public class PlayerBedLeaveEvent extends PlayerEvent {

    private final Block bed;

    public PlayerBedLeaveEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    public Block getBed() {
        return bed;
    }
}
