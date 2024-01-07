package cn.nukkit;

import cn.nukkit.registry.Registries;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class PNXTestExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        Registries.BLOCK.init();
    }
}