package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class EffectInfested extends Effect {

    private static final float SPAWN_CHANCE = 0.1f;

    public EffectInfested() {
        super(EffectType.INFESTED, "%effect.infested", new Color(140, 155, 140), true);
    }

    @Override
    public void onHurt(Entity entity, EntityDamageEvent source) {
        var random = ThreadLocalRandom.current();
        if (random.nextFloat() >= SPAWN_CHANCE) {
            return;
        }
        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            Entity silverfish = Entity.createEntity(EntityID.SILVERFISH, entity.getPosition());
            if (silverfish != null) {
                silverfish.spawnToAll();
            }
        }
    }
}
