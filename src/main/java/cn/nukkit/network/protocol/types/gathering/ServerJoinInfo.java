package cn.nukkit.network.protocol.types.gathering;

import lombok.Data;

@Data
public class ServerJoinInfo {

    private GatheringJoinInfo gatheringJoinInfo;
    private StoreEntryPointInfo storeEntryPointInfo;
    private PresenceInfo presenceInfo;
}