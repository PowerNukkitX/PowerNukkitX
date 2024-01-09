package cn.nukkit;

import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class BlockRegistryExtension implements ParameterResolver {
    final static BlockRegistry BLOCK_REGISTRY;

    static {
        Registries.BLOCK.init();
        BLOCK_REGISTRY = Registries.BLOCK;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(BlockRegistry.class); // 修改为你的全局变量类型
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        if (parameterContext.getParameter().getType().equals(BlockRegistry.class)) {
            return BLOCK_REGISTRY;
        }
        return null;
    }
}