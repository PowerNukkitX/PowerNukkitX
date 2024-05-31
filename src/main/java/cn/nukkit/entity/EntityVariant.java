package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.utils.Utils;

/**
 * 实现这个接口的实体拥有变种属性
 */


public interface EntityVariant extends EntityComponent {
    default 
    /**
     * @deprecated 
     */
    int getVariant() {
        return getMemoryStorage().get(CoreMemoryTypes.VARIANT);
    }

    default 
    /**
     * @deprecated 
     */
    void setVariant(int variant) {
        getMemoryStorage().put(CoreMemoryTypes.VARIANT, variant);
    }

    default 
    /**
     * @deprecated 
     */
    boolean hasVariant() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.VARIANT);
    }

    /**
     * 随机一个变种值
     */
    default 
    /**
     * @deprecated 
     */
    int randomVariant() {
        return getAllVariant()[Utils.rand(0, getAllVariant().length - 1)];
    }

    /**
     * 定义全部可能的变种
     */
    int[] getAllVariant();
}
