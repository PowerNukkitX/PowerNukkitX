package cn.nukkit;

import cn.nukkit.level.PlayerChunkManager;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.jetbrains.annotations.NotNull;

public class TestPlayer extends Player {
    public TestPlayer(@NotNull BedrockServerSession session, @NotNull PlayerInfo info) {
        super(session, info);
    }

    public PlayerChunkManager getPlayerChunkManager() {
        return playerChunkManager;
    }
}
