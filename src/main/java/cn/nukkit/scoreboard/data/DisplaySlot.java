package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

/**
 * 计分板显示槽位枚举
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public enum DisplaySlot {

    SIDEBAR("sidebar"),
    LIST("list"),
    BELOW_NAME("belowname");

    @Getter
    private String slotName;

    DisplaySlot(String slotName) {
        this.slotName = slotName;
    }
}
