package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 此类描述了放在jar插件文件内assets/resource_pack目录的资源包相关文件
 */


@Slf4j
public class JarPluginResourcePack extends AbstractResourcePack {
    public static final String RESOURCE_PACK_PATH = "assets/resource_pack/";
    protected File jarPluginFile;
    protected ByteBuffer zippedByteBuffer;
    protected byte[] sha256;
    protected String encryptionKey = "";

    public static boolean hasResourcePack(File jarPluginFile) {
        try {
            return findManifestInJar(new ZipFile(jarPluginFile)) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Nullable
    protected static ZipEntry findManifestInJar(ZipFile jar) {
        ZipEntry manifest = jar.getEntry(RESOURCE_PACK_PATH + "manifest.json");
        if (manifest == null) {
            manifest = jar.stream()
                    .filter(e -> e.getName().toLowerCase(Locale.ENGLISH).endsWith("manifest.json") && !e.isDirectory())
                    .filter(e -> {
                        File fe = new File(e.getName());
                        if (!fe.getName().equalsIgnoreCase("manifest.json")) {
                            return false;
                        }
                        return fe.getParent() == null || fe.getParentFile().getParent() == null;
                    })
                    .findFirst()
                    .orElse(null);
        }
        return manifest;
    }

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
            ZipEntry manifest = findManifestInJar(jar);
            if (manifest == null)
                throw new IllegalArgumentException(
                        Server.getInstance().getLanguage().tr("nukkit.resources.zip.no-manifest"));

            this.manifest = JsonParser
                    .parseReader(new InputStreamReader(jar.getInputStream(manifest), StandardCharsets.UTF_8))
                    .getAsJsonObject();

            ZipEntry encryptionKeyEntry = jar.getEntry(RESOURCE_PACK_PATH + "encryption.key");
            if (encryptionKeyEntry != null) {
                this.encryptionKey = new String(jar.getInputStream(encryptionKeyEntry).readAllBytes(),StandardCharsets.UTF_8);
                log.debug(this.encryptionKey);
            }

            jar.stream().forEach(entry -> {
                if (entry.getName().startsWith(RESOURCE_PACK_PATH) && !entry.isDirectory() && !entry.getName().equals(RESOURCE_PACK_PATH + "encryption.key")) {
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

            zippedByteBuffer = ByteBuffer.allocateDirect(byteArrayOutputStream.size());
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

        try{
            zippedByteBuffer.get(off, chunk);
        } catch (Exception e) {
            log.error("An error occurred while processing the resource pack {} at offset:{} and length:{}", getPackName(), off, len, e);
        }

        return chunk;
    }
}
