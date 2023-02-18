package cn.nukkit.block.fake;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.List;

/**
 * 假方块API，通过发送数据包欺骗客户端在指定位置存在某方块及其方块实体
 * <p>
 * Fake block API, which tricks the client into believing that a block and its BlockEntity exist at a specified location by sending packets
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
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
     * @param titleName 该方块实体的名称<br>the title name
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

    default List<Vector3> getPositions(Player player) {
        return Collections.singletonList(getOffset(player));
    }

    default Vector3 getOffset(Player player) {
        return player.getPosition().floor().setY(player.getLevel().getMinHeight() + 1);
    }
}
