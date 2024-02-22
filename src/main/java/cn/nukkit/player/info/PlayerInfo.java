package cn.nukkit.player.info;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.ClientChainData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class PlayerInfo {
    @Getter
    private final String username;
    @Getter
    private final UUID uniqueId;
    @Getter
    private final Skin skin;
    @Getter
    private final ClientChainData data;
}
