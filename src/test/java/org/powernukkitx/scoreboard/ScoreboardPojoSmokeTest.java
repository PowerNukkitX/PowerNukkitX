package org.powernukkitx.scoreboard;

import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.scoreboard.scorer.FakeScorer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.cloudburstmc.protocol.bedrock.data.ObjectiveSortOrder;

/**
 * Pure data coverage for the scoreboard objective/line POJOs - constructors, lombok
 * getters and the map based line lookups that do not fire server events. Complements
 * the existing DisplaySlot / FakeScorer tests without duplicating them.
 */
public class ScoreboardPojoSmokeTest {

    @Test
    void scoreboardHoldsObjectiveData() {
        Scoreboard board = new Scoreboard("obj", "Display", "dummy", ObjectiveSortOrder.DESCENDING);

        Assertions.assertEquals("obj", board.getObjectiveName());
        Assertions.assertEquals("Display", board.getDisplayName());
        Assertions.assertEquals("dummy", board.getCriteriaName());
        Assertions.assertEquals(ObjectiveSortOrder.DESCENDING, board.getSortOrder());
        Assertions.assertNotNull(board.getLines());
        Assertions.assertNotNull(board.getViewers());
        Assertions.assertNotNull(board.getAllViewers());
        Assertions.assertNotNull(board.getViewers(DisplaySlot.SIDEBAR));

        board.setSortOrder(ObjectiveSortOrder.ASCENDING);
        Assertions.assertEquals(ObjectiveSortOrder.ASCENDING, board.getSortOrder());
    }

    @Test
    void shortConstructorsDefault() {
        Scoreboard board = new Scoreboard("o", "d");
        Assertions.assertEquals("dummy", board.getCriteriaName());
        Assertions.assertEquals(ObjectiveSortOrder.ASCENDING, board.getSortOrder());
    }

    @Test
    void scoreboardLineExposesScorerAndScore() {
        Scoreboard board = new Scoreboard("o", "d");
        FakeScorer scorer = new FakeScorer("fake");
        ScoreboardLine line = new ScoreboardLine(board, scorer, 7);

        Assertions.assertSame(board, line.getScoreboard());
        Assertions.assertSame(scorer, line.getScorer());
        Assertions.assertEquals(7, line.getScore());
        Assertions.assertTrue(line.getLineId() > 0);

        ScoreboardLine other = new ScoreboardLine(board, scorer);
        Assertions.assertEquals(0, other.getScore());
        Assertions.assertNotEquals(line.getLineId(), other.getLineId());

        Assertions.assertFalse(board.containLine(scorer));
        Assertions.assertNull(board.getLine(scorer));
    }
}
