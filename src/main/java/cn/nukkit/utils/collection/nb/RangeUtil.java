package cn.nukkit.utils.collection.nb;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public final class RangeUtil {
    public static int checkPositiveOrZero(int n, String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: >= 0)");
        }
        return n;
    }
}
