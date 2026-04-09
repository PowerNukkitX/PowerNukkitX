package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionApplicationMode;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class EntityLingeringPotion extends EntitySplashPotion {
    @Override
    @NotNull
    public String getIdentifier() {
        return LINGERING_POTION;
    }

    public EntityLingeringPotion(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    public EntityLingeringPotion(IChunk chunk, NbtMap nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setDataFlag(ActorFlags.LINGERING, true);
    }

    @Override
    protected void splash(Entity collidedWith) {
        super.splash(collidedWith);
        saveNBT();
        List<NbtMap> pos = namedTag.getList("Pos", NbtType.COMPOUND);
        EntityAreaEffectCloud entity = (EntityAreaEffectCloud) Entity.createEntity(Entity.AREA_EFFECT_CLOUD, getChunk(),
                NbtMap.builder().putList("Pos", NbtType.COMPOUND, pos)
                        .putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f))
                        .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                        .putShort("PotionId", (short) potionId)
                        .build()
        );

        List<Effect> effects = PotionType.get(potionId).getEffects(PotionApplicationMode.LINGERING);
        for (Effect effect : effects) {
            if (effect != null && entity != null) {
                entity.cloudEffects.add(effect/*.setDuration(1)*/.setVisible(false).setAmbient(false));
                entity.spawnToAll();
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Lingering Potion";
    }
}
