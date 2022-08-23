package cn.powernukkitx.tools;

import cn.nukkit.utils.MinecraftNamespaceComparator;

import java.util.Arrays;
import java.util.List;

public final class FNVComparator {
    public static void main(String[] args) {
        // List<String> tmp = Arrays.asList("rail_direction", "rail_data_bit");
        List<String> tmp = Arrays.asList("radio:west", "radio:top", "radio:south", "radio:north", "radio:east", "radio:bottom");
        tmp.sort((idA, idB) -> MinecraftNamespaceComparator.compareFNV(idA, idB));
        System.out.println(tmp);
    }
}
