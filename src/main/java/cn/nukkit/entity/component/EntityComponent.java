package cn.nukkit.entity.component;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实体组件<p>
 * 一个实体组件包含了此组件功能的所有逻辑,并可被注册到实体组件组中<p>
 * 对于一些复杂功能，最好把它写成一个组件<p>
 * 这样子就只需要在使用时在添加此组件到实体的组件组中并实现接口，而不是在每个实体都重写一遍相同逻辑
 * 一般不直接实现此接口，请继承{@link AbstractEntityComponent}<p/>
 *
 * 注意！实现了此接口的类必须具有一个形参只有{@link cn.nukkit.entity.Entity}的构造函数，对象将会反射创建
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface EntityComponent {

    /**
     * 实体初始化时被调用，组件可以在这个方法中写一些状态设置的逻辑
     */
    default void onInitEntity() {};

    /**
     * 当需要保存数据进nbt时调用
     */
    default void onSaveNBT() {};
}
