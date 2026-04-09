package cn.nukkit.entity.passive;

import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
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

    public EntityPufferfish(IChunk chunk, NbtMap nbt) {
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
