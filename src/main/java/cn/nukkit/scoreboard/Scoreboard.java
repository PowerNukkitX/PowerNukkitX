package cn.nukkit.scoreboard;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Scoreboard {

    private Map<Scorer,ScoreboardLine> lines;
    private String objectiveName;
    private String displayName;
    private String criteriaName;
    @Setter
    private SortOrder sortOrder;
    private ScoreboardStorage storage;
    private static int scoreboardId = 0;

    public Scoreboard(String objectiveName, String displayName, String criteriaName, SortOrder sortOrder, ScoreboardStorage storage) {
        this(new HashMap(),objectiveName, displayName, criteriaName,sortOrder,storage);
    }

    public Scoreboard(Map<Scorer,ScoreboardLine> lines,String objectiveName,String displayName,String criteriaName,SortOrder sortOrder,ScoreboardStorage storage){
        this.lines = lines;
        this.lines.forEach((scorer,line)->line.scoreboardId = ++scoreboardId);//we should reset the scoreboard id of each lines to prevent repetition
        this.objectiveName = objectiveName;
        this.displayName = displayName;
        this.criteriaName = criteriaName;
        this.storage = storage;
        this.sortOrder = sortOrder;
    }

    public void addLine(Scorer scorer,int score){
        lines.put(scorer,new ScoreboardLine(scorer,score,++scoreboardId));
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.SET;
        if (this.lines.get(scorer).toScoreInfo() != null)
            packet.infos.add(this.lines.get(scorer).toScoreInfo());
        Server.getInstance().getOnlinePlayers().values().forEach(player->player.dataPacket(packet));
        if (Server.getInstance().getScoreboardManager().getDisplay().get(DisplaySlot.BELOW_NAME) != null && Server.getInstance().getScoreboardManager().getDisplay().get(DisplaySlot.BELOW_NAME).equals(this.objectiveName))
            Server.getInstance().getScoreboardManager().updateScoreTag();
        storage.saveScoreboard(this);
    }

    public void removeLine(Scorer scorer){
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.REMOVE;
        if (this.lines.get(scorer).toScoreInfo() != null)
            packet.infos.add(this.lines.get(scorer).toScoreInfo());
        Server.getInstance().getOnlinePlayers().values().forEach(player->player.dataPacket(packet));
        if (Server.getInstance().getScoreboardManager().getDisplay().get(DisplaySlot.BELOW_NAME).equals(this.objectiveName))
            Server.getInstance().getScoreboardManager().updateScoreTag();
        lines.remove(scorer);


        storage.saveScoreboard(this);
    }

    public ScoreboardLine getLine(Scorer scorer){
        return lines.get(scorer);
    }

    public boolean containLine(Scorer scorer){
        return lines.containsKey(scorer);
    }

    @Getter
    public class ScoreboardLine {

        private Scorer scorer;
        private int score;
        private int scoreboardId;

        private ScoreboardLine(Scorer scorer, int score, int scoreboardId) {
            this.scorer = scorer;
            this.score = score;
            this.scoreboardId = scoreboardId;
        }

        public void setScore(int score){
            this.score = score;
            SetScorePacket packet = new SetScorePacket();
            packet.action = SetScorePacket.Action.SET;
            if (this.toScoreInfo() != null)
                packet.infos.add(this.toScoreInfo());
            Server.getInstance().getOnlinePlayers().values().forEach(player->player.dataPacket(packet));
            if (Server.getInstance().getScoreboardManager().getDisplay().get(DisplaySlot.BELOW_NAME) != null && Server.getInstance().getScoreboardManager().getDisplay().get(DisplaySlot.BELOW_NAME).equals(Scoreboard.this.objectiveName))
                Server.getInstance().getScoreboardManager().updateScoreTag();
            storage.saveScoreboard(Scoreboard.this);
        }

        public void addScore(int score){
            setScore(this.score + score);
        }

        public void removeScore(int score){
            setScore(this.score - score);
        }

        @Nullable
        public SetScorePacket.ScoreInfo toScoreInfo(){
            switch(this.scorer.getScorerType()) {
                case PLAYER -> {
                    UUID uuid = ((PlayerScorer) this.scorer).getUuid();
                    return !Server.getInstance().getPlayer(uuid).isEmpty() ? new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score, ScorerType.PLAYER,Server.getInstance().getPlayer(uuid).get().getId()) : null;
                }
                case ENTITY -> {
                    UUID uuid = ((EntityScorer) this.scorer).getEntityUuid();
                    long id = uuid.getMostSignificantBits();
                    return new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score, ScorerType.ENTITY, id);
                }
                case FAKE -> {
                    return new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score,((FakeScorer) this.scorer).getFakeName());
                }
            };
            return null;
        }
    }
}
