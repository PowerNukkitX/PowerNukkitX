package org.powernukkitx.level.format;

import org.powernukkitx.level.Level;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Adapts a {@link LevelProvider} class following the legacy contract - a {@code (Level, String)} constructor plus
 * the static {@code isValid(String)} and {@code generate(String, String, GeneratorConfig)} methods - to the
 * {@link LevelProviderFactory} API.
 */
public class ReflectiveLevelProviderFactory implements LevelProviderFactory {
    @Getter
    private final String name;
    @Getter
    private final Class<? extends LevelProvider> providerClass;
    private final Constructor<? extends LevelProvider> constructor;
    private final Method isValidMethod;
    private final Method generateMethod;

    public ReflectiveLevelProviderFactory(String name, Class<? extends LevelProvider> providerClass) {
        this.name = name;
        this.providerClass = providerClass;
        try {
            this.constructor = providerClass.getConstructor(Level.class, String.class);
            this.isValidMethod = providerClass.getMethod("isValid", String.class); // TODO: this doesn't looks great, we should find a great way
            this.generateMethod = providerClass.getMethod("generate", String.class, String.class, LevelConfig.GeneratorConfig.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(providerClass.getName() + " does not follow the level provider contract", e);
        }
    }

    @Override
    public LevelProvider create(Level level, String path) throws Exception {
        return this.constructor.newInstance(level, path);
    }

    @Override
    public boolean isValid(String path) {
        try {
            return (boolean) this.isValidMethod.invoke(null, path);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to validate " + path + " with " + this.providerClass.getName(), e);
        }
    }

    @Override
    public void generate(String path, String name, LevelConfig.GeneratorConfig generatorConfig) throws Exception {
        this.generateMethod.invoke(null, path, name, generatorConfig);
    }
}
