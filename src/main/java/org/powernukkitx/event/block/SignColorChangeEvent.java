package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.utils.BlockColor;

public class SignColorChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final BlockColor color;

    public SignColorChangeEvent(Block block, Player player, BlockColor color) {
        super(block);
        this.player = player;
        this.color = color;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockColor getColor() {
        return this.color;
    }
}
