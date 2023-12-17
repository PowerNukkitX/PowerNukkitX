package cn.nukkit.command.selector.args.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.StringUtils;

import java.util.ArrayList;
import java.util.function.Predicate;


public class Scores extends CachedSimpleSelectorArgument {

    protected static final String SCORE_SEPARATOR = ",";
    protected static final String SCORE_JOINER = "=";
    protected static final String SCORE_SCOPE_SEPARATOR = "..";

    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        final var conditions = new ArrayList<ScoreCondition>();
        for (String entry : StringUtils.fastSplit(SCORE_SEPARATOR, arguments[0].substring(1, arguments[0].length() - 1))) {
            if (entry.isEmpty()) throw new SelectorSyntaxException("Empty score entry is not allowed in selector!");
            var splittedEntry = StringUtils.fastSplit(SCORE_JOINER, entry, 2);
            String objectiveName = splittedEntry.get(0);
            String condition = splittedEntry.get(1);
            boolean reversed = ParseUtils.checkReversed(condition);
            if (reversed) condition = condition.substring(1);
            if (condition.contains("..")) {
                //条件为一个区间
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                var splittedScoreScope = StringUtils.fastSplit(SCORE_SCOPE_SEPARATOR, condition);
                String min_str = splittedScoreScope.get(0);
                if (!min_str.isEmpty()) {
                    min = Integer.parseInt(min_str);
                }
                String max_str = splittedScoreScope.get(1);
                if (!max_str.isEmpty()) {
                    max = Integer.parseInt(max_str);
                }
                conditions.add(new ScoreCondition(objectiveName, min, max, reversed));
            } else {
                //条件为单个数字
                int score = Integer.parseInt(condition);
                conditions.add(new ScoreCondition(objectiveName, score, score, reversed));
            }
        }
        return entity -> conditions.stream().allMatch(condition -> condition.test(entity));
    }

    @Override
    public String getKeyName() {
        return "scores";
    }

    @Override
    public int getPriority() {
        return 5;
    }

    protected record ScoreCondition(String objectiveName, int min, int max, boolean reversed){
        boolean test(Entity entity) {
            var scoreboard = Server.getInstance().getScoreboardManager().getScoreboard(objectiveName);
            if (scoreboard == null) return false;
            IScorer scorer = entity instanceof Player player ? new PlayerScorer(player) : new EntityScorer(entity);
            if (!scoreboard.containLine(scorer)) return false;
            var value = scoreboard.getLine(scorer).getScore();
            return (value >= min && value <= max) != reversed;
        }
    }
}
