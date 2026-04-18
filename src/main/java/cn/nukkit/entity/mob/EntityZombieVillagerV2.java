package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityZombieVillagerV2 extends EntityMob implements EntityWalkable, EntitySmite {
    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_VILLAGER_V2;
    }
    

    public EntityZombieVillagerV2(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
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
        float ageMovement = this.isBaby() ? 0.35f : 0.23f;
        return MovementComponent.value(ageMovement);
    }

    @Override
    public String getOriginalName() {
        return "Zombie VillagerV2";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie", "zombie_villager", "undead", "monster", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
