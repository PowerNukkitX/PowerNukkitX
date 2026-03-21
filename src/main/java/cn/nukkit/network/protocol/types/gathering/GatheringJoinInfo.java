package cn.nukkit.network.protocol.types.gathering;

import lombok.Data;

import java.util.UUID;

@Data
public class GatheringJoinInfo {

    private UUID experienceID;
    private String experienceName;
    private UUID experienceWorldID;
    private String experienceWorldName;
    private String creatorID;
    /**
     * @since v935
     */
    private UUID unk;
    /**
     * @since v935
     */
    private UUID unk1;
    /**
     * @since v935
     */
    private String serverID;
}