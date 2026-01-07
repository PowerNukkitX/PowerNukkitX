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

/**
 * Selector argument implementation for the 'scores' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'scores' argument is used to filter entities based on their scoreboard objective values. It allows specifying
 * one or more scoreboard objectives, each with a required value, a value range, or a negated condition. This enables
 * selectors such as @e[scores={kills=5,health=10..20}] to select entities with a kills score of 5 and a health score between 10 and 20.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'scores' selector argument for scoreboard-based entity filtering.</li>
 *   <li>Allows multiple objectives, each with a single value, a value range (min..max), or negation (prefix with '!').</li>
 *   <li>Parses the argument as a map of objective names to conditions, supporting both exact and ranged values.</li>
 *   <li>Returns a predicate that checks if an entity's score for each objective matches the specified condition(s).</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 *   <li>Handles both player and non-player entities using {@link PlayerScorer} and {@link EntityScorer}.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for invalid or empty score entries.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[scores=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[scores={kills=5}] selects entities with a kills score of 5
 * // @e[scores={health=10..20}] selects entities with a health score between 10 and 20
 * // @e[scores={xp=!0}] selects entities with a non-zero xp score
 * // @e[scores={deaths=..3}] selects entities with a deaths score of 3 or less
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must be a valid map in the form {objective=value,...}.</li>
 *   <li>Each value can be a single integer, a range (min..max), or negated by prefixing with '!'.</li>
 *   <li>Empty score entries are not allowed.</li>
 * </ul>
 *
 * <b>Scoreboard Handling:</b>
 * <ul>
 *   <li>Works with both player and non-player entities using the scoreboard API.</li>
 *   <li>Checks if the entity has a score for the specified objective before comparing values.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @see cn.nukkit.scoreboard.scorer.PlayerScorer
 * @see cn.nukkit.scoreboard.scorer.EntityScorer
 * @since PowerNukkitX 2.0.0
 */
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
