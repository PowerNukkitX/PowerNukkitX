package cn.nukkit.scoreboard;

import cn.nukkit.Server;
import cn.nukkit.event.command.ScoreboardEvent;
import cn.nukkit.event.command.ScoreboardScoreChangeEvent;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.interfaces.AbstractScoreboardManager;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Scoreboard {

    private Map<Scorer, ScoreboardLine> lines;
    private String objectiveName;
    private String displayName;
    private String criteriaName;
    @Setter
    private SortOrder sortOrder;
    private AbstractScoreboardManager manager;
    private static int scoreboardId = 0;

    public Scoreboard(String objectiveName, String displayName, String criteriaName, SortOrder sortOrder, AbstractScoreboardManager manager) {
        this(new HashMap(), objectiveName, displayName, criteriaName, sortOrder, manager);
    }

    public Scoreboard(Map<Scorer, ScoreboardLine> lines, String objectiveName, String displayName, String criteriaName, SortOrder sortOrder, AbstractScoreboardManager manager) {
        this.lines = lines;
        this.lines.forEach((scorer, line) -> line.scoreboardId = ++scoreboardId);//we should reset the scoreboard id of each lines to prevent repetition
        this.objectiveName = objectiveName;
        this.displayName = displayName;
        this.criteriaName = criteriaName;
        this.manager = manager;
        this.sortOrder = sortOrder;
    }

    public boolean addLine(Scorer scorer, int score) {
        ScoreboardScoreChangeEvent event = new ScoreboardScoreChangeEvent(this,scorer,score,0, ScoreboardScoreChangeEvent.ActionType.ADD);
        //Because ScoreboardManager is loaded before PluginManager, so we need to check if it is null
        if (Server.getInstance().getPluginManager() != null) {
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return false;
        }
        lines.put(scorer, new ScoreboardLine(scorer, event.getNewValue(), ++scoreboardId));
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.SET;
        if (this.lines.get(scorer).toScoreInfo() != null)
            packet.infos.add(this.lines.get(scorer).toScoreInfo());
        Server.getInstance().getOnlinePlayers().values().forEach(player -> player.dataPacket(packet));
        if (scorer instanceof PlayerScorer playerScorer && playerScorer.isOnline())
            manager.updateScoreTag(playerScorer.getPlayer());
        manager.getStorage().saveScoreboard(this);
        return true;
    }

    public boolean removeLine(Scorer scorer) {
        if (!this.lines.containsKey(scorer))
            return false;
        ScoreboardScoreChangeEvent event = new ScoreboardScoreChangeEvent(this,scorer,0,0, ScoreboardScoreChangeEvent.ActionType.REMOVE);
        //Because ScoreboardManager is loaded before PluginManager, so we need to check if it is null
        if (Server.getInstance().getPluginManager() != null) {
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return false;
        }
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.REMOVE;
        if (this.lines.get(scorer).toScoreInfo() != null)
            packet.infos.add(this.lines.get(scorer).toScoreInfo());
        Server.getInstance().getOnlinePlayers().values().forEach(player -> player.dataPacket(packet));
        if (scorer instanceof PlayerScorer playerScorer && playerScorer.isOnline())
            manager.updateScoreTag(playerScorer.getPlayer());
        lines.remove(scorer);
        manager.getStorage().saveScoreboard(this);
        return true;
    }

    public ScoreboardLine getLine(Scorer scorer) {
        return lines.get(scorer);
    }

    public boolean containLine(Scorer scorer) {
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

        public boolean setScore(int score) {
            ScoreboardScoreChangeEvent event = callEvent(score,this.score);
            if (event.isCancelled())
                return false;
            this.score = event.getNewValue();
            SetScorePacket packet = new SetScorePacket();
            packet.action = SetScorePacket.Action.SET;
            if (this.toScoreInfo() != null)
                packet.infos.add(this.toScoreInfo());
            Server.getInstance().getOnlinePlayers().values().forEach(player -> player.dataPacket(packet));
            if (this.scorer instanceof PlayerScorer playerScorer && playerScorer.isOnline())
                manager.updateScoreTag(playerScorer.getPlayer());
            manager.getStorage().saveScoreboard(Scoreboard.this);
            return true;
        }

        public boolean addScore(int score) {
            return setScore(this.score + score);
        }

        public boolean removeScore(int score) {
            return setScore(this.score - score);
        }

        public SetScorePacket.ScoreInfo toScoreInfo() {
            switch (this.scorer.getScorerType()) {
                case PLAYER -> {
                    UUID uuid = ((PlayerScorer) this.scorer).getUuid();
                    return !Server.getInstance().getPlayer(uuid).isEmpty() ? new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score, ScorerType.PLAYER, Server.getInstance().getPlayer(uuid).get().getId()) : null;
                }
                case ENTITY -> {
                    UUID uuid = ((EntityScorer) this.scorer).getEntityUuid();
                    long id = uuid.getMostSignificantBits();
                    return new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score, ScorerType.ENTITY, id);
                }
                case FAKE -> {
                    return new SetScorePacket.ScoreInfo(this.scoreboardId, Scoreboard.this.getObjectiveName(), this.score, ((FakeScorer) this.scorer).getFakeName());
                }
            }
            return null;
        }

        public SetScorePacket.ScoreInfo toRemovedScoreInfo(){
            return new SetScorePacket.ScoreInfo(this.scoreboardId,Scoreboard.this.getObjectiveName(), this.score,ScorerType.INVALID,-1);
        }

        private ScoreboardScoreChangeEvent callEvent(int newValue,int oldValue){
            ScoreboardScoreChangeEvent event = new ScoreboardScoreChangeEvent(Scoreboard.this,this.scorer,newValue,oldValue);
            //Because ScoreboardManager is loaded before PluginManager, so we need to check if it is null
            if (Server.getInstance().getPluginManager() != null)
                Server.getInstance().getPluginManager().callEvent(event);
            return event;
        }
    }
}
