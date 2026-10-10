package org.powernukkitx.resourcepacks;

import java.util.UUID;

/**
 * A resource pack that is hosted externally. The client downloads the file from the given url
 * instead of receiving it chunk by chunk over the game connection.
 */
public class CdnResourcePack implements ResourcePack {

    private static final byte[] NO_HASH = new byte[0];

    private final String name;
    private final UUID id;
    private final String version;
    private final String url;
    private final int size;
    private final String encryptionKey;
    private final String subPackName;
    private final boolean addonPack;
    private final boolean scripts;
    private final boolean raytracing;

    public CdnResourcePack(String name, UUID id, String version, String url, int size, String encryptionKey,
                           String subPackName, boolean addonPack, boolean scripts, boolean raytracing) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.url = url;
        this.size = size;
        this.encryptionKey = encryptionKey;
        this.subPackName = subPackName;
        this.addonPack = addonPack;
        this.scripts = scripts;
        this.raytracing = raytracing;
    }

    @Override
    public String getPackName() {
        return this.name;
    }

    @Override
    public UUID getPackId() {
        return this.id;
    }

    @Override
    public String getPackVersion() {
        return this.version;
    }

    @Override
    public int getPackSize() {
        return this.size;
    }

    @Override
    public byte[] getSha256() {
        return NO_HASH;
    }

    /**
     * CDN packs are never uploaded through the game connection, the client fetches them itself.
     */
    @Override
    public byte[] getPackChunk(int off, int len) {
        return NO_HASH;
    }

    @Override
    public String cdnUrl() {
        return this.url;
    }

    @Override
    public String getSubPackName() {
        return this.subPackName;
    }

    @Override
    public boolean isAddonPack() {
        return this.addonPack;
    }

    @Override
    public boolean usesScript() {
        return this.scripts;
    }

    @Override
    public boolean isRaytracingCapable() {
        return this.raytracing;
    }

    @Override
    public String getEncryptionKey() {
        return this.encryptionKey;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePack anotherPack && this.id.equals(anotherPack.getPackId());
    }
}
