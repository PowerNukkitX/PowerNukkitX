package org.powernukkitx.item;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

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

            level.addLevelEvent(LevelEvent.PARTICLE_SMASH_ATTACK_GROUND_DUST, 0, pos);
            if (damage >= 16) {
                level.addSound(pos, Sound.MACE_HEAVY_SMASH_GROUND);
            } else {
                level.addSound(pos, Sound.MACE_SMASH_GROUND);
            }
        }
    }
}