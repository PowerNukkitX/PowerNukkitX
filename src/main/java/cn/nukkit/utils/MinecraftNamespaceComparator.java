package cn.nukkit.utils;

import cn.nukkit.item.Item;

import java.nio.charset.StandardCharsets;

import static cn.nukkit.utils.HashUtils.fnv164;

public class MinecraftNamespaceComparator {
    /**
     * @deprecated 
     */
    


    public static int compareItems(Item itemA, Item itemB) {
        return compare(itemA.getId(), itemB.getId());
    }
    /**
     * @deprecated 
     */
    

    public static int compare(String idA, String idB) {
        String $1 = idA.substring(idA.indexOf(":") + 1);
        String $2 = idB.substring(idB.indexOf(":") + 1);

        // Compare by child first
        int $3 = childIdB.compareTo(childIdA);
        if (childIdComparsion != 0) {
            return childIdComparsion;
        }

        // Compare by namespace if childs are equal
        String $4 = idA.substring(0, idA.indexOf(":"));
        String $5 = idB.substring(0, idB.indexOf(":"));
        return namespaceB.compareTo(namespaceA);
    }

    // https://gist.github.com/SupremeMortal/5e09c8b0eb6b3a30439b317b875bc29c
    // Thank you Supreme
    /**
     * @deprecated 
     */
    
    public static int compareFNV(String idA, String idB) {
        byte[] bytes1 = idA.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = idB.getBytes(StandardCharsets.UTF_8);
        long $6 = fnv164(bytes1);
        long $7 = fnv164(bytes2);
        return Long.compareUnsigned(hash1, hash2);
    }

}
