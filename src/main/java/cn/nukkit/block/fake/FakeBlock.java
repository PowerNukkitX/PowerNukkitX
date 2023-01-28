package cn.nukkit.block.fake;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.List;

public interface FakeBlock {

    void create(Player player);

    default void create(Player player, String titleName) {
        this.create(player);
    }

    void remove(Player player);

    default List<Vector3> getPositions(Player player) {
        return Collections.singletonList(getOffset(player));
    }

    default Vector3 getOffset(Player player) {
        return player.getPosition().floor().setY(player.getLevel().getMinHeight() + 1);
    }
}
