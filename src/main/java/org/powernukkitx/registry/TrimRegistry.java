package org.powernukkitx.registry;

import org.powernukkitx.Server;
import org.powernukkitx.utils.JSONUtils;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.TrimMaterial;
import org.cloudburstmc.protocol.bedrock.data.TrimPattern;
import org.cloudburstmc.protocol.bedrock.packet.TrimDataPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TrimRegistry implements IRegistry<String, TrimPattern, TrimPattern> {

    private static final Object2ObjectOpenHashMap<String, TrimPattern> PATTERNS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, TrimMaterial> MATERIALS = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var stream = TrimRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/trim_data.json")) {
            JsonObject obj = JSONUtils.from(stream, JsonObject.class);
            for (var e : obj.getAsJsonArray("patterns").asList()) {
                JsonObject o = e.getAsJsonObject();
                registerPattern(o.get("itemName").getAsString(), o.get("patternId").getAsString());
            }
            for (var e : obj.getAsJsonArray("materials").asList()) {
                JsonObject o = e.getAsJsonObject();
                registerMaterial(o.get("itemName").getAsString(),
                        o.get("materialId").getAsString(),
                        o.get("color").getAsString());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the pattern registered for the given template item id, or {@code null}.
     */
    @Override
    public TrimPattern get(String templateItemId) {
        return PATTERNS.get(templateItemId);
    }

    public Optional<TrimPattern> getPattern(@NotNull String templateItemId) {
        return Optional.ofNullable(PATTERNS.get(templateItemId));
    }

    public Optional<TrimMaterial> getMaterial(@NotNull String ingredientItemId) {
        return Optional.ofNullable(MATERIALS.get(ingredientItemId));
    }

    /**
     * Registers a trim pattern.
     *
     * @param templateItemId the smithing template item id placed in the template slot
     * @param patternId      the id written into the item's trim NBT
     * @throws RegisterException if the template item id is already registered
     */
    public TrimPattern registerPattern(@NotNull String templateItemId, @NotNull String patternId) throws RegisterException {
        TrimPattern pattern = new TrimPattern(templateItemId, patternId);
        register(templateItemId, pattern);
        return pattern;
    }

    /**
     * Registers a trim material.
     *
     * @param ingredientItemId the ingredient item id placed in the material slot
     * @param materialId       the id written into the item's trim NBT
     * @param color            the hex color (with leading {@code #}) used for the item name tint
     * @throws RegisterException if the ingredient item id is already registered
     */
    public TrimMaterial registerMaterial(@NotNull String ingredientItemId, @NotNull String materialId, @NotNull String color) throws RegisterException {
        TrimMaterial material = new TrimMaterial(materialId, color, ingredientItemId);
        if (MATERIALS.putIfAbsent(ingredientItemId, material) != null) {
            throw new RegisterException("Trim material already registered for item " + ingredientItemId);
        }
        return material;
    }

    @Override
    public void register(String templateItemId, TrimPattern pattern) throws RegisterException {
        if (PATTERNS.putIfAbsent(templateItemId, pattern) != null) {
            throw new RegisterException("Trim pattern already registered for item " + templateItemId);
        }
    }

    public boolean unregisterPattern(@NotNull String templateItemId) {
        return PATTERNS.remove(templateItemId) != null;
    }

    public boolean unregisterMaterial(@NotNull String ingredientItemId) {
        return MATERIALS.remove(ingredientItemId) != null;
    }

    public Map<String, TrimPattern> getPatterns() {
        return Collections.unmodifiableMap(PATTERNS);
    }

    public Map<String, TrimMaterial> getMaterials() {
        return Collections.unmodifiableMap(MATERIALS);
    }

    public TrimDataPacket buildPacket() {
        TrimDataPacket packet = new TrimDataPacket();
        packet.getTrimMaterialList().addAll(MATERIALS.values());
        packet.getTrimPatternList().addAll(PATTERNS.values());
        return packet;
    }

    public void resyncOnlinePlayers() {
        Collection<? extends org.powernukkitx.Player> players = Server.getInstance().getOnlinePlayers().values();
        if (players.isEmpty()) return;
        TrimDataPacket packet = buildPacket();
        for (var player : players) {
            player.sendPacket(packet);
        }
    }

    @Override
    public void trim() {
        PATTERNS.trim();
        MATERIALS.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        PATTERNS.clear();
        MATERIALS.clear();
        init();
    }
}
