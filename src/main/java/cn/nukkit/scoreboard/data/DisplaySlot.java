package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.19.21-r5")
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
