package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.Server;
import org.powernukkitx.resourcepacks.ResourcePack;
import org.powernukkitx.resourcepacks.ZippedResourcePack;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Loads Bedrock addons from a directory.
 * <p>
 * An {@code .mcaddon} is a zip container bundling several packs at once - typically a
 * behavior pack plus the resource pack it depends on - held either as sub-directories
 * that each contain a {@code manifest.json}, or as nested {@code .mcpack}/{@code .zip}
 * files. Clients download one pack at a time, so a container is first split into
 * individual {@code .mcpack} files. The split result is cached in a {@code .cache}
 * directory beside the addon, keyed by the container's name, size and modification time,
 * so that editing an addon invalidates its cache on the next load; cache directories with
 * no matching addon are removed.
 * <p>
 * Plain {@code .mcpack}/{@code .zip} files in the directory are loaded directly. Every
 * pack loaded here is flagged as an addon pack, and whether it joins the resource or the
 * behavior stack is decided by its own manifest.
 * <p>
 * A pack that fails to load is logged and skipped; one broken addon never stops the rest
 * of the directory from loading.
 */
@Slf4j
public class AddonPackLoader implements ResourcePackLoader {

    private static final String CACHE_DIR_NAME = ".cache";
    private static final String MANIFEST_NAME = "manifest.json";

    protected final File path;
    protected final File cacheDir;

    /**
     * @param path the directory to load addons from; it is created when missing
     * @throws IllegalArgumentException when the path exists and is not a directory
     */
    public AddonPackLoader(File path) {
        this.path = path;
        this.cacheDir = new File(path, CACHE_DIR_NAME);
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
        List<ResourcePack> loadedPacks = new ArrayList<>();
        Set<String> liveCacheKeys = new HashSet<>();

        File[] files = path.listFiles();
        if (files == null) {
            return loadedPacks;
        }

        for (File file : files) {
            if (file.isDirectory() || file.getName().startsWith(".")) {
                continue;
            }
            String extension = Files.getFileExtension(file.getName()).toLowerCase(Locale.ENGLISH);
            try {
                switch (extension) {
                    case "mcaddon" -> {
                        String cacheKey = cacheKeyFor(file);
                        liveCacheKeys.add(cacheKey);
                        File addonCache = new File(cacheDir, cacheKey);
                        if (!addonCache.isDirectory()) {
                            splitAddon(file, addonCache);
                        }
                        loadedPacks.addAll(loadSplitPacks(file, addonCache));
                    }
                    case "mcpack", "zip" -> {
                        ZippedResourcePack pack = new ZippedResourcePack(file);
                        pack.setAddonSource(true);
                        loadedPacks.add(pack);
                        log.info(baseLang.tr("nukkit.resources.addon.loaded", pack.getPackName(), file.getName()));
                    }
                    case "key" -> {
                    }
                    default -> log.warn(baseLang.tr("nukkit.resources.unknown-format", file.getName()));
                }
            } catch (IOException | RuntimeException e) {
                log.warn(baseLang.tr("nukkit.resources.fail", file.getName(), String.valueOf(e.getMessage())), e);
            }
        }

        cleanStaleCache(liveCacheKeys);
        return loadedPacks;
    }

    private List<ResourcePack> loadSplitPacks(File addonFile, File addonCache) {
        var baseLang = Server.getInstance().getLanguage();
        List<ResourcePack> packs = new ArrayList<>();
        File[] packFiles = addonCache.listFiles((dir, name) -> name.toLowerCase(Locale.ENGLISH).endsWith(".mcpack"));
        if (packFiles == null || packFiles.length == 0) {
            log.warn(baseLang.tr("nukkit.resources.addon.empty", addonFile.getName()));
            return packs;
        }
        for (File packFile : packFiles) {
            try {
                ZippedResourcePack pack = new ZippedResourcePack(packFile);
                pack.setAddonSource(true);
                packs.add(pack);
                log.info(baseLang.tr("nukkit.resources.addon.split.loaded",
                        pack.getPackName(), packFile.getName(), addonFile.getName()));
            } catch (RuntimeException e) {
                log.warn(baseLang.tr("nukkit.resources.fail", packFile.getName(), String.valueOf(e.getMessage())), e);
            }
        }
        return packs;
    }

    /**
     * Splits an addon container into one {@code .mcpack} per contained pack.
     *
     * @param addonFile the {@code .mcaddon} container
     * @param targetDir the cache directory to write the split packs into
     * @throws IOException when the cache directory cannot be created, the container
     *                     cannot be read, or it holds neither a manifest nor a nested pack
     */
    protected void splitAddon(File addonFile, File targetDir) throws IOException {
        if (!targetDir.mkdirs() && !targetDir.isDirectory()) {
            throw new IOException("Could not create addon cache directory " + targetDir);
        }
        try (ZipFile zip = new ZipFile(addonFile, StandardCharsets.UTF_8)) {
            boolean foundNested = false;
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String lowerName = entry.getName().toLowerCase(Locale.ENGLISH);
                if (lowerName.endsWith(".mcpack") || lowerName.endsWith(".zip")) {
                    foundNested = true;
                    File out = new File(targetDir, sanitizeFileName(baseName(entry.getName())) + ".mcpack");
                    try (InputStream in = zip.getInputStream(entry); OutputStream os = new FileOutputStream(out)) {
                        in.transferTo(os);
                    }
                }
            }

            Set<String> packPrefixes = findPackPrefixes(zip);
            for (String prefix : packPrefixes) {
                String packName = prefix.isEmpty()
                        ? stripExtension(addonFile.getName())
                        : trimTrailingSlash(prefix).replace('/', '_');
                writeSubZip(zip, prefix, new File(targetDir, sanitizeFileName(packName) + ".mcpack"));
            }

            if (!foundNested && packPrefixes.isEmpty()) {
                throw new IOException(Server.getInstance().getLanguage().tr("nukkit.resources.zip.no-manifest"));
            }
        }
    }

    /**
     * Finds the directory prefixes inside the container that are pack roots, that is the
     * directories holding a {@code manifest.json}. A manifest below an already-found root
     * belongs to that pack's own content, such as a subpack, and is not a root itself.
     *
     * @return the pack root prefixes, shortest first; an empty prefix means the container
     * is itself a single pack
     */
    private Set<String> findPackPrefixes(ZipFile zip) {
        Set<String> prefixes = new LinkedHashSet<>();
        List<String> manifestDirs = zip.stream()
                .filter(entry -> !entry.isDirectory())
                .map(ZipEntry::getName)
                .filter(name -> {
                    String lower = name.toLowerCase(Locale.ENGLISH);
                    return lower.equals(MANIFEST_NAME) || lower.endsWith("/" + MANIFEST_NAME);
                })
                .map(name -> name.substring(0, name.length() - MANIFEST_NAME.length()))
                .sorted(Comparator.comparingInt(String::length))
                .toList();
        for (String dir : manifestDirs) {
            boolean nestedInExisting = false;
            for (String existing : prefixes) {
                if (dir.startsWith(existing)) {
                    nestedInExisting = true;
                    break;
                }
            }
            if (!nestedInExisting) {
                prefixes.add(dir);
            }
        }
        return prefixes;
    }

    private void writeSubZip(ZipFile source, String prefix, File target) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target), StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = source.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith(prefix)) {
                    continue;
                }
                String relative = entry.getName().substring(prefix.length());
                if (relative.isEmpty()) {
                    continue;
                }
                String lower = relative.toLowerCase(Locale.ENGLISH);
                if (lower.endsWith(".mcpack") || lower.endsWith(".zip")) {
                    continue;
                }
                out.putNextEntry(new ZipEntry(relative));
                try (InputStream in = source.getInputStream(entry)) {
                    in.transferTo(out);
                }
                out.closeEntry();
            }
        }
    }

    /**
     * Builds the cache key for an addon container. The key covers the container's
     * identity, so replacing the file with a different version produces a different key
     * and the stale split is discarded rather than reused.
     */
    protected String cacheKeyFor(File file) {
        String identity = file.getName() + ":" + file.length() + ":" + file.lastModified();
        return sanitizeFileName(stripExtension(file.getName())) + "-" + shortHash(identity);
    }

    private void cleanStaleCache(Set<String> liveCacheKeys) {
        File[] cached = cacheDir.listFiles();
        if (cached == null) {
            return;
        }
        for (File dir : cached) {
            if (dir.isDirectory() && !liveCacheKeys.contains(dir.getName())) {
                deleteRecursively(dir.toPath());
            }
        }
    }

    private static void deleteRecursively(Path root) {
        try (var stream = java.nio.file.Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(entry -> {
                try {
                    java.nio.file.Files.deleteIfExists(entry);
                } catch (IOException e) {
                    log.debug("Could not delete stale addon cache file {}", entry, e);
                }
            });
        } catch (IOException e) {
            log.debug("Could not clean stale addon cache {}", root, e);
        }
    }

    private static String shortHash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                hex.append(String.format("%02x", digest[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(value.hashCode());
        }
    }

    private static String baseName(String entryName) {
        int slash = entryName.lastIndexOf('/');
        return stripExtension(slash >= 0 ? entryName.substring(slash + 1) : entryName);
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    /**
     * Reduces a pack or entry name to a safe file name. Names come from inside the
     * container, so this also keeps path separators and traversal sequences out of the
     * cache directory.
     */
    private static String sanitizeFileName(String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9._ -]", "_").trim();
        return sanitized.isEmpty() ? "pack" : sanitized;
    }
}
