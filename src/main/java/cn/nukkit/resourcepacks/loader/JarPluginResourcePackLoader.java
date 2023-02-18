package cn.nukkit.resourcepacks.loader;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.resourcepacks.JarPluginResourcePack;
import cn.nukkit.resourcepacks.ResourcePack;
import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@PowerNukkitXOnly
@Since("1.19.60-r2")
public class JarPluginResourcePackLoader implements ResourcePackLoader {

    protected final File jarPath;

    public JarPluginResourcePackLoader(File jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public List<ResourcePack> loadPacks() {
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File jar : jarPath.listFiles()) {
            ResourcePack resourcePack = null;
            String fileExt = Files.getFileExtension(jar.getName());
            if (!jar.isDirectory()) {
                if (fileExt.equals("jar")) {
                    try {
                        resourcePack = new JarPluginResourcePack(jar);
                    } catch (IllegalArgumentException ignore) {}
                }
            }
            if (resourcePack != null) {
                loadedResourcePacks.add(resourcePack);
            }
        }
        return loadedResourcePacks;
    }
}
