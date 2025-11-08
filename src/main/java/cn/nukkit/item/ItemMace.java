package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

public class ItemMace extends ItemTool {
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

        if (damage >= 7) {
            Vector3 pos = entity.getPosition();

            int x = NukkitMath.floorDouble(pos.x);
            int y = NukkitMath.floorDouble(pos.y) - 1;
            int z = NukkitMath.floorDouble(pos.z);

            Block underBlock = entity.getLevel().getBlock(x, y, z);
            while (underBlock.getId().equals(Block.AIR)) {
                y--;
                underBlock = entity.getLevel().getBlock(x, y, z);
            }

            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    Vector3 particlePos = pos.add(0.5 + ox, 0.1, 0.5 + oz);
                    entity.getLevel().addParticle(new DestroyBlockParticle(particlePos, underBlock));
                }
            }

            if (damage >= 16) {
                entity.getLevel().addSound(pos, Sound.MACE_HEAVY_SMASH_GROUND);
            } else {
                entity.getLevel().addSound(pos, Sound.MACE_SMASH_GROUND);
            }
        }
        return damage;
    }
}