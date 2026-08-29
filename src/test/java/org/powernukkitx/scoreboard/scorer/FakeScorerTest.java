package org.powernukkitx.scoreboard.scorer;

import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FakeScorerTest {

    @Test
    void nameAccessorsReturnConstructorArg() {
        FakeScorer scorer = new FakeScorer("Bot");
        Assertions.assertEquals("Bot", scorer.getFakeName());
        Assertions.assertEquals("Bot", scorer.getName());
    }

    @Test
    void scorerTypeIsChangeFakePlayer() {
        Assertions.assertEquals(ScorePacketEntryAction.CHANGE_FAKE_PLAYER, new FakeScorer("x").getScorerType());
    }

    @Test
    void equalsAndHashCodeBasedOnName() {
        FakeScorer a = new FakeScorer("same");
        FakeScorer b = new FakeScorer("same");
        FakeScorer c = new FakeScorer("other");

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, c);
        Assertions.assertNotEquals(a, "same");
    }
}
