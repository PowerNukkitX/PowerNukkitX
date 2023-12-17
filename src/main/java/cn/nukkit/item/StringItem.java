package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.utils.StringUtils;

/**
 * @author joserobjr
 * @since 2021-06-12
 */

@PowerNukkitXDifference(info = "Change to interface,Remove CustomCompound from StringItem")
public interface StringItem {
    String getNamespaceId();

    static String notEmpty(String value) {
        if (value != null && value.trim().isEmpty()) throw new IllegalArgumentException("The name cannot be empty");
        return value;
    }
    static String createItemName(String namespaceId) {
        StringBuilder name = new StringBuilder();
        String[] split = StringUtils.fastSplit(":", namespaceId).get(1).split("_");
        for (int i = 0; i < split.length; i++) {
            name.append(Character.toUpperCase(split[i].charAt(0))).append(split[i].substring(1));
            if (i != split.length - 1) {
                name.append(" ");
            }
        }
        return name.toString();
    }

    default int getId() {
        return ItemID.STRING_IDENTIFIED_ITEM;
    }
}
