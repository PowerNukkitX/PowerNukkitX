package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 此类描述了放在jar插件文件内assets/resource_pack目录的资源包相关文件
 */
@PowerNukkitXOnly
@Since("1.19.60-r2")
@Log4j2
public class JarPluginResourcePack extends AbstractResourcePack {
    public static final String RESOURCE_PACK_PATH = "assets/resource_pack/";
    protected File jarPluginFile;
    protected ByteBuffer zippedByteBuffer;
    protected byte[] sha256;
    protected String encryptionKey = "";

    public JarPluginResourcePack(File jarPluginFile) {
        if (!jarPluginFile.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.zip.not-found", jarPluginFile.getName()));
        }

        this.jarPluginFile = jarPluginFile;


        try {
            ZipFile jar = new ZipFile(jarPluginFile);
            var byteArrayOutputStream = new ByteArrayOutputStream();
            var zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            ZipEntry manifest = jar.getEntry(RESOURCE_PACK_PATH + "manifest.json");
            if (manifest == null) {
                manifest = jar.stream()
                        .filter(e -> e.getName().toLowerCase().endsWith("manifest.json") && !e.isDirectory())
                        .filter(e -> {
                            File fe = new File(e.getName());
                            if (!fe.getName().equalsIgnoreCase("manifest.json")) {
                                return false;
                            }
                            return fe.getParent() == null || fe.getParentFile().getParent() == null;
                        })
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                Server.getInstance().getLanguage().tr("nukkit.resources.zip.no-manifest")));
            }

            this.manifest = new JsonParser()
                    .parse(new InputStreamReader(jar.getInputStream(manifest), StandardCharsets.UTF_8))
                    .getAsJsonObject();

            ZipEntry encryptionKeyEntry = jar.getEntry(RESOURCE_PACK_PATH + "encryption.key");
            if (encryptionKeyEntry != null) {
                this.encryptionKey = new String(jar.getInputStream(encryptionKeyEntry).readAllBytes(),StandardCharsets.UTF_8);
                log.debug(this.encryptionKey);
            }

            jar.stream().forEach(entry -> {
                if (entry.getName().startsWith(RESOURCE_PACK_PATH) && !entry.isDirectory()) {
                    try {
                        zipOutputStream.putNextEntry(new ZipEntry(entry.getName().substring(RESOURCE_PACK_PATH.length())));
                        zipOutputStream.write(jar.getInputStream(entry).readAllBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            jar.close();
            zipOutputStream.close();
            byteArrayOutputStream.close();

            zippedByteBuffer = ByteBuffer.allocate(byteArrayOutputStream.size());
            var bytes = byteArrayOutputStream.toByteArray();
            zippedByteBuffer.put(bytes);
            zippedByteBuffer.flip();


            try {
                this.sha256 = MessageDigest.getInstance("SHA-256").digest(bytes);
            } catch (Exception e) {
                log.error("Failed to parse the SHA-256 of the resource pack inside of jar plugin {}", jarPluginFile.getName(), e);
            }
        } catch (IOException e) {
            log.error("An error occurred while loading the resource pack inside of a jar plugin {}", jarPluginFile, e);
        }

        if (!this.verifyManifest()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.zip.invalid-manifest"));
        }
    }

    @Override
    public int getPackSize() {
        return this.zippedByteBuffer.limit();
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        byte[] chunk;
        if (this.getPackSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[this.getPackSize() - off];
        }

        zippedByteBuffer.get(off, chunk);
        return chunk;
    }
}
