package cn.nukkit.resourcepacks;

import java.util.UUID;

/**
 * 描述一个资源包的接口
 */
public interface ResourcePack {


    ResourcePack[] EMPTY_ARRAY = new ResourcePack[0];

    /**
     * @return 此资源包的名称
     */
    String getPackName();

    /**
     * @return 此资源包的UUID
     */
    UUID getPackId();

    /**
     * @return 此资源包的版本号
     */
    String getPackVersion();

    /**
     * @return 此资源包的文件大小
     */
    int getPackSize();

    /**
     * @return 资源包文件的SHA-256值
     */
    byte[] getSha256();

    /**
     * @param off 偏移值
     * @param len 长度
     * @return 资源包文件的指定分块
     */
    byte[] getPackChunk(int off, int len);

    /**
     * @return 资源包密钥（若加密）
     */
    default String getEncryptionKey() {
        return "";
    }
}
