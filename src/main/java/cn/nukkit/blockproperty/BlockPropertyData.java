package cn.nukkit.blockproperty;

import cn.nukkit.nbt.tag.CompoundTag;

public record BlockPropertyData(String namespace, CompoundTag blockProperty) {
}
