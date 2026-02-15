package cn.nukkit.wizard;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

@Slf4j
public class SparkInstaller {

    // Maven coordinates
    private static final String REPO_BASE = "https://repo.powernukkitx.org/releases";
    private static final String GROUP_ID = "me.lucko";
    private static final String ARTIFACT_ID = "spark-pnx";
    private static final String VERSION = "1.10-PNX-SNAPSHOT";

    // We want the shaded jar
    private static final String CLASSIFIER = "all";
    private static final String EXTENSION = "jar";

    public static void initSpark(@Nonnull Server server) {
        boolean download = false;
        Plugin spark = server.getPluginManager().getPlugin("spark");
        if (spark == null) {
            download = true;
        }

        if (!download) {
            return;
        }

        File targetPath = new File(server.getPluginPath(), "spark.jar");

        // Keep old behavior: if spark.jar already exists, don't overwrite it.
        if (targetPath.exists()) {
            log.info("Spark not loaded but spark.jar already exists at {} - skipping download.", targetPath.getAbsolutePath());
            return;
        }

        try {
            Files.createDirectories(targetPath.getParentFile().toPath());

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            URI jarUri = resolveLatestSparkAllJar(client);

            // Download to a temp file first, then atomically move into place
            Path tmp = Files.createTempFile("spark-", ".jar");
            try {
                httpDownload(client, jarUri, tmp);
                Files.move(tmp, targetPath.toPath());
            } finally {
                try { Files.deleteIfExists(tmp); } catch (IOException ignored) {}
            }

            server.getPluginManager().enablePlugin(server.getPluginManager().loadPlugin(targetPath));
            log.info("Spark has been downloaded and installed from repo: {}", jarUri);
        } catch (Exception e) {
            log.warn("Failed to download/install spark: {}", Arrays.toString(e.getStackTrace()));
        }

    }

    private static URI resolveLatestSparkAllJar(HttpClient client) throws Exception {
        String groupPath = GROUP_ID.replace('.', '/');
        String basePath = trimTrailingSlash(REPO_BASE) + "/" + groupPath + "/" + ARTIFACT_ID + "/" + VERSION;

        URI metadataUri = URI.create(basePath + "/maven-metadata.xml");
        String xml = httpGetString(client, metadataUri);

        // Prefer snapshotVersions (most reliable)
        String snapshotValue = xpathValue(xml,
                "//snapshotVersion[classifier='" + CLASSIFIER + "' and extension='" + EXTENSION + "']/value");

        if (snapshotValue == null || snapshotValue.isBlank()) {
            // Fallback: build from timestamp/buildNumber if snapshotVersions isn't present
            String ts = xpathValue(xml, "/metadata/versioning/snapshot/timestamp");
            String bn = xpathValue(xml, "/metadata/versioning/snapshot/buildNumber");
            if (ts == null || bn == null) {
                throw new IllegalStateException("Could not resolve latest snapshot from metadata (no snapshotVersions, no timestamp/buildNumber).");
            }
            // Maven snapshot naming: baseVersion (without -SNAPSHOT) + -timestamp-buildNumber
            String baseVersion = VERSION.substring(0, VERSION.length() - "-SNAPSHOT".length());
            snapshotValue = baseVersion + "-" + ts + "-" + bn;
        }

        String fileName = ARTIFACT_ID + "-" + snapshotValue + "-" + CLASSIFIER + "." + EXTENSION;
        return URI.create(basePath + "/" + fileName);
    }

    private static String xpathValue(String xml, String expression) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // basic XXE hardening
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setNamespaceAware(false);

        Document doc;
        try (ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            doc = dbf.newDocumentBuilder().parse(in);
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        return node == null ? null : node.getTextContent().trim();
    }

    private static String httpGetString(HttpClient client, URI uri) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("GET failed: " + uri + " (" + resp.statusCode() + ")");
        }
        return resp.body();
    }

    private static void httpDownload(HttpClient client, URI uri, Path target) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(Duration.ofMinutes(2))
                .build();

        HttpResponse<Path> resp = client.send(req, HttpResponse.BodyHandlers.ofFile(target));
        if (resp.statusCode() != 200) {
            throw new IOException("Download failed: " + uri + " (" + resp.statusCode() + ")");
        }
    }

    private static String trimTrailingSlash(String s) {
        if (s == null) return null;
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
