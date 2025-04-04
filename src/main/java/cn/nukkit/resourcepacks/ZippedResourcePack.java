package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class ZippedResourcePack extends AbstractResourcePack {
    protected File file;


    protected ByteBuffer byteBuffer;
    protected byte[] sha256;
    protected String encryptionKey = "";


    public ZippedResourcePack(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.zip.not-found", file.getName()));
        }

        this.file = file;

        try {
            this.sha256 = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(this.file.toPath()));
        } catch (Exception e) {
            log.error("Failed to parse the SHA-256 of the resource pack {}", file, e);
        }

        try (ZipFile zip = new ZipFile(file)) {
            loadZip(zip);
        } catch (java.util.zip.ZipException exception) {
            if (exception.getMessage().equals("invalid CEN header (bad entry name)")) {
                try (ZipFile zip = new ZipFile(file, Charset.forName("GBK"))) {
                    loadZip(zip);
                } catch (IOException e) {
                    log.error("An error occurred while loading the zipped resource pack {}", file, e);
                }
            } else {
                log.error("An error occurred while loading the zipped resource pack {}", file, exception);
            }
        } catch (IOException e) {
            log.error("An error occurred while loading the zipped resource pack {}", file, e);
        }

        if (!this.verifyManifest()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.zip.invalid-manifest"));
        }
    }

    private void loadZip(ZipFile zip) throws IOException {
        ZipEntry entry = zip.getEntry("manifest.json");
        if (entry == null) {
            entry = zip.stream()
                    .filter(e -> e.getName().toLowerCase(Locale.ENGLISH).endsWith("manifest.json") && !e.isDirectory())
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

        this.manifest = JsonParser
                .parseReader(new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8))
                .getAsJsonObject();
        File parentFolder = this.file.getParentFile();
        if (parentFolder == null || !parentFolder.isDirectory()) {
            throw new IOException("Invalid resource pack path");
        }
        File keyFile = new File(parentFolder, this.file.getName() + ".key");
        if (keyFile.exists()) {
            this.encryptionKey = new String(Files.readAllBytes(keyFile.toPath()), StandardCharsets.UTF_8);
            log.debug(this.encryptionKey);
        }

        var bytes = Files.readAllBytes(file.toPath());
        //使用java nio bytebuffer以获得更好性能
        byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        byte[] chunk;
        if (this.getPackSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[this.getPackSize() - off];
        }

        try {
            byteBuffer.get(off, chunk);
        } catch (Exception e) {
            log.error("An error occurred while processing the resource pack {} at offset:{} and length:{}", getPackName(), off, len, e);
        }

        return chunk;
    }

    @Override
    public boolean isAddonPack() {
        return false;
    }

    @Override
    public String cdnUrl() {
        return "";
    }

    @Override
    public int getPackSize() {
        return (int) this.file.length();
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }
}
