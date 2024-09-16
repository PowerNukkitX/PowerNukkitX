package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlayerTransferItemEvent extends PlayerEvent implements Cancellable {

    private final Player player;
    private final Type type;
    private final Item sourceItem;

    @Nullable
    private final Item destinationItem;

    private final int sourceSlot;

    private final int destinationSlot;  // -1 if not present

    private final Inventory sourceInventory;

    @Nullable
    private final Inventory destinationInventory;

    private static final HandlerList handlers = new HandlerList();


    public PlayerTransferItemEvent(
            Player player,
            Type type,
            Item sourceItem,
            @Nullable Item destinationItem,
            int sourceSlot,
            int destinationSlot,    // set this to -1 if not present
            Inventory sourceInventory,
            @Nullable Inventory destinationInventory
    ) {
        this.player = player;
        this.type = type;
        this.sourceItem = sourceItem;
        this.destinationItem = destinationItem;
        this.sourceSlot = sourceSlot;
        this.destinationSlot = destinationSlot;
        this.sourceInventory = sourceInventory;
        this.destinationInventory = destinationInventory;
    }


    public static HandlerList getHandlers() {
        return handlers;
    }


    public Player getPlayer() {
        return this.player;
    }


    public Type getType() {
        return this.type;
    }


    public Item getSourceItem() {
        return this.sourceItem;
    }


    public Optional<Item> getDestinationItem() {
        return Optional.ofNullable(this.destinationItem);
    }


    public int getSourceSlot() {
        return this.sourceSlot;
    }


    public Optional<Integer> getDestinationSlot() {
        return this.destinationSlot == -1 ? Optional.empty() : Optional.of(this.destinationSlot);
    }


    public Inventory getSourceInventory() {
        return this.sourceInventory;
    }


    public Optional<Inventory> getDestinationInventory() {
        return Optional.ofNullable(this.destinationInventory);
    }


    public enum Type {
        TRANSFER,
        SWAP,
        DROP
    }

}
