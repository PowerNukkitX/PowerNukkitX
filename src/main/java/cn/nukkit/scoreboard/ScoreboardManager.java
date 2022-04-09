package cn.nukkit.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.ScoreboardSendable;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import lombok.Getter;

import java.util.Map;

@Getter
public class ScoreboardManager {

    private Map<String, Scoreboard> scoreboards;
    private Map<DisplaySlot,String> display;
    private ScoreboardStorage storage;

    public ScoreboardManager(ScoreboardStorage storage) {
        this.storage = storage;
        scoreboards = storage.readScoreboard();
        display = storage.readDisplay();
    }

    public void addScoreboard(Scoreboard scoreboard){
        scoreboards.put(scoreboard.getObjectiveName(),scoreboard);
        storage.saveScoreboard(scoreboard);
    }

    public void removeScoreboard(String name){
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = name;
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            player.dataPacket(pk);
        }
        scoreboards.remove(name);
        storage.removeScoreboard(name);
    }

    public void setDisplaySlot(DisplaySlot slot,String name){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.sendScoreboard(this.scoreboards.get(name),slot);
        });
    }

    public void sendDisplaySlot(DisplaySlot slot){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            sendDisplaySlot(slot,player);
        });
    }

    public void sendDisplaySlot(DisplaySlot slot,ScoreboardSendable sendable){
        if (display.get(slot) != null)
            sendable.sendScoreboard(this.scoreboards.get(this.display.get(slot)),slot);
    }

    public void sendAllDisplaySlot(){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            sendAllDisplaySlot(player);
        });
    }

    public void sendAllDisplaySlot(ScoreboardSendable sendable){
        for(DisplaySlot slot : DisplaySlot.values()){
            sendDisplaySlot(slot,sendable);
        }
    }

    public void clearDisplaySlot(DisplaySlot slot){
        display.remove(slot);
        storage.saveDisplay(display);
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.clearScoreboardSlot(slot);
        });
    }

    public void removeOfflinePlayerScore(Player player){
        PlayerScorer scorer = new PlayerScorer(player);

        SetScorePacket pk = new SetScorePacket();
        pk.action = SetScorePacket.Action.REMOVE;
        for (Scoreboard scoreboard : scoreboards.values()) {
            if (scoreboard.getLines().containsKey(scorer)) {
                pk.infos.add(new SetScorePacket.ScoreInfo(scoreboard.getLines().get(scorer).getScoreboardId(),scoreboard.getObjectiveName(),0, ScorerType.PLAYER,-1));
            }
        }

        for (Player onlinePlayer : Server.getInstance().getOnlinePlayers().values()) {
            onlinePlayer.dataPacket(pk);
        }
    }
}
