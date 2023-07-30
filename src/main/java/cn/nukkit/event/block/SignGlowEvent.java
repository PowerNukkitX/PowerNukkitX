package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;

public class SignGlowEvent extends BlockEvent implements Cancellable {

    private final Player player;
    private final boolean glowing;

    public SignGlowEvent(Block block, Player player, boolean glowing) {
        super(block);
        this.player = player;
        this.glowing = glowing;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGlowing() {
        return this.glowing;
    }
}
