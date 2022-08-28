package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 实体创建的时候会被调用
 */
@PowerNukkitXOnly
@Since("1.19.21-r2")
public interface EntityProvider<T extends Entity> {
    T provideEntity(@Nonnull FullChunk chunk, @Nonnull CompoundTag nbt, @Nullable Object... args);

    int getNetworkId();

    @NotNull String getName();

    @NotNull String getSimpleName();
}
