package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

public class ItemEnchantedGoldenApple extends ItemEdible {
    public ItemEnchantedGoldenApple() {
        super(ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}