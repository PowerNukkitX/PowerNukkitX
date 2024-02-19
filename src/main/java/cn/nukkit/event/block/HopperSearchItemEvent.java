package cn.nukkit.event.block;

import cn.nukkit.block.BlockHopper;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;


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
