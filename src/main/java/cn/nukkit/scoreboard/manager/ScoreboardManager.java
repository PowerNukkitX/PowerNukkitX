package cn.nukkit.scoreboard.manager;


import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.scoreboard.storage.IScoreboardStorage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.19.21-r5")
@Getter
public class ScoreboardManager implements IScoreboardManager{

    protected Map<String, IScoreboard> scoreboards = new HashMap<>();
    protected Map<DisplaySlot, IScoreboard> display = new HashMap<>();
    protected Set<IScoreboardViewer> viewers = new HashSet<>();
    protected IScoreboardStorage storage;

    public ScoreboardManager(IScoreboardStorage storage) {
        this.storage = storage;
        read();
    }

    @Override
    public boolean addScoreboard(IScoreboard scoreboard) {
        scoreboards.put(scoreboard.getObjectiveName(), scoreboard);
        return true;
    }

    @Override
    public boolean removeScoreboard(IScoreboard scoreboard) {
        return removeScoreboard(scoreboard.getObjectiveName());
    }

    @Override
    public boolean removeScoreboard(String objectiveName) {
       var removed =  scoreboards.remove(objectiveName);
       if (removed == null) return false;
       display.forEach((slot, scoreboard) -> {
           if (scoreboard.getObjectiveName().equals(objectiveName)) {
                setDisplay(slot, null);
           }
       });
       return true;
    }

    @Override
    public IScoreboard getScoreboard(String objectiveName) {
        return scoreboards.get(objectiveName);
    }

    @Override
    public boolean containScoreboard(IScoreboard scoreboard) {
        return scoreboards.containsKey(scoreboard.getObjectiveName());
    }

    @Override
    public boolean containScoreboard(String name) {
        return scoreboards.containsKey(name);
    }

    @Override
    public IScoreboard getDisplaySlot(DisplaySlot slot) {
        return display.get(slot);
    }

    @Override
    public void setDisplay(DisplaySlot slot, @Nullable IScoreboard scoreboard) {
        var old = display.put(slot, scoreboard);
        if (old != null) this.viewers.forEach(viewer -> old.removeViewer(viewer, slot));
        if (scoreboard != null) this.viewers.forEach(viewer -> scoreboard.addViewer(viewer, slot));
    }

    @Override
    public boolean addViewer(IScoreboardViewer viewer) {
        var added =  this.viewers.add(viewer);
        if (added) this.display.forEach((slot, scoreboard) -> {
            if (scoreboard != null) scoreboard.addViewer(viewer, slot);
        });
        return added;
    }

    @Override
    public boolean removeViewer(IScoreboardViewer viewer) {
        var removed = viewers.remove(viewer);
        if (removed) this.display.forEach((slot, scoreboard) -> {
            if (scoreboard != null) scoreboard.removeViewer(viewer, slot);
        });
        return removed;
    }

    /**
     * 你可能会对这里的代码感到疑惑
     * 这里其实是为了规避玩家下线导致的“玩家离线”计分项
     * 我们在玩家下线时，删除其他在线玩家显示槽位中的“玩家离线”
     * 并在其重新连接上服务器时加回去
     */

    @Override
    public void onPlayerJoin(Player player) {
        addViewer(player);
        var scorer = new PlayerScorer(player);
        this.scoreboards.values().forEach(scoreboard -> {
            if (scoreboard.containLine(scorer)) {
                this.viewers.forEach(viewer -> viewer.updateLine(scoreboard.getLine(scorer)));
            }
        });
    }

    @Override
    public void beforePlayerQuit(Player player) {
        var scorer = new PlayerScorer(player);
        this.scoreboards.values().forEach(scoreboard -> {
            if (scoreboard.containLine(scorer)) {
                this.viewers.forEach(viewer -> viewer.removeLine(scoreboard.getLine(scorer)));
            }
        });
        removeViewer(player);
    }

    @Override
    public void save() {
        storage.removeAllScoreboard();
        storage.saveScoreboard(scoreboards.values());
        storage.saveDisplay(display);
    }

    @Override
    public void read() {
        this.scoreboards.values().forEach(this::removeScoreboard);
        this.display.forEach((slot, scoreboard) -> setDisplay(slot, null));

        scoreboards = storage.readScoreboard();
        storage.readDisplay().forEach((slot, objectiveName) -> {
            var scoreboard = getScoreboard(objectiveName);
            if (scoreboard != null) {
                this.setDisplay(slot, scoreboard);
            }
        });
    }
}
