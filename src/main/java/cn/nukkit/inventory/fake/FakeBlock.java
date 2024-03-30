package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

import java.util.HashSet;
import java.util.List;

/**
 * 假方块API，通过发送数据包欺骗客户端在指定位置存在某方块及其方块实体
 * <p>
 * Fake block API, which tricks the client into believing that a block and its BlockEntity exist at a specified location by sending packets
 */


public interface FakeBlock {
    /**
     * 给某玩家发送该假方块及其对应方块实体.
     * <p>
     * Send the fake block and its corresponding BlockEntity to a player
     *
     * @param player the player
     */
    void create(Player player);

    /**
     * 给某玩家发送该假方块及其对应方块实体.
     * <p>
     * Send the fake block and its corresponding BlockEntity to a player
     *
     * @param player    the player
     * @param titleName 该方块实体的名称<br>the title name for blockentity
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

    HashSet<Vector3> getLastPositions();

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
}
