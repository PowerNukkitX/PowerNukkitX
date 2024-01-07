package cn.nukkit.inventory;

import cn.nukkit.Player;

/**
 * @author CreeperFace
 */
public class PlayerCursorInventory extends BaseInventory {
    public PlayerCursorInventory(Player player) {
        super(player, InventoryType.CURSOR);
    }

    /**
     * This override is here for documentation and code completion purposes only.
     *
     * @return Player
     */
    @Override
    public Player getHolder() {
        return (Player) holder;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.CURSOR;
    }
}
