package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.IScorer;

import java.util.Map;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.19.21-r5")
public interface IScoreboard {

    String getObjectiveName();
    String getDisplayName();
    String getCriteriaName();

    SortOrder getSortOrder();
    void setSortOrder(SortOrder order);

    Set<IScoreboardViewer> getAllViewers();
    Set<IScoreboardViewer> getViewers(DisplaySlot slot);
    boolean removeViewer(IScoreboardViewer viewer, DisplaySlot slot);
    boolean addViewer(IScoreboardViewer viewer, DisplaySlot slot);
    boolean containViewer(IScoreboardViewer viewer, DisplaySlot slot);

    Map<IScorer, IScoreboardLine> getLines();
    IScoreboardLine getLine(IScorer scorer);
    boolean addLine(IScoreboardLine line);
    boolean removeLine(IScorer scorer);
    boolean containLine(IScorer scorer);
    void updateLine(IScoreboardLine update);
}
