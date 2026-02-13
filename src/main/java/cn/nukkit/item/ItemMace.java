package cn.nukkit.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

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
        entity.updateFallDistance();
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

    public void onPostAttack(Entity victim, float damage) {
        if (damage >= 7) {
            Level level = victim.getLevel();
            Vector3 pos = victim.getPosition();

            level.addLevelEvent(LevelEventPacket.EVENT_PARTICLE_SMASH_ATTACK_GROUND_DUST, 0, pos);
            if (damage >= 16) {
                level.addSound(pos, Sound.MACE_HEAVY_SMASH_GROUND);
            } else {
                level.addSound(pos, Sound.MACE_SMASH_GROUND);
            }
        }
    }
}