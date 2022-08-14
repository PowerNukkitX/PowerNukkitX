package cn.powernukkitx.tools;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.LinkedCompoundTag;
import cn.nukkit.utils.MinecraftNamespaceComparator;

import java.util.Arrays;
import java.util.List;

public final class FNVComparator {
    public static void main(String[] args) {
        List<String> tmp = Arrays.asList("in_wall_bit", "open_bit");
        tmp.sort((idA, idB) -> -MinecraftNamespaceComparator.compareFNV(idA, idB));
        System.out.println(tmp);
    }
}
