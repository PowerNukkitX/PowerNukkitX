package cn.nukkit.registry;

/**
 * Allay Project 12/18/2023
 *
 * @author Cool_Loong
 */
public abstract class BaseRegistry<K, V, R> implements IRegistry<K, V, R> {
    public BaseRegistry() {
        init();
    }
}
