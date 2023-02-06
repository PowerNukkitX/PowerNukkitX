package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends ItemTool instead of Item only in PowerNukkit")
public class ItemShield extends ItemTool {

    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    /**
     * 为自定义盾牌提供的构造函数
     * <p>
     * Constructor for custom shield
     *
     * @param id    the id
     * @param meta  the meta
     * @param count the count
     * @param name  the name
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public ItemShield(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_SHIELD;
    }
}
