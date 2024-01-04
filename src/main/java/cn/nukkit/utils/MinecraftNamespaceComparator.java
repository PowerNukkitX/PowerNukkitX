package cn.nukkit.utils;

import cn.nukkit.item.Item;

import java.nio.charset.StandardCharsets;

import static cn.nukkit.utils.HashUtils.fnv164;

public class MinecraftNamespaceComparator {


    public static int compareItems(Item itemA, Item itemB) {
        return compare(itemA.getId(), itemB.getId());
    }

    public static int compare(String idA, String idB) {
        String childIdA = idA.substring(idA.indexOf(":") + 1);
        String childIdB = idB.substring(idB.indexOf(":") + 1);

        // Compare by child first
        int childIdComparsion = childIdB.compareTo(childIdA);
        if (childIdComparsion != 0) {
            return childIdComparsion;
        }

        // Compare by namespace if childs are equal
        String namespaceA = idA.substring(0, idA.indexOf(":"));
        String namespaceB = idB.substring(0, idB.indexOf(":"));
        return namespaceB.compareTo(namespaceA);
    }

    // https://gist.github.com/SupremeMortal/5e09c8b0eb6b3a30439b317b875bc29c
    // Thank you Supreme
    public static int compareFNV(String idA, String idB) {
        byte[] bytes1 = idA.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = idB.getBytes(StandardCharsets.UTF_8);
        long hash1 = fnv164(bytes1);
        long hash2 = fnv164(bytes2);
        return Long.compareUnsigned(hash1, hash2);
    }

}
