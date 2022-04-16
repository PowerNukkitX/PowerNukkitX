package cn.nukkit.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.AbstractScoreboardManager;
import cn.nukkit.scoreboard.interfaces.ScoreboardSendable;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
import cn.nukkit.scoreboard.scorer.PlayerScorer;

public class ScoreboardManager extends AbstractScoreboardManager {

    public ScoreboardManager(ScoreboardStorage storage) {
        super(storage);
    }

    @Override
    public void addScoreboard(Scoreboard scoreboard){
        scoreboards.put(scoreboard.getObjectiveName(),scoreboard);
        storage.saveScoreboard(scoreboard);
    }

    @Override
    public void removeScoreboard(String name){
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = name;
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            player.dataPacket(pk);
        }
        scoreboards.remove(name);
        if (isScoreboardOnDisplay(name)){
            removeDisplay(getDisplaySlot(name));
        }
        storage.removeScoreboard(name);
    }

    @Override
    public void setDisplay(DisplaySlot slot, String name){
        display.put(slot,name);
        storage.saveDisplay(display);
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.sendScoreboard(this.scoreboards.get(name),slot);
        });
        if (slot == DisplaySlot.BELOW_NAME)
            this.updateAllScoreTag();
    }

    @Override
    public void removeDisplay(DisplaySlot slot){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            player.clearScoreboardSlot(slot);
        });
        display.put(slot,null);
        storage.saveDisplay(display);
        if (slot == DisplaySlot.BELOW_NAME)
            this.updateAllScoreTag();
    }

    @Override
    public void onPlayerJoin(Player player){
        sendAllDisplayTo(player);
        updateScoreTag(player);
    }

    @Override
    public void onPlayerQuit(Player player){
        //in order to clear the old score information so that we can avoid "offline player"
        hideOfflinePlayerScore(player);
    }

    private void sendDisplayToAll(DisplaySlot slot){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            sendDisplayTo(slot,player);
        });
    }

    private void sendDisplayTo(DisplaySlot slot, ScoreboardSendable sendable){
        if (display.get(slot) != null)
            sendable.sendScoreboard(this.scoreboards.get(this.display.get(slot)),slot);
    }

    private void sendAllDisplayToAll(){
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            sendAllDisplayTo(player);
        });
    }

    private void sendAllDisplayTo(ScoreboardSendable sendable){
        for(DisplaySlot slot : DisplaySlot.values()){
            sendDisplayTo(slot,sendable);
        }
    }

    private void hideOfflinePlayerScore(Player player) {
        PlayerScorer scorer = new PlayerScorer(player);

        SetScorePacket pk = new SetScorePacket();
        pk.action = SetScorePacket.Action.REMOVE;
        for (Scoreboard scoreboard : scoreboards.values()) {
            if (scoreboard.getLines().containsKey(scorer)) {
                pk.infos.add(new SetScorePacket.ScoreInfo(scoreboard.getLines().get(scorer).getScoreboardId(), scoreboard.getObjectiveName(), 0, ScorerType.PLAYER, -1));
            }
        }

        for (Player onlinePlayer : Server.getInstance().getOnlinePlayers().values()) {
            onlinePlayer.dataPacket(pk);
        }
    }
}
