package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

public class ItemGoldenApple extends ItemEdible {
    public ItemGoldenApple() {
        super(GOLDEN_APPLE);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}