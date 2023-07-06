package cn.nukkit.spark;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;

@PowerNukkitXOnly
@Since("1.20.0-r3")
public class SparkInstaller {

    public static boolean initSpark(@Nonnull Server server) {
        boolean download = false;

        Plugin spark = server.getPluginManager().getPlugin("spark");

        File sparkFile = null;

        if (spark != null) {
            sparkFile = spark.getFile();
            try {
                String sha1 = getFileSha1(sparkFile);
                URL url = new URL("https://sparkapi.lucko.me/download/nukkit/sha1");
                try (InputStream in = url.openStream()) {
                    byte[] sha1Remote = new byte[40];
                    in.read(sha1Remote);
                    if (!sha1.equals(new String(sha1Remote))) {
                        download = true;
                    }
                }
            } catch (Exception e) {
                download = false;
                server.getLogger().alert("Failed to check spark update: " + e.getMessage());
            }
        } else {
            download = true;
        }

        if (download) {
            if (spark != null && sparkFile != null) {
                server.getPluginManager().disablePlugin(spark);
                sparkFile.delete();
            }
            try (InputStream in = new URL("https://sparkapi.lucko.me/download/nukkit").openStream()) {
                File targetPath = new File(server.getPluginPath() + "/spark.jar");
                Files.copy(in, targetPath.toPath());
                server.getPluginManager().loadPlugin(targetPath);
                server.getLogger().info("Spark has been installed.");
            } catch (IOException e) {
                server.getLogger().alert("Failed to download spark: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return download;
    }

    private static String getFileSha1(File file) throws Exception {
        return String.format("%040x", new BigInteger(1,
                MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(file.toPath()))));
    }
}
