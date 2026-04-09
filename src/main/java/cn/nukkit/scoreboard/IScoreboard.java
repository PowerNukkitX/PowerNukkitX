package cn.nukkit.scoreboard;

import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.IScorer;
import org.cloudburstmc.protocol.bedrock.data.ObjectiveSortOrder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IScoreboard {
    String getObjectiveName();

    String getDisplayName();

    String getCriteriaName();

    ObjectiveSortOrder getSortOrder();

    void setSortOrder(ObjectiveSortOrder order);

    Set<IScoreboardViewer> getAllViewers();

    Set<IScoreboardViewer> getViewers(DisplaySlot slot);

    boolean removeViewer(IScoreboardViewer viewer, DisplaySlot slot);

    boolean addViewer(IScoreboardViewer viewer, DisplaySlot slot);

    boolean containViewer(IScoreboardViewer viewer, DisplaySlot slot);

    Map<IScorer, IScoreboardLine> getLines();

    @Nullable
    IScoreboardLine getLine(IScorer scorer);

    boolean addLine(IScoreboardLine line);

    boolean addLine(IScorer scorer, int score);

    boolean addLine(String text, int score);

    boolean removeLine(IScorer scorer);

    boolean removeAllLine(boolean send);

    boolean containLine(IScorer scorer);

    void updateScore(IScoreboardLine update);

    void resend();

    void setLines(List<String> lines);

    void setLines(Collection<IScoreboardLine> lines);

    boolean shouldCallEvent();
}