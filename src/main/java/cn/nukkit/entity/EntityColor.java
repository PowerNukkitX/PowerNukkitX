package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.utils.DyeColor;


public interface EntityColor extends EntityComponent {
    default void setColor(DyeColor color) {
        getMemoryStorage().put(CoreMemoryTypes.COLOUR, Integer.valueOf(color.getWoolData()).byteValue());
    }

    default DyeColor getColor() {
        return DyeColor.getByWoolData(getMemoryStorage().get(CoreMemoryTypes.COLOUR).intValue());
    }

    default boolean hasColor() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.COLOUR);
    }
}
