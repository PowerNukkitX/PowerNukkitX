package cn.nukkit;

import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.registry.BlockRegistry;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;

public class LevelDBProviderExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {
    static Level level = Mockito.mock(Level.class);

    @Override
    @SneakyThrows
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        LevelDBProvider levelDBProvider = extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get("levelDBProvider", LevelDBProvider.class);
        levelDBProvider.initDimensionData(DimensionEnum.OVERWORLD.getDimensionData());
        levelDBProvider.close();
        FileUtils.deleteDirectory(new File("src/test/resources/newlevel"));
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        FileUtils.copyDirectory(new File("src/test/resources/level"), new File("src/test/resources/newlevel"));
        LevelDBProvider levelDBProvider = new LevelDBProvider(level, "src/test/resources/newlevel");
        levelDBProvider.initDimensionData(DimensionEnum.OVERWORLD.getDimensionData());
        extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("levelDBProvider", levelDBProvider);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(LevelDBProvider.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        LevelDBProvider levelDBProvider = extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get("levelDBProvider", LevelDBProvider.class);
        levelDBProvider.initDimensionData(DimensionEnum.OVERWORLD.getDimensionData());
        if (parameterContext.getParameter().getType().equals(LevelDBProvider.class)) {
            return levelDBProvider;
        }
        return null;
    }
}
