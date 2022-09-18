package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.IScorer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@PowerNukkitXOnly
@Since("1.19.21-r5")
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

    public Scoreboard(String objectiveName, String displayName) {
        this(objectiveName, displayName, "dummy");
    }

    public Scoreboard(String objectiveName, String displayName, String criteriaName) {
        this(objectiveName, displayName, criteriaName, SortOrder.ASCENDING);
    }

    public Scoreboard(String objectiveName, String displayName, String criteriaName, SortOrder sortOrder) {
        this.objectiveName = objectiveName;
        this.displayName = displayName;
        this.criteriaName = criteriaName;
        this.sortOrder = sortOrder;
    }

    @Override
    public Set<IScoreboardViewer> getAllViewers() {
        var all = new HashSet<IScoreboardViewer>();
        this.viewers.values().forEach(all::addAll);
        return all;
    }

    @Override
    public Set<IScoreboardViewer> getViewers(DisplaySlot slot) {
        return this.viewers.get(slot);
    }

    @Override
    public boolean removeViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        boolean removed = this.viewers.get(slot).remove(viewer);
        if (removed) viewer.hide(slot);
        return removed;
    }

    @Override
    public boolean addViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        boolean added =  this.viewers.get(slot).add(viewer);
        if (added) viewer.display(this, slot);
        return added;
    }

    @Override
    public boolean containViewer(IScoreboardViewer viewer, DisplaySlot slot) {
        return this.viewers.get(slot).contains(viewer);
    }

    @Override
    public IScoreboardLine getLine(IScorer scorer) {
        return this.lines.get(scorer);
    }

    @Override
    public boolean addLine(IScoreboardLine line) {
        this.lines.put(line.getScorer(), line);
        updateLine(line);
        return true;
    }

    @Override
    public boolean removeLine(IScorer scorer) {
        var removed =  this.lines.remove(scorer);
        if (removed != null) {
            getAllViewers().forEach(viewer -> viewer.removeLine(removed));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containLine(IScorer scorer) {
        return this.lines.containsKey(scorer);
    }

    @Override
    public void updateLine(IScoreboardLine update) {
        getAllViewers().forEach(viewer -> viewer.updateLine(update));
    }
}
