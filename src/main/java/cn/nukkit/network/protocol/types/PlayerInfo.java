package cn.nukkit.network.protocol.types;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.ClientChainData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerInfo {
    private final String username;
    private final UUID uniqueId;
    private final Skin skin;
    private final ClientChainData data;
}
