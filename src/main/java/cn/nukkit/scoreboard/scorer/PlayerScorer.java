package cn.nukkit.scoreboard.scorer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;
import lombok.Getter;

import java.util.UUID;

@PowerNukkitXOnly
@Since("1.19.30-r1")
@Getter
public class PlayerScorer implements IScorer {

    private UUID uuid;

    public PlayerScorer(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerScorer(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public PlayerScorer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        if (uuid == null) return null;
        return Server.getInstance().getPlayer(uuid).isPresent() ? Server.getInstance().getPlayer(uuid).get() : null;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public ScorerType getScorerType() {
        return ScorerType.PLAYER;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerScorer playerScorer) {
            return uuid.equals(playerScorer.uuid);
        }
        return false;
    }

    @Override
    public String getName() {
        return Server.getInstance().getOnlinePlayers().get(uuid) == null ? String.valueOf(uuid.getMostSignificantBits()) : Server.getInstance().getOnlinePlayers().get(uuid).getName();
    }

    @Override
    public SetScorePacket.ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        if (uuid == null) return null;
        return Server.getInstance().getPlayer(uuid).isPresent() ? new SetScorePacket.ScoreInfo(line.getLineId(), scoreboard.getObjectiveName(), line.getScore(), ScorerType.PLAYER, Server.getInstance().getPlayer(uuid).get().getId()) : null;
    }
}
