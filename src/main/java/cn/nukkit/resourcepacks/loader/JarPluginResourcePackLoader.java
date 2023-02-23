package cn.nukkit.resourcepacks.loader;

import cn.nukkit.Server;
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
@Since("1.19.62-r1")
public class JarPluginResourcePackLoader implements ResourcePackLoader {

    protected final File jarPath;

    public JarPluginResourcePackLoader(File jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public List<ResourcePack> loadPacks() {
        var baseLang = Server.getInstance().getLanguage();
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File jar : jarPath.listFiles()) {
            try {
                ResourcePack resourcePack = null;
                String fileExt = Files.getFileExtension(jar.getName());
                if (!jar.isDirectory()) {
                    if (fileExt.equals("jar") && JarPluginResourcePack.hasResourcePack(jar)) {
                        log.info(baseLang.tr("nukkit.resources.plugin.loading", jar.getName()));
                        resourcePack = new JarPluginResourcePack(jar);
                    }
                }
                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack);
                    log.info(baseLang.tr("nukkit.resources.plugin.loaded", jar.getName(), resourcePack.getPackName()));
                }
            } catch (IllegalArgumentException e) {
                log.warn(baseLang.tr("nukkit.resources.fail", jar.getName(), e.getMessage()), e);
            }
        }
        return loadedResourcePacks;
    }
}
