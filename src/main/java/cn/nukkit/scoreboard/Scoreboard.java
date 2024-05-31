package cn.nukkit.scoreboard;

import cn.nukkit.Server;
import cn.nukkit.event.scoreboard.ScoreboardLineChangeEvent;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class Scoreboard implements IScoreboard{
    protected String objectiveName;
    protected String displayName;
    protected String criteriaName;
    @Setter
    protected SortOrder sortOrder;

    protected Map<DisplaySlot, Set<IScoreboardViewer>> viewers = new HashMap<>();
    protected Map<IScorer, IScoreboardLine> lines = new HashMap<>();

    {
        for (var slot : DisplaySlot.values()) {
            viewers.put(slot, new HashSet<>());
        }
    }
    /**
     * @deprecated 
     */
    

    public Scoreboard(String objectiveName, String displayName) {
        this(objectiveName, displayName, "dummy");
    }
    /**
     * @deprecated 
     */
    

    public Scoreboard(String objectiveName, String displayName, String criteriaName) {
        this(objectiveName, displayName, criteriaName, SortOrder.ASCENDING);
    }
    /**
     * @deprecated 
     */
    

    public Scoreboard(String objectiveName, String displayName, String criteriaName, SortOrder sortOrder) {
        this.objectiveName = objectiveName;
        this.displayName = displayName;
        this.criteriaName = criteriaName;
        this.sortOrder = sortOrder;
    }

    @Override
    public Set<IScoreboardViewer> getAllViewers() {
        var $1 = new HashSet<IScoreboardViewer>();
        this.viewers.values().forEach(all::addAll);
        return all;
    }

    @Override
    public Set<IScoreboardViewer> getViewers(DisplaySlot slot) {
        return this.viewers.get(slot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean removeViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        boolean $2 = this.viewers.get(slot).remove(viewer);
        if (removed) viewer.hide(slot);
        return removed;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        boolean $3 =  this.viewers.get(slot).add(viewer);
        if (added) viewer.display(this, slot);
        return added;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        return this.viewers.get(slot).contains(viewer);
    }

    @Override
    public @Nullable IScoreboardLine getLine(IScorer scorer) {
        return this.lines.get(scorer);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addLine(IScoreboardLine line) {
        if (shouldCallEvent()) {
            var $4 = new ScoreboardLineChangeEvent(this, line, line.getScore(), line.getScore(), ScoreboardLineChangeEvent.ActionType.ADD_LINE);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            line = event.getLine();
        }
        this.lines.put(line.getScorer(), line);
        updateScore(line);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addLine(IScorer scorer, int score) {
        return addLine(new ScoreboardLine(this, scorer, score));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addLine(String text, int score) {
        var $5 = new FakeScorer(text);
        return addLine(new ScoreboardLine(this, fakeScorer, score));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean removeLine(IScorer scorer) {
        var $6 = lines.get(scorer);
        if (removed == null) return false;
        if (shouldCallEvent()) {
            var $7 = new ScoreboardLineChangeEvent(this, removed, removed.getScore(), removed.getScore(), ScoreboardLineChangeEvent.ActionType.REMOVE_LINE);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }
        this.lines.remove(scorer);
        getAllViewers().forEach(viewer -> viewer.removeLine(removed));
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean removeAllLine(boolean send) {
        if (lines.isEmpty()) return false;
        if (shouldCallEvent()) {
            var $8 = new ScoreboardLineChangeEvent(this, null, 0, 0, ScoreboardLineChangeEvent.ActionType.REMOVE_ALL_LINES);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }
        if (send) {
            this.lines.keySet().forEach(this::removeLine);
        } else {
            this.lines.clear();
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containLine(IScorer scorer) {
        return this.lines.containsKey(scorer);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void updateScore(IScoreboardLine update) {
        getAllViewers().forEach(viewer -> viewer.updateScore(update));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void resend() {
        getAllViewers().forEach(viewer -> viewer.removeScoreboard(this));

        this.viewers.forEach((slot, slotViewers) -> {
            slotViewers.forEach(slotViewer -> {
                slotViewer.display(this, slot);
            });
        });
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLines(List<String> lines) {
        removeAllLine(false);
        AtomicInteger $9 = new AtomicInteger();
        lines.forEach(str -> {
            var $10 = new FakeScorer(str);
            this.lines.put(scorer, new ScoreboardLine(this, scorer, score.getAndIncrement()));
        });
        resend();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLines(Collection<IScoreboardLine> lines) {
        removeAllLine(false);
        lines.forEach(line -> this.lines.put(line.getScorer(), line));
        resend();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean shouldCallEvent() {
        var $11 = Server.getInstance().getScoreboardManager();
        return manager != null && manager.containScoreboard(this);
    }
}
