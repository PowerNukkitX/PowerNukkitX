package cn.nukkit.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.NukkitMath;

public class ItemMace extends Item implements ItemDurable {
    public ItemMace() {
        super(MACE);
    }

    @Override
    public int getMaxDurability() {
        return 501;
    }

    @Override
    public int getAttackDamage(Entity entity) {
        int height = NukkitMath.floorDouble(entity.highestPosition - entity.y);
        if(height < 1.5f) return 6;
        int damage = 0;
        for(int i = 0; i <= height; i++) {
            if(i < 3) damage+=4;
            else if(i < 8) damage+=2;
            else damage++;
        }

        entity.resetFallDistance();
        return damage;
    }
}