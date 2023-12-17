package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class CustomEntityProvider implements EntityProvider<Entity> {
    private CustomEntityDefinition customEntityDefinition;

    public CustomEntityProvider(CustomEntityDefinition customEntityDefinition) {
        this.customEntityDefinition = customEntityDefinition;
    }

    public CustomEntityDefinition getCustomEntityDefinition() {
        return customEntityDefinition;
    }

    public void setCustomEntityDefinition(CustomEntityDefinition customEntityDefinition) {
        this.customEntityDefinition = customEntityDefinition;
    }

    @Override
    public Entity provideEntity(@NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        return null;
    }

    @Override
    public int getNetworkId() {
        return customEntityDefinition.getRuntimeId();
    }

    @NotNull
    @Override
    public String getName() {
        return customEntityDefinition.getStringId();
    }

    @NotNull
    @Override
    public String getSimpleName() {
        return customEntityDefinition.getStringId();
    }
}
