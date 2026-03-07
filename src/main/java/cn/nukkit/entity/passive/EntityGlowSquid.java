package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityGlowSquid extends EntityAnimal implements EntitySwimmable {

    @Override
    @NotNull public String getIdentifier() {
        return GLOW_SQUID;
    }

    public EntityGlowSquid(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public float getWidth() {
        return 0.475f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.2f);
    }

    @Override
    public String getOriginalName() {
        return "GlowSquid";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("squid", "mob");
    }
}
