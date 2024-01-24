package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

public interface EntityColor extends EntityComponent {
    default void setColor(DyeColor color) {
        getMemoryStorage().put(CoreMemoryTypes.COLOR, Integer.valueOf(color.getWoolData()).byteValue());
    }

    default void setColor2(DyeColor color) {
        getMemoryStorage().put(CoreMemoryTypes.COLOR2, Integer.valueOf(color.getWoolData()).byteValue());
    }

    default DyeColor getColor() {
        return DyeColor.getByWoolData(getMemoryStorage().get(CoreMemoryTypes.COLOR).intValue());
    }

    default DyeColor getColor2() {
        return DyeColor.getByWoolData(getMemoryStorage().get(CoreMemoryTypes.COLOR2).intValue());
    }

    default boolean hasColor() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.COLOR);
    }
    default boolean hasColor2() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.COLOR2);
    }

    default DyeColor getRandomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        DyeColor[] colors = DyeColor.values();
        int c = random.nextInt(colors.length);
        return colors[c];
    }
}
