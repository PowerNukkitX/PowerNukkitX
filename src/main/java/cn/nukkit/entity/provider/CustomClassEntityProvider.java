package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
@Log4j2
public class CustomClassEntityProvider extends CustomEntityProvider implements EntityProviderWithClass {
    private final Class<? extends Entity> clazz;

    /**
     * 调用此方法注册自定义实体，请一定要在自定义实体类中，存在一个名为 def 或者 DEF(推荐) 的{@link CustomEntityDefinition}静态常量
     *
     * @param customEntityClass the custom entity class
     */
    public CustomClassEntityProvider(Class<? extends Entity> customEntityClass) {
        super(null);
        this.clazz = customEntityClass;
        Field def;
        try {
            def = this.clazz.getDeclaredField("DEF");
            def.setAccessible(true);
            this.customEntityDefinition = (CustomEntityDefinition) def.get(null);
        } catch (NoSuchFieldException e) {
            try {
                def = this.clazz.getDeclaredField("def");
            } catch (NoSuchFieldException ex) {
                log.error("Cannot find the static final 'DEF' or 'def' for this custom entity:" + customEntityClass.getCanonicalName());
                throw new RuntimeException(ex);
            }
            try {
                def.setAccessible(true);
                this.customEntityDefinition = (CustomEntityDefinition) def.get(null);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomClassEntityProvider(CustomEntityDefinition customEntityDefinition, Class<? extends Entity> customEntityClass) {
        super(customEntityDefinition);
        this.clazz = customEntityClass;
    }

    public Class<? extends Entity> getEntityClass() {
        return clazz;
    }

    //TODO 直接Copy普通实体的提供者,或许这里可以实现一些自定义实体相关的高级功能?
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
            Exception cause = new IllegalArgumentException("Could not create an custom entity of type " + getName(), exceptions != null && exceptions.size() > 0 ? exceptions.get(0) : null);
            if (exceptions != null && exceptions.size() > 1) {
                for (int i = 1; i < exceptions.size(); i++) {
                    cause.addSuppressed(exceptions.get(i));
                }
            }
            log.debug("Could not create an custom entity of type {} with {} args", getName(), args == null ? 0 : args.length, cause);
        } else {
            return entity;
        }
        return null;
    }

    @NotNull
    @Override
    public String getSimpleName() {
        return this.clazz.getSimpleName();
    }
}
