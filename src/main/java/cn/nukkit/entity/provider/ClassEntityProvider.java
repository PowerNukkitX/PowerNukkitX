package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
@Log4j2
public class ClassEntityProvider implements EntityProvider<Entity>, EntityProviderWithClass {
    private final String name;
    private final Class<? extends Entity> clazz;
    private final int networkId;

    public ClassEntityProvider(String name, Class<? extends Entity> clazz) {
        this.name = name;
        this.clazz = clazz;
        int networkId1;
        try {
            networkId1 = clazz.getField("NETWORK_ID").getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            networkId1 = -1;
        }
        this.networkId = networkId1;
    }

    @Override
    public Entity provideEntity(@NotNull FullChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        Entity entity = null;
        List<Exception> exceptions = null;

        for (var constructor : clazz.getConstructors()) {
            if (entity != null) {
                break;
            }

            if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                continue;
            }

            try {
                if (args == null || args.length == 0) {
                    entity = (Entity) constructor.newInstance(chunk, nbt);
                } else {
                    Object[] objects = new Object[args.length + 2];

                    objects[0] = chunk;
                    objects[1] = nbt;
                    System.arraycopy(args, 0, objects, 2, args.length);
                    entity = (Entity) constructor.newInstance(objects);

                }
            } catch (Exception e) {
                if (exceptions == null) {
                    exceptions = new ArrayList<>();
                }
                exceptions.add(e);
            }

        }

        if (entity == null) {
            Exception cause = new IllegalArgumentException("Could not create an entity of type " + name, exceptions != null && exceptions.size() > 0 ? exceptions.get(0) : null);
            if (exceptions != null && exceptions.size() > 1) {
                for (int i = 1; i < exceptions.size(); i++) {
                    cause.addSuppressed(exceptions.get(i));
                }
            }
            log.debug("Could not create an entity of type {} with {} args", name, args == null ? 0 : args.length, cause);
        } else {
            return entity;
        }
        return null;
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    public Class<? extends Entity> getEntityClass() {
        return clazz;
    }
}
