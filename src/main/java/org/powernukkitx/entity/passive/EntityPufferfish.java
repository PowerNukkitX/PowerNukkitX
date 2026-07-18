package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityPufferfish extends EntityFish {

    @Override
    @NotNull public String getIdentifier() {
        return PUFFERFISH;
    }

    public EntityPufferfish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Pufferfish";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("pufferfish", "fish");
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(3);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.13f);
    }
}
