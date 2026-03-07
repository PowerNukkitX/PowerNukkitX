package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityTraderLlama extends EntityCreature implements IEntityNPC {
    @Override
    @NotNull public String getIdentifier() {
        return TRADER_LLAMA;
    }

    public EntityTraderLlama(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isAgeable() {
        return true;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Wandering Trader";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("trader_llama", "llama", "mob");
    }
}
