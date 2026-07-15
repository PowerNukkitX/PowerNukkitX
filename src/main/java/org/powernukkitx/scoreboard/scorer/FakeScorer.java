package org.powernukkitx.scoreboard.scorer;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ChangeFakePlayerScore;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;


@Getter
public class FakeScorer implements IScorer {

    private String fakeName;

    public FakeScorer(String fakeName) {
        this.fakeName = fakeName;
    }

    @Override
    public ScorePacketEntryAction getScorerType() {
        return ScorePacketEntryAction.CHANGE_FAKE_PLAYER;
    }

    @Override
    public int hashCode() {
        return fakeName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FakeScorer fakeScorer) {
            return fakeScorer.fakeName.equals(fakeName);
        }
        return false;
    }

    @Override
    public String getName() {
        return fakeName;
    }

    @Override
    public ChangeFakePlayerScore toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        final ChangeFakePlayerScore score = new ChangeFakePlayerScore();
        score.setScoreboardId(line.getLineId());
        score.setObjectiveName(scoreboard.getObjectiveName());
        score.setScoreValue(line.getScore());
        score.setFakePlayerName(this.getFakeName());
        return score;
    }
}
