package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;

import java.nio.charset.StandardCharsets;

public class MinecraftNamespaceComparator {
    private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV1_PRIME_64 = 1099511628211L;


    public static int compareBlocks(Block blockA, Block blockB) {
        return compare(blockA.getPersistenceName(), blockB.getPersistenceName());
    }

    public static int compareItems(Item itemA, Item itemB) {
        return compare(itemA.getNamespaceId(), itemB.getNamespaceId());
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

    public static long fnv164(byte[] data) {
        long hash = FNV1_64_INIT;
        for (byte datum : data) {
            hash *= FNV1_PRIME_64;
            hash ^= (datum & 0xff);
        }

        return hash;
    }

    private static final int FNV1_32_INIT = 0x811c9dc5;
    private static final int FNV1_PRIME_32 = 0x01000193;

    public static int fnv1a_32(byte[] data) {
        int hash = FNV1_32_INIT;
        for (byte datum : data) {
            hash ^= (datum & 0xff);
            hash *= FNV1_PRIME_32;
        }
        return hash;
    }
}
