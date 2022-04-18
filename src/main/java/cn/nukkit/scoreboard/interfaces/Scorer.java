package cn.nukkit.scoreboard.interfaces;

import cn.nukkit.scoreboard.data.ScorerType;

/**
 * the class which can be used as a key in a scoreboard
 */
public interface Scorer {
    ScorerType getScorerType();

    String getName();
}
