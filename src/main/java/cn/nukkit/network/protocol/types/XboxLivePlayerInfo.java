package cn.nukkit.network.protocol.types;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.ClientChainData;
import lombok.Getter;

import java.util.UUID;

public class XboxLivePlayerInfo extends PlayerInfo {
    @Getter
    private final String xuid;

    public XboxLivePlayerInfo(
            String username,
            UUID uuid,
            Skin skin,
            ClientChainData data,
            String xuid
    ) {
        super(username, uuid, skin, data);
        this.xuid = xuid;
    }
}
