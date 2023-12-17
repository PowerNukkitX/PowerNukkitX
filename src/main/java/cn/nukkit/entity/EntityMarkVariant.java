package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.utils.Utils;

/**
 * 实现这个接口的实体拥有次要变种属性
 */


public interface EntityMarkVariant extends EntityComponent {
    default int getMarkVariant() {
        return getMemoryStorage().get(CoreMemoryTypes.MARK_VARIANT);
    }

    default void setMarkVariant(int variant) {
        getMemoryStorage().put(CoreMemoryTypes.MARK_VARIANT, variant);
    }

    default boolean hasMarkVariant() {
        return getMemoryStorage().notEmpty(CoreMemoryTypes.MARK_VARIANT);
    }

    /**
     * 随机一个变种值
     */
    default int randomMarkVariant() {
        return getAllMarkVariant()[Utils.rand(0, getAllMarkVariant().length - 1)];
    }

    /**
     * 定义全部可能的变种
     */
    int[] getAllMarkVariant();
}
