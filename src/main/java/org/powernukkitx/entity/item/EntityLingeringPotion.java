package org.powernukkitx.entity.item;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.PotionApplicationMode;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class EntityLingeringPotion extends EntitySplashPotion {
    @Override
    @NotNull
    public String getIdentifier() {
        return LINGERING_POTION;
    }

    public EntityLingeringPotion(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntityLingeringPotion(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
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
        ListTag<?> pos = (ListTag<?>) nbt.getList("Pos", CompoundTag.class).copy();
        EntityAreaEffectCloud entity = (EntityAreaEffectCloud) Entity.createEntity(Entity.AREA_EFFECT_CLOUD, getChunk(),
                new CompoundTag().putList("Pos", pos)
                        .putList("Rotation", new ListTag<>()
                                .add(new FloatTag(0))
                                .add(new FloatTag(0))
                        )
                        .putList("Motion", new ListTag<>()
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0))
                        )
                        .putShort("PotionId", potionId)
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