package cn.nukkit.level.format.powerWorld.worldData;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;

import java.nio.ByteBuffer;

@PowerNukkitXOnly
@Since("1.19.20-r3")
public record OpenedDB(@NotNull Env<ByteBuffer> env, @NotNull Dbi<ByteBuffer> dbi) {
}
