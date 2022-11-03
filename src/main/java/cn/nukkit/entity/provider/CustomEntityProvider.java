package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PowerNukkitXOnly
@Since("1.19.21-r2")
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
    public Entity provideEntity(@NotNull FullChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        return null;
    }

    //自定义物品只用getSimpleName当做knownEntities的Key所以不需要指定NETWORK ID
    @Override
    public int getNetworkId() {
        return Entity.NETWORK_ID;
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
