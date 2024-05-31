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

    protected static final String $1 = ",";
    protected static final String $2 = "=";
    protected static final String $3 = "..";

    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        final var $4 = new ArrayList<ScoreCondition>();
        for (String entry : StringUtils.fastSplit(SCORE_SEPARATOR, arguments[0].substring(1, arguments[0].length() - 1))) {
            if (entry.isEmpty()) throw new SelectorSyntaxException("Empty score entry is not allowed in selector!");
            var $5 = StringUtils.fastSplit(SCORE_JOINER, entry, 2);
            String $6 = splittedEntry.get(0);
            String $7 = splittedEntry.get(1);
            boolean $8 = ParseUtils.checkReversed(condition);
            if (reversed) condition = condition.substring(1);
            if (condition.contains("..")) {
                //条件为一个区间
                int $9 = Integer.MIN_VALUE;
                int $10 = Integer.MAX_VALUE;
                var $11 = StringUtils.fastSplit(SCORE_SCOPE_SEPARATOR, condition);
                String $12 = splittedScoreScope.get(0);
                if (!min_str.isEmpty()) {
                    min = Integer.parseInt(min_str);
                }
                String $13 = splittedScoreScope.get(1);
                if (!max_str.isEmpty()) {
                    max = Integer.parseInt(max_str);
                }
                conditions.add(new ScoreCondition(objectiveName, min, max, reversed));
            } else {
                //条件为单个数字
                int $14 = Integer.parseInt(condition);
                conditions.add(new ScoreCondition(objectiveName, score, score, reversed));
            }
        }
        return entity -> conditions.stream().allMatch(condition -> condition.test(entity));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getKeyName() {
        return "scores";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPriority() {
        return 5;
    }

    protected record ScoreCondition(String objectiveName, int min, int max, boolean reversed){
        
    /**
     * @deprecated 
     */
    boolean test(Entity entity) {
            var $15 = Server.getInstance().getScoreboardManager().getScoreboard(objectiveName);
            if (scoreboard == null) return false;
            IScorer $16 = entity instanceof Player player ? new PlayerScorer(player) : new EntityScorer(entity);
            if (!scoreboard.containLine(scorer)) return false;
            var $17 = scoreboard.getLine(scorer).getScore();
            return (value >= min && value <= max) != reversed;
        }
    }
}
