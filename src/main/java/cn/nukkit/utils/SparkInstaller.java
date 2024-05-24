package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;


@Slf4j
public class SparkInstaller {
    public static boolean initSpark(@Nonnull Server server) {
        boolean download = false;
        Plugin spark = server.getPluginManager().getPlugin("spark");
        if (spark == null) {
            download = true;
        }

        if (download) {
            try (InputStream in = SparkInstaller.class.getClassLoader().getResourceAsStream("spark.jar")) {
                assert in != null;
                File targetPath = new File(server.getPluginPath(), "spark.jar");
                if (!targetPath.exists()) {
                    //Do not remove this try catch block!!!
                    try {
                        Files.copy(in, targetPath.toPath());
                        server.getPluginManager().enablePlugin(server.getPluginManager().loadPlugin(targetPath));
                        log.info("Spark has been installed.");
                    } catch (Exception e)  {
                        log.warn("Failed to copy spark: {}", Arrays.toString(e.getStackTrace()));
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to download spark: {}", Arrays.toString(e.getStackTrace()));
            }
        }

        return download;
    }
}
