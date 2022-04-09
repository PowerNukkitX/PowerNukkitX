package cn.nukkit.scoreboard;

import cn.nukkit.Server;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.interfaces.ScoreboardSendable;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
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
}
