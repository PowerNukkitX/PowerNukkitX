package cn.nukkit.scoreboard.scorer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.Scorer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerScorer implements Scorer {

    private UUID uuid;

    public PlayerScorer(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerScorer(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public PlayerScorer(Player player){
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
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
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerScorer playerScorer) {
            return uuid.equals(playerScorer.uuid);
        }
        return false;
    }

    @Override
    public String getName() {
        return Server.getInstance().getOnlinePlayers().get(uuid) == null ? String.valueOf(uuid.getMostSignificantBits()) : Server.getInstance().getOnlinePlayers().get(uuid).getName();
    }
}
