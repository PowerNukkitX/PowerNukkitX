package org.powernukkitx.resourcepacks.manifest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Structured model of a Bedrock pack {@code manifest.json}.
 * <p>
 * Parsing is deliberately tolerant, because manifests are authored by pack creators
 * rather than generated: unknown fields are ignored, and a malformed optional section is
 * skipped instead of failing the whole pack. Only the header is strict, since a pack
 * without a name, uuid and version cannot be offered to a client at all.
 *
 * @param formatVersion the manifest {@code format_version}, {@code "1"} when absent
 * @param modules       the pack's modules; the module types decide which stack the pack joins
 * @param dependencies  other packs and engine modules this pack requires
 * @param capabilities  declared capabilities such as {@code raytraced}
 * @param subPacks      subpacks offered for different memory tiers, empty when none
 */
public record PackManifest(
        String formatVersion,
        Header header,
        List<Module> modules,
        List<Dependency> dependencies,
        List<String> capabilities,
        List<SubPack> subPacks
) {

    /**
     * A manifest version triple.
     *
     * @param raw the version exactly as written in the manifest, which keeps any
     *            pre-release suffix that {@link #toString()} drops
     */
    public record SemVersion(int major, int minor, int patch, String raw) {

        public static final SemVersion ZERO = new SemVersion(0, 0, 0, "0.0.0");

        @Override
        public @NonNull String toString() {
            return major + "." + minor + "." + patch;
        }

        /**
         * Reads a version in either manifest encoding: the array form {@code [1, 2, 3]}
         * or the string form {@code "1.2.3"}, optionally with a pre-release suffix such
         * as {@code "1.2.3-beta"}.
         *
         * @param element the version element, may be null or JSON null
         * @return the parsed version, or {@link #ZERO} when it is absent or unreadable;
         * missing or non-numeric components become {@code 0} rather than throwing
         */
        public static SemVersion from(@Nullable JsonElement element) {
            if (element == null || element.isJsonNull()) {
                return ZERO;
            }
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                int major = array.size() > 0 ? asInt(array.get(0)) : 0;
                int minor = array.size() > 1 ? asInt(array.get(1)) : 0;
                int patch = array.size() > 2 ? asInt(array.get(2)) : 0;
                return new SemVersion(major, minor, patch, major + "." + minor + "." + patch);
            }
            if (element.isJsonPrimitive()) {
                String raw = element.getAsString();
                String[] parts = raw.split("-", 2)[0].split("\\.");
                int major = parts.length > 0 ? parseIntSafe(parts[0]) : 0;
                int minor = parts.length > 1 ? parseIntSafe(parts[1]) : 0;
                int patch = parts.length > 2 ? parseIntSafe(parts[2]) : 0;
                return new SemVersion(major, minor, patch, raw);
            }
            return ZERO;
        }

        private static int asInt(JsonElement element) {
            try {
                return element.getAsInt();
            } catch (RuntimeException e) {
                return 0;
            }
        }

        private static int parseIntSafe(String value) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    /**
     * @param description the header description, empty when the manifest omits it
     * @param minEngineVersion the lowest game version the pack declares support for,
     *                         {@link SemVersion#ZERO} when absent
     */
    public record Header(
            String name,
            String description,
            UUID uuid,
            SemVersion version,
            SemVersion minEngineVersion
    ) {}

    /**
     * The module kinds that may appear in a manifest. Anything unrecognised parses as
     * {@link #UNKNOWN} so that a pack using a newer module type still loads.
     */
    public enum ModuleType {
        RESOURCES,
        DATA,
        CLIENT_DATA,
        INTERFACE,
        WORLD_TEMPLATE,
        SCRIPT,
        SKIN_PACK,
        UNKNOWN;

        /**
         * @param value the raw {@code type} string, matched case-insensitively
         * @return the matching type, or {@link #UNKNOWN} for null and unrecognised values
         */
        public static ModuleType from(@Nullable String value) {
            if (value == null) {
                return UNKNOWN;
            }
            return switch (value.toLowerCase(Locale.ENGLISH)) {
                case "resources" -> RESOURCES;
                case "data" -> DATA;
                case "client_data" -> CLIENT_DATA;
                case "interface" -> INTERFACE;
                case "world_template" -> WORLD_TEMPLATE;
                case "script", "javascript" -> SCRIPT;
                case "skin_pack" -> SKIN_PACK;
                default -> UNKNOWN;
            };
        }
    }

    /**
     * @param uuid     the module uuid, null when the manifest omits it or it is malformed
     * @param entry    the script entry point (for example {@code scripts/main.js}), empty
     *                 for non-script modules
     * @param language the script language (for example {@code javascript}), empty for
     *                 non-script modules
     */
    public record Module(
            ModuleType type,
            @Nullable UUID uuid,
            SemVersion version,
            String description,
            String entry,
            String language
    ) {}

    /**
     * A manifest dependency. Exactly one of {@link #uuid()} (a dependency on another pack)
     * and {@link #moduleName()} (a dependency on an engine module such as
     * {@code @minecraft/server}) is set.
     */
    public record Dependency(
            @Nullable UUID uuid,
            @Nullable String moduleName,
            SemVersion version
    ) {

        public boolean isPackDependency() {
            return uuid != null;
        }

        public boolean isModuleDependency() {
            return moduleName != null && !moduleName.isEmpty();
        }
    }

    /**
     * @param memoryTier the memory tier the subpack targets, {@code 0} when unspecified
     */
    public record SubPack(String folderName, String name, int memoryTier) {}

    /**
     * @return true when any module is a {@code data} or {@code script} module, meaning the
     * pack belongs on the behavior side of the pack stack
     */
    public boolean isBehaviorPack() {
        for (Module module : modules) {
            if (module.type() == ModuleType.DATA || module.type() == ModuleType.SCRIPT) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true when the pack ships a script module, which clients are told about so
     * they can apply the stricter rules that scripted packs require
     */
    public boolean hasScripts() {
        for (Module module : modules) {
            if (module.type() == ModuleType.SCRIPT) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param capability the capability name, matched case-insensitively
     * @return true when the manifest declares it
     */
    public boolean hasCapability(String capability) {
        for (String value : capabilities) {
            if (value.equalsIgnoreCase(capability)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a manifest from the root object of a {@code manifest.json}.
     *
     * @param json the parsed manifest document
     * @return the manifest model, with every list unmodifiable and empty when its section
     * is absent
     * @throws IllegalArgumentException when the header is missing, or is missing any of
     *                                  {@code name}, {@code uuid} and {@code version}, or
     *                                  when the uuid is not a valid UUID
     */
    public static @NotNull PackManifest fromJson(@NotNull JsonObject json) {
        String formatVersion = json.has("format_version") ? json.get("format_version").getAsString() : "1";

        JsonObject headerJson = json.getAsJsonObject("header");
        if (headerJson == null || !headerJson.has("uuid") || !headerJson.has("version") || !headerJson.has("name")) {
            throw new IllegalArgumentException("Pack manifest header is missing required fields (name, uuid, version)");
        }
        Header header = new Header(
                headerJson.get("name").getAsString(),
                optionalString(headerJson, "description"),
                UUID.fromString(headerJson.get("uuid").getAsString().trim()),
                SemVersion.from(headerJson.get("version")),
                SemVersion.from(headerJson.get("min_engine_version"))
        );

        List<Module> modules = new ArrayList<>();
        if (json.has("modules") && json.get("modules").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("modules")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject moduleJson = element.getAsJsonObject();
                modules.add(new Module(
                        ModuleType.from(optionalString(moduleJson, "type")),
                        optionalUuid(moduleJson, "uuid"),
                        SemVersion.from(moduleJson.get("version")),
                        optionalString(moduleJson, "description"),
                        optionalString(moduleJson, "entry"),
                        optionalString(moduleJson, "language")
                ));
            }
        }

        List<Dependency> dependencies = new ArrayList<>();
        if (json.has("dependencies") && json.get("dependencies").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("dependencies")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject dependencyJson = element.getAsJsonObject();
                String moduleName = optionalString(dependencyJson, "module_name");
                dependencies.add(new Dependency(
                        optionalUuid(dependencyJson, "uuid"),
                        moduleName.isEmpty() ? null : moduleName,
                        SemVersion.from(dependencyJson.get("version"))
                ));
            }
        }

        List<String> capabilities = new ArrayList<>();
        if (json.has("capabilities") && json.get("capabilities").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("capabilities")) {
                if (element.isJsonPrimitive()) {
                    capabilities.add(element.getAsString());
                }
            }
        }

        List<SubPack> subPacks = new ArrayList<>();
        if (json.has("subpacks") && json.get("subpacks").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("subpacks")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject subPackJson = element.getAsJsonObject();
                int memoryTier = 0;
                if (subPackJson.has("memory_tier")) {
                    try {
                        memoryTier = subPackJson.get("memory_tier").getAsInt();
                    } catch (RuntimeException ignored) {
                    }
                }
                subPacks.add(new SubPack(
                        optionalString(subPackJson, "folder_name"),
                        optionalString(subPackJson, "name"),
                        memoryTier
                ));
            }
        }

        return new PackManifest(
                formatVersion,
                header,
                Collections.unmodifiableList(modules),
                Collections.unmodifiableList(dependencies),
                Collections.unmodifiableList(capabilities),
                Collections.unmodifiableList(subPacks)
        );
    }

    private static String optionalString(JsonObject json, String key) {
        if (json.has(key) && json.get(key).isJsonPrimitive()) {
            return json.get(key).getAsString();
        }
        return "";
    }

    private static @Nullable UUID optionalUuid(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return null;
        }
        try {
            return UUID.fromString(json.get(key).getAsString().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
