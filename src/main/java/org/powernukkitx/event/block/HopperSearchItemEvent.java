package org.powernukkitx.event.block;

import org.powernukkitx.block.BlockHopper;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;


public class HopperSearchItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockHopper.IHopper hopper;
    private final boolean isMinecart;

    public HopperSearchItemEvent(BlockHopper.IHopper hopper, boolean isMinecart) {
        this.hopper = hopper;
        this.isMinecart = isMinecart;
    }

    public BlockHopper.IHopper getHopper() {
        return hopper;
    }

    public boolean isMinecart() {
        return isMinecart;
    }
}
