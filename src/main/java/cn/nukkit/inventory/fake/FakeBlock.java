package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

import java.util.HashSet;
import java.util.List;

/**
 * Fake block API, which tricks the client into believing that a block and its BlockEntity exist at a specified location by sending packets
 */


public interface FakeBlock {
    /**
     * Send the fake block and its corresponding BlockEntity to a player
     *
     * @param player the player
     */
    void create(Player player);

    /**
     * Send the fake block and its corresponding BlockEntity to a player
     *
     * @param player    the player
     * @param titleName the title name for blockentity
     */
    default void create(Player player, String titleName) {
        this.create(player);
    }

    /**
     * Remove it.
     *
     * @param player the player
     */
    void remove(Player player);

    HashSet<Vector3> getLastPositions(Player player);

    default List<Vector3> getPlacePositions(Player player) {
        return List.of(getOffset(player));
    }

    /**
     * Get the place position of this fake block
     */
    default Vector3 getOffset(Player player) {
        Vector3 offset = player.getDirectionVector();
        offset.x *= -(1 + player.getWidth());
        offset.y *= -(1 + player.getHeight());
        offset.z *= -(1 + player.getWidth());
        return player.getPosition().add(offset);
    }

    /**
     * Blocks return 0, entity-based fake blocks override
     */
    default long getEntityId(Player player) {
        return 0L;
    }
}
