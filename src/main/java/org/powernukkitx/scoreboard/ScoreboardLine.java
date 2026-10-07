package org.powernukkitx.scoreboard;

import org.powernukkitx.Server;
import org.powernukkitx.event.scoreboard.ScoreboardLineChangeEvent;
import org.powernukkitx.scoreboard.scorer.IScorer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
public class ScoreboardLine implements IScoreboardLine{

    protected static long staticLineId = 0;
    private static final Map<IScorer, Long> identityIds = new HashMap<>();

    protected final IScoreboard scoreboard;
    protected final IScorer scorer;
    protected final long lineId;
    protected int score;

    public ScoreboardLine(IScoreboard scoreboard, IScorer scorer) {
        this(scoreboard, scorer, 0);
    }

    public ScoreboardLine(IScoreboard scoreboard, IScorer scorer, int score) {
        this(scoreboard, scorer, score, nextLineId(scorer));
    }

    public ScoreboardLine(IScoreboard scoreboard, IScorer scorer, int score, long lineId) {
        if (lineId <= 0) {
            throw new IllegalArgumentException("ScoreboardId must be greater than zero");
        }

        registerIdentity(scorer, lineId);
        this.scoreboard = scoreboard;
        this.scorer = scorer;
        this.score = score;
        this.lineId = lineId;
    }

    private static synchronized long nextLineId(IScorer scorer) {
        Long existing = identityIds.get(scorer);
        if (existing != null) {
            return existing;
        }

        long lineId = ++staticLineId;
        identityIds.put(scorer, lineId);
        return lineId;
    }

    private static synchronized void registerIdentity(IScorer scorer, long lineId) {
        Long existing = identityIds.putIfAbsent(scorer, lineId);
        if (existing != null && existing != lineId) {
            throw new IllegalStateException("Scoreboard identity has conflicting IDs " + existing + " and " + lineId);
        }

        if (lineId > staticLineId) {
            staticLineId = lineId;
        }
    }

    /**
     * Returns the highest scoreboard identity ID assigned so far.
     *
     * @return last assigned identity ID
     */
    public static synchronized long getLastUniqueId() {
        return staticLineId;
    }

    /**
     * Advances the scoreboard identity ID counter when necessary.
     *
     * @param uniqueId persisted identity ID
     */
    public static synchronized void setLastUniqueId(long uniqueId) {
        if (uniqueId > staticLineId) {
            staticLineId = uniqueId;
        }
    }

    /**
     * Resets the runtime scoreboard identity state.
     *
     * @param lastUniqueId last persisted identity ID
     */
    public static synchronized void resetIdentityState(long lastUniqueId) {
        identityIds.clear();
        staticLineId = Math.max(lastUniqueId, 0);
    }

    @Override
    public boolean setScore(int score) {
        if (scoreboard.shouldCallEvent()) {
            var event = new ScoreboardLineChangeEvent(scoreboard, this, score, this.score, ScoreboardLineChangeEvent.ActionType.SCORE_CHANGE);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            score = event.getNewValue();
        }
        this.score = score;
        updateScore();
        return true;
    }
}
