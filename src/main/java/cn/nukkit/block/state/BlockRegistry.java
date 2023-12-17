package cn.nukkit.block.state;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
public final class BlockRegistry {
    private static final Object2ObjectOpenHashMap<String, BlockProperties> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends Block>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();

    public static void init() {
        //todo more block register
    }

    public static void trim() {
        REGISTRY.trim();
        CACHE_CONSTRUCTORS.trim();
    }

    @NotNull
    public static Block get(String identifier) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
        try {
            return (Block) constructor.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(String identifier, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke();
            b.x = x;
            b.y = y;
            b.z = z;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(String identifier, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke();
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(String identifier, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke();
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            b.layer = layer;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(BlockState blockState) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
        try {
            return (Block) constructor.invoke(blockState);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(BlockState blockState, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(BlockState blockState, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Block get(BlockState blockState, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            b.layer = layer;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @UnmodifiableView
    public static Set<String> getPersistenceNames() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }


    public static OK<?> register(Class<? extends Block> block) {
        if (Modifier.isAbstract(block.getModifiers())) {
            return new OK<>(false, new IllegalArgumentException("you cant register a abstract block class!"));
        }
        try {
            Field properties = block.getDeclaredField("PROPERTIES");
            properties.setAccessible(true);
            int modifiers = properties.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && properties.getType() == BlockProperties.class) {
                BlockProperties blockProperties = (BlockProperties) properties.get(block);
                if (REGISTRY.putIfAbsent(blockProperties.getIdentifier(), blockProperties) == null) {
                    FastConstructor<? extends Block> c = FastConstructor.create(block.getConstructor(BlockState.class));
                    CACHE_CONSTRUCTORS.put(blockProperties.getIdentifier(), c);
                    return OK.TRUE;
                }
                return new OK<>(false, new IllegalArgumentException("This block has already been registered with the identifier: " + blockProperties.getIdentifier()));
            } else {
                return new OK<>(false, new IllegalArgumentException("There must define a field `public static final BlockProperties PROPERTIES` in this class!"));
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            return new OK<>(false, e);
        }
    }
}
