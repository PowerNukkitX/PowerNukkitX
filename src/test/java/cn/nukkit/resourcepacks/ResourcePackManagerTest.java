package cn.nukkit.resourcepacks;

import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PowerNukkitExtension.class)
class ResourcePackManagerTest {

    ResourcePackManager resourcePackManager;
    Path temp;

    @BeforeEach
    void setUp() throws IOException {
        temp = Files.createTempDirectory("ResourcePackManagerTest_");
        resourcePackManager = new ResourcePackManager(temp.toFile());
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteRecursively(temp.toFile());
    }

    @Test
    void maxChunkSize() {
        assertEquals(1024*100, resourcePackManager.getMaxChunkSize());
        resourcePackManager.setMaxChunkSize(1024);
        assertEquals(1024, resourcePackManager.getMaxChunkSize());
        ResourcePackManager other = new ResourcePackManager(temp.toFile());
        assertEquals(1024*100, other.getMaxChunkSize());
        assertEquals(1024, resourcePackManager.getMaxChunkSize());
    }
}
