package org.powernukkitx.entity.ai.executor.enderdragon;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.PotionApplicationMode;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.entity.item.EntityAreaEffectCloud;
import org.powernukkitx.level.Location;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;


public class PerchingExecutor implements EntityControl, IBehaviorExecutor {

    private int stayTick = -1;

    public PerchingExecutor() {
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Vector3 target = new Vector3(0, entity.getLevel().getHighestBlockAt(0, 0) + 1, 0);
        if (stayTick >= 0) {
            stayTick++;
        }
        if (entity.distance(target) <= 1) {
            if (stayTick == -1) stayTick = 0;
            if (stayTick == 25) {
                entity.getViewers().values().stream().filter(player -> player.distance(new Vector3(0, 64, 0)) <= 20).findAny().ifPresent(player -> {
                    removeRouteTarget(entity);
                    setLookTarget(entity, player);
                    Vector3 toPlayerVector = new Vector3(player.x - entity.x, player.y - entity.y, player.z - entity.z).normalize();
                    Location location = entity.getLocation().add(toPlayerVector.multiply(10));
                    location.y = location.level.getHighestBlockAt(location.toHorizontal()) + 1;
                    EntityAreaEffectCloud areaEffectCloud = (EntityAreaEffectCloud) Entity.createEntity(Entity.AREA_EFFECT_CLOUD, location.getChunk(),
                            new CompoundTag().putList("Pos", new ListTag<>()
                                            .add(new DoubleTag(location.x))
                                            .add(new DoubleTag(location.y))
                                            .add(new DoubleTag(location.z))
                                    )
                                    .putList("Rotation", new ListTag<>()
                                            .add(new FloatTag(0))
                                            .add(new FloatTag(0))
                                    )
                                    .putList("Motion", new ListTag<>()
                                            .add(new DoubleTag(0))
                                            .add(new DoubleTag(0))
                                            .add(new DoubleTag(0))
                                    )
                                    .putInt("Duration", 60)
                                    .putFloat("InitialRadius", 6)
                                    .putFloat("Radius", 6)
                                    .putFloat("Height", 1)
                                    .putFloat("RadiusChangeOnPickup", 0)
                                    .putFloat("RadiusPerTick", 0)
                    );

                    List<Effect> effects = PotionType.get(PotionType.HARMING.id()).getEffects(PotionApplicationMode.DRINK);
                    for (Effect effect : effects) {
                        if (effect != null && areaEffectCloud != null) {
                            areaEffectCloud.cloudEffects.add(effect.setVisible(false).setAmbient(false));
                            areaEffectCloud.spawnToAll();
                        }
                    }
                    areaEffectCloud.spawnToAll();
                });
            }
        } else {
            setRouteTarget(entity, target);
            setLookTarget(entity, target);
        }
        if (stayTick > 100) {
            return false;
        } else if (stayTick >= 0) {
            entity.teleport(target);
        }
        return true;
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
        if (player == null) return;
        setLookTarget(entity, player);
        setRouteTarget(entity, player);
        stayTick = -1;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_PERCHING, false);
        entity.setEnablePitch(false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

}
