package cn.nukkit.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.ScoreboardSendable;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
public class ScoreboardManager {

    private Map<String, Scoreboard> scoreboards;
    private Map<DisplaySlot,String> display;
    private ScoreboardStorage storage;

    public ScoreboardManager(ScoreboardStorage storage) {
        this.storage = storage;
    }

    public void init(){
        display = storage.readDisplay();
        scoreboards = storage.readScoreboard();
    }

    public void addScoreboard(Scoreboard scoreboard){
        scoreboards.put(scoreboard.getObjectiveName(),scoreboard);
        if (display.get(DisplaySlot.BELOW_NAME) != null && display.get(DisplaySlot.BELOW_NAME).equals(scoreboard.getObjectiveName()))
            this.updateScoreTag();
        storage.saveScoreboard(scoreboard);
    }

    public void removeScoreboard(String name){
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = name;
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            player.dataPacket(pk);
        }
        scoreboards.remove(name);
        if (display.get(DisplaySlot.BELOW_NAME) != null && display.get(DisplaySlot.BELOW_NAME).equals(name)) {
            display.put(DisplaySlot.BELOW_NAME, null);
            this.updateScoreTag();
        }
        storage.removeScoreboard(name);
    }

    public boolean containScoreboard(String name){
        return scoreboards.containsKey(name);
    }

    public void setDisplaySlot(DisplaySlot slot,String name){
        display.put(slot,name);
        storage.saveDisplay(display);
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.sendScoreboard(this.scoreboards.get(name),slot);
        });
        if (slot == DisplaySlot.BELOW_NAME)
            this.updateScoreTag();
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
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.clearScoreboardSlot(slot);
        });
        display.put(slot,null);
        storage.saveDisplay(display);
        if (slot == DisplaySlot.BELOW_NAME)
            this.updateScoreTag();
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

    public void updateScoreTag(){
        if (scoreboards == null)
            return;//first time init
        if (display.get(DisplaySlot.BELOW_NAME) != null && this.containScoreboard(display.get(DisplaySlot.BELOW_NAME))) {
            for (Scorer scorer : scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().keySet()) {
                if (scorer instanceof PlayerScorer playerScorer) {
                    Optional<Player> player = Server.getInstance().getPlayer(playerScorer.getUuid());
                    if (!player.isEmpty()) {
                        player.get().setScoreTag(scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().get(scorer).getScore() + " " + scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getDisplayName());
                    }
                }
            }
        }else{
            Server.getInstance().getOnlinePlayers().values().forEach(player -> {
                player.setScoreTag("");
            });
        }
    }
}
