package cn.nukkit.resourcepacks;

import java.util.UUID;

/**
 * Interface describing a resource pack
 */
public interface ResourcePack {


    ResourcePack[] EMPTY_ARRAY = new ResourcePack[0];

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