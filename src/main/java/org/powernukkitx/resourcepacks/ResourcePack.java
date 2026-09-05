package org.powernukkitx.resourcepacks;

import org.powernukkitx.resourcepacks.manifest.PackManifest;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

/**
 * Interface describing a resource pack
 */
public interface ResourcePack {


    ResourcePack[] EMPTY_ARRAY = new ResourcePack[0];

    /**
     * @return which side of the pack stack this pack joins; {@link PackType#RESOURCES}
     * unless the implementation says otherwise
     */
    default PackType getType() {
        return PackType.RESOURCES;
    }

    /**
     * @return the parsed {@code manifest.json} of this pack, or null when the
     * implementation has none to expose, as with synthetic and externally hosted packs
     */
    default @Nullable PackManifest getPackManifest() {
        return null;
    }

    /**
     * @return the zip file backing this pack on disk, or null for packs that have no
     * standalone file, such as jar-embedded, synthetic and externally hosted packs
     */
    default @Nullable File getFile() {
        return null;
    }

    /**
     * @return The name of this resource pack
     */
    String getPackName();


    default String getSubPackName() {
        return "";
    }

    /**
     * @return The UUID of this resource pack
     */
    UUID getPackId();

    /**
     * @return The version number of this resource pack
     */
    String getPackVersion();

    /**
     * @return The file size of this resource pack
     */
    int getPackSize();

    /**
     * @return The SHA-256 hash of the resource pack file
     */
    byte[] getSha256();

    /**
     * @param off Offset value
     * @param len Length
     * @return The specified chunk of the resource pack file
     */
    byte[] getPackChunk(int off, int len);

    default boolean isAddonPack(){
        return false;
    }

    default String cdnUrl(){
        return "";
    }

    default boolean isRaytracingCapable() {
        return false;
    }

    default boolean usesScript(){
        return false;
    }

    /**
     * @return Resource pack key (if encrypted)
     */
    default String getEncryptionKey() {
        return "";
    }
}