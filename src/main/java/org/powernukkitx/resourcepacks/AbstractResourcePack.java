package org.powernukkitx.resourcepacks;

import org.powernukkitx.resourcepacks.manifest.PackManifest;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Slf4j
public abstract class AbstractResourcePack implements ResourcePack {
    protected JsonObject manifest;
    protected PackManifest packManifest;
    protected UUID id = null;

    /**
     * Whether this pack came out of an addon container rather than the resource pack
     * directory. Clients treat addon packs differently, so it is reported to them.
     */
    protected boolean addonSource = false;

    @Override
    public String getPackName() {
        return getOrParseManifest().header().name();
    }

    @Override
    public UUID getPackId() {
        if (id == null) {
            id = getOrParseManifest().header().uuid();
        }
        return id;
    }

    @Override
    public String getPackVersion() {
        return getOrParseManifest().header().version().toString();
    }

    /**
     * @return {@link PackType#BEHAVIOR} when the manifest declares a data or script
     * module, {@link PackType#RESOURCES} otherwise
     */
    @Override
    public PackType getType() {
        return getOrParseManifest().isBehaviorPack() ? PackType.BEHAVIOR : PackType.RESOURCES;
    }

    @Override
    public @Nullable PackManifest getPackManifest() {
        return getOrParseManifest();
    }

    @Override
    public boolean usesScript() {
        return getOrParseManifest().hasScripts();
    }

    @Override
    public boolean isRaytracingCapable() {
        return getOrParseManifest().hasCapability("raytraced");
    }

    @Override
    public boolean isAddonPack() {
        return this.addonSource;
    }

    /**
     * Marks this pack as having been loaded out of an addon container. Set by the loader
     * before the pack is handed to the pack manager.
     */
    public void setAddonSource(boolean addonSource) {
        this.addonSource = addonSource;
    }

    /**
     * @return the parsed manifest, parsing it on first use
     * @throws IllegalArgumentException when the manifest is malformed; call
     *                                  {@link #verifyManifest()} first to check instead
     */
    protected PackManifest getOrParseManifest() {
        if (this.packManifest == null) {
            this.packManifest = PackManifest.fromJson(this.manifest);
        }
        return this.packManifest;
    }

    protected boolean verifyManifest() {
        if (this.manifest == null || !this.manifest.has("format_version")
                || !this.manifest.has("header") || !this.manifest.has("modules")) {
            return false;
        }
        try {
            getOrParseManifest();
            return true;
        } catch (RuntimeException e) {
            log.warn("Invalid pack manifest", e);
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getPackId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePack anotherPack && this.getPackId().equals(anotherPack.getPackId());
    }
}
