package cn.nukkit.scoreboard.scorer;

import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.Scorer;
import lombok.Getter;

@Getter
public class FakeScorer implements Scorer {

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
}
