package cn.nukkit.block.customblock;

import cn.nukkit.nbt.tag.CompoundTag;

public record BlockPropertyData(String namespace, CompoundTag blockProperty) {
}
