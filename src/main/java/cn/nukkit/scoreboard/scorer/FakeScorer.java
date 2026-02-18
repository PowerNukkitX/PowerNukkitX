package cn.nukkit.scoreboard.scorer;

import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;
import lombok.Getter;


@Getter
public class FakeScorer implements IScorer {

    private String fakeName;

    public FakeScorer(String fakeName) {
        this.fakeName = fakeName;
    }

    @Override
    public ScorerType getScorerType() {
        return ScorerType.FAKE;
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
    public ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        return new ScoreInfo(line.getLineId(), scoreboard.getObjectiveName(), line.getScore(), getFakeName());
    }
}
