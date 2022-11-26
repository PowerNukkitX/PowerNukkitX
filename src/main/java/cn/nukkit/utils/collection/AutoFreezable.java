package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public interface AutoFreezable {
    FreezeStatus getFreezeStatus();

    int getTemperature();

    /**
     * this.temperature += temperature; <br/>
     * 没有绝对零度检查！
     * @param temperature 温度变化量
     */
    void warmer(int temperature);

    /**
     * this.temperature -= temperature; <br/>
     * 带有绝对零度检查
     * @param temperature 温度变化量
     */
    void colder(int temperature);

    /**
     * 强制冻结数组
     */
    void freeze();

    /**
     * 强制深度冻结数组
     */
    void deepFreeze();

    @ShouldThaw
    void thaw();

    enum FreezeStatus {
        NONE, FREEZING, FREEZE, DEEP_FREEZING, DEEP_FREEZE, THAWING
    }
}
