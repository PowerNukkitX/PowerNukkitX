package org.powernukkitx.scoreboard.scorer;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ChangePlayerScore;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;

import java.util.UUID;


@Getter
public class PlayerScorer implements IScorer {

    private final long uniqueId;

    public PlayerScorer(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Returns the player ActorUniqueID as its persisted string representation.
     *
     * @return ActorUniqueID string
     */
    public String uniqueId() {
        return Long.toString(this.uniqueId);
    }

    /**
     * Returns the player ActorUniqueID.
     *
     * @return ActorUniqueID
     */
    public long uniqueIdLong() {
        return this.uniqueId;
    }

    @Deprecated
    public PlayerScorer(UUID uuid) {
        this.uniqueId = Server.getInstance().resolvePlayerUniqueId(uuid);
    }

    public PlayerScorer(String identity) {
        long uniqueId;

        try {
            uniqueId = Long.parseLong(identity);
        } catch (NumberFormatException e) {
            uniqueId = Server.getInstance().resolvePlayerUniqueId(UUID.fromString(identity));
        }

        this.uniqueId = uniqueId;
    }

    public PlayerScorer(Player player) {
        this.uniqueId = player.uniqueIdLong();
    }

    @Deprecated
    public UUID getUuid() {
        return Server.getInstance().getPlayerUuidByUniqueId(this.uniqueId).orElse(null);
    }

    public Player getPlayer() {
        return Server.getInstance().getPlayerByUniqueId(this.uniqueId).orElse(null);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public ScorePacketEntryAction getScorerType() {
        return ScorePacketEntryAction.CHANGE_PLAYER;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.uniqueId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerScorer playerScorer) {
            return this.uniqueId == playerScorer.uniqueId;
        }
        return false;
    }

    @Override
    public String getName() {
        Player player = getPlayer();

        if (player != null) {
            return player.getName();
        }

        UUID uuid = getUuid();
        return uuid != null ? String.valueOf(uuid.getMostSignificantBits()) : String.valueOf(this.uniqueId);
    }

    @Override
    public ChangePlayerScore toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        if (!isOnline()) {
            return null;
        }

        final ChangePlayerScore score = new ChangePlayerScore();
        score.setScoreboardId(line.getLineId());
        score.setObjectiveName(scoreboard.getObjectiveName());
        score.setScoreValue(line.getScore());
        score.setPlayerUniqueId(this.uniqueIdLong());
        return score;
    }
}
