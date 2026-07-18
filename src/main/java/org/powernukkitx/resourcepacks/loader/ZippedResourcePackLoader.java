package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.Server;
import org.powernukkitx.resourcepacks.ResourcePack;
import org.powernukkitx.resourcepacks.ZippedResourcePack;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j


public class ZippedResourcePackLoader implements ResourcePackLoader {

    //资源包文件存放地址
    protected final File path;

    public ZippedResourcePackLoader(File path) {
        this.path = path;
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.invalid-path", path.getName()));
        }
    }

    @Override
    public List<ResourcePack> loadPacks() {
        var baseLang = Server.getInstance().getLanguage();
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File pack : path.listFiles()) {
            try {
                ResourcePack resourcePack = null;
                String fileExt = Files.getFileExtension(pack.getName());
                if (!pack.isDirectory() && !fileExt.equals("key")) { //directory resource packs temporarily unsupported
                    switch (fileExt) {
                        case "zip", "mcpack" -> resourcePack = new ZippedResourcePack(pack);
                        default -> log.warn(baseLang.tr("nukkit.resources.unknown-format", pack.getName()));
                    }
                }
                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack);
                    log.info(baseLang.tr("nukkit.resources.zip.loaded", pack.getName()));
                }
            } catch (IllegalArgumentException e) {
                log.warn(baseLang.tr("nukkit.resources.fail", pack.getName(), e.getMessage()), e);
            }
        }
        return loadedResourcePacks;
    }
}
