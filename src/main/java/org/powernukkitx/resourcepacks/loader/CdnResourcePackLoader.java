package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.Server;
import org.powernukkitx.config.category.GameplaySettings;
import org.powernukkitx.config.category.gameplay.CdnPackSettings;
import org.powernukkitx.resourcepacks.CdnResourcePack;
import org.powernukkitx.resourcepacks.ResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Loads the resource packs that are hosted on an external server. Packs are declared in the
 * {@code gameplay-settings.cdnPacks} section of pnx.yml and are downloaded by the client, not by
 * this server.
 */
@Slf4j
public class CdnResourcePackLoader implements ResourcePackLoader {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int MAX_PROBE_SIZE = 512 * 1024 * 1024;

    protected final GameplaySettings settings;

    public CdnResourcePackLoader(GameplaySettings settings) {
        this.settings = settings;
    }

    @Override
    public List<ResourcePack> loadPacks() {
        var baseLang = Server.getInstance().getLanguage();
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();

        HttpClient httpClient = null;
        for (CdnPackSettings entry : this.settings.cdnPacks()) {
            try {
                String url = entry.url();
                validateUrl(url);

                UUID id = entry.uuid().isEmpty() ? null : UUID.fromString(entry.uuid());
                String version = entry.version();
                String name = entry.name();
                int size = entry.size();

                if (id == null || version.isEmpty() || size <= 0) {
                    if (httpClient == null) {
                        httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
                    }
                    log.info(baseLang.tr("nukkit.resources.cdn.probe", url));
                    byte[] packData = download(httpClient, url);
                    JsonObject header = readManifest(packData).getAsJsonObject("header");
                    if (id == null) {
                        id = UUID.fromString(header.get("uuid").getAsString());
                    }
                    if (version.isEmpty()) {
                        version = readVersion(header);
                    }
                    if (name.isEmpty() && header.has("name")) {
                        name = header.get("name").getAsString();
                    }
                    size = packData.length;
                }

                if (name.isEmpty()) {
                    name = id.toString();
                }

                loadedResourcePacks.add(new CdnResourcePack(
                        name,
                        id,
                        version,
                        url,
                        size,
                        entry.contentKey(),
                        entry.subPackName(),
                        entry.addonPack(),
                        entry.scripts(),
                        entry.rayTracing()
                ));
                log.info(baseLang.tr("nukkit.resources.cdn.loaded", name, url));
            } catch (Exception e) {
                log.warn(baseLang.tr("nukkit.resources.cdn.fail", entry.url(), String.valueOf(e.getMessage())));
            }
        }
        return loadedResourcePacks;
    }

    private static void validateUrl(String url) {
        URI uri = URI.create(url);
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ENGLISH);
        if (!scheme.equals("https") && !scheme.equals("http")) {
            throw new IllegalArgumentException("Only http and https urls are supported");
        }
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Missing host in url");
        }
        if (scheme.equals("http")) {
            log.warn("The CDN resource pack url {} is not using https, clients may refuse to download it", url);
        }
    }

    private static byte[] download(HttpClient httpClient, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(TIMEOUT).GET().build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected status code " + response.statusCode());
        }
        byte[] body = response.body();
        if (body.length == 0 || body.length > MAX_PROBE_SIZE) {
            throw new IOException("Invalid pack size " + body.length);
        }
        return body;
    }

    private static JsonObject readManifest(byte[] packData) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(packData))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File file = new File(entry.getName());
                if (!file.getName().equalsIgnoreCase("manifest.json")) {
                    continue;
                }
                if (file.getParent() != null && file.getParentFile().getParent() != null) {
                    continue;
                }
                return JsonParser.parseReader(new InputStreamReader(zip, StandardCharsets.UTF_8)).getAsJsonObject();
            }
        }
        throw new IOException(Server.getInstance().getLanguage().tr("nukkit.resources.zip.no-manifest"));
    }

    private static String readVersion(JsonObject header) {
        JsonArray version = header.getAsJsonArray("version");
        return String.join(".", version.get(0).getAsString(),
                version.get(1).getAsString(),
                version.get(2).getAsString());
    }
}
