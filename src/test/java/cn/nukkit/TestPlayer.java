package cn.nukkit;

import cn.nukkit.level.PlayerChunkManager;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.types.PlayerInfo;
import org.jetbrains.annotations.NotNull;

public class TestPlayer extends Player {
    public TestPlayer(@NotNull BedrockSession session, @NotNull PlayerInfo info) {
        super(session, info);
    }

    public PlayerChunkManager getPlayerChunkManager() {
        return playerChunkManager;
    }
}
