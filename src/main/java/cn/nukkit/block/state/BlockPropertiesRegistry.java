package cn.nukkit.block.state;

import cn.nukkit.block.Block;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
public final class BlockPropertiesRegistry {
    private static final Object2ObjectOpenHashMap<String, BlockProperties> REGISTRY = new Object2ObjectOpenHashMap<>();

    public static void init() {
        //todo more block register
    }

    public static void trim() {
        REGISTRY.trim();
    }

    public static BlockProperties get(String identifier) {
        return REGISTRY.get(identifier);
    }

    public static OK<Throwable> register(Class<? extends Block> block) {
        try {
            Field properties = block.getDeclaredField("PROPERTIES");
            properties.setAccessible(true);
            int modifiers = properties.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && properties.getType() == BlockProperties.class) {
                Object o = properties.get(block);
                REGISTRY.put(blockProperties.getIdentifier(), blockProperties);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
