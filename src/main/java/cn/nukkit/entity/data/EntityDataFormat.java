package cn.nukkit.entity.data;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EntityDataFormat {
    BYTE,
    SHORT,
    INT,
    FLOAT,
    STRING,
    NBT,
    VECTOR3I,
    LONG,
    VECTOR3F;

    public static EntityDataFormat from(Class<?> clazz) {
        if (clazz == Byte.class) {
            return BYTE;
        } else if (clazz == Short.class) {
            return SHORT;
        } else if (clazz == Integer.class) {
            return INT;
        } else if (clazz == Float.class) {
            return FLOAT;
        } else if (clazz == String.class) {
            return STRING;
        } else if (clazz == CompoundTag.class) {
            return NBT;
        } else if (clazz == BlockVector3.class) {
            return VECTOR3I;
        } else if (clazz == Long.class) {
            return LONG;
        } else if (clazz == Vector3.class) {
            return VECTOR3F;
        }
        throw new IllegalArgumentException("Unknown EntityDataType: " + clazz);
    }
}
