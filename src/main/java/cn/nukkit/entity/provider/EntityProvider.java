package cn.nukkit.entity.provider;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * 实体创建的时候会被调用
 */


public interface EntityProvider<T extends Entity> {
    T provideEntity(@NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args);

    int getNetworkId();

    @NotNull
    String getName();

    @NotNull
    String getSimpleName();
}
