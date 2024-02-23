package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.data.GenericParameter;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.WildcardIntNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.manager.IScoreboardManager;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardLine;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.TextFormat;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;


public class ScoreboardCommand extends VanillaCommand {
    public ScoreboardCommand(String name) {
        super(name, "commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        this.commandParameters.put("objectives-add", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardAddAction", List.of("add"), false)),
                GenericParameter.OBJECTIVES.get(false),
                CommandParameter.newEnum("criteria", false, new CommandEnum("ScoreboardCriteria", List.of("dummy"), false)),
                CommandParameter.newType("displayName", true, CommandParamType.STRING)
        });
        this.commandParameters.put("objectives-list", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardListAction", List.of("list"), false)),
        });
        this.commandParameters.put("objectives-remove", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardRemoveAction", List.of("remove"), false)),
                GenericParameter.OBJECTIVES.get(false),
        });
        this.commandParameters.put("objectives-setdisplay-list-sidebar", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardSetDisplayAction", List.of("setdisplay"), false)),
                CommandParameter.newEnum("displaySlot", false, new CommandEnum("ScoreboardDisplaySlotSortable", List.of("list", "sidebar"), false), CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
                GenericParameter.OBJECTIVES.get(true),
                CommandParameter.newEnum("sortOrder", true, new CommandEnum("ScoreboardSortOrder", List.of("ascending", "descending"), false), CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
        });
        this.commandParameters.put("objectives-setdisplay-belowname", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardSetDisplayAction", List.of("setdisplay"), false)),
                CommandParameter.newEnum("displaySlot", false, new CommandEnum("ScoreboardDisplaySlotNonSortable", List.of("belowname"), false)),
                GenericParameter.OBJECTIVES.get(true)
        });
        this.commandParameters.put("players-add-remove-set", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardPlayersNumAction", List.of("add", "remove", "set"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                GenericParameter.TARGET_OBJECTIVES.get(false),
                CommandParameter.newType("count", CommandParamType.INT)
        });
        this.commandParameters.put("players-list", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardListAction", List.of("list"), false)),
                CommandParameter.newType("playername", true, CommandParamType.WILDCARD_TARGET)//allow *
        });
        this.commandParameters.put("players-operation", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardOperationAction", List.of("operation"), false)),
                CommandParameter.newType("targetName", CommandParamType.WILDCARD_TARGET),//allow *
                GenericParameter.TARGET_OBJECTIVES.get(false),
                CommandParameter.newType("operation", CommandParamType.OPERATOR),
                CommandParameter.newType("selector", CommandParamType.WILDCARD_TARGET),
                GenericParameter.OBJECTIVES.get(false),
        });
        this.commandParameters.put("players-random", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardRandomAction", List.of("random"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                GenericParameter.OBJECTIVES.get(false),
                CommandParameter.newType("min", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MIN_VALUE)),
                CommandParameter.newType("max", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MAX_VALUE))
        });
        this.commandParameters.put("players-reset", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardResetAction", List.of("reset"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                GenericParameter.OBJECTIVES.get(true),
        });
        this.commandParameters.put("players-test", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardTestAction", List.of("test"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                GenericParameter.OBJECTIVES.get(false),
                CommandParameter.newType("min", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MIN_VALUE)),
                CommandParameter.newType("max", true, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MAX_VALUE))
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        var manager = Server.getInstance().getScoreboardManager();
        try {
            switch (result.getKey()) {
                case "objectives-add" -> {
                    String objectiveName = list.getResult(2);
                    if (manager.containScoreboard(objectiveName)) {
                        log.addError("commands.scoreboard.objectives.add.alreadyExists", objectiveName).output();
                        return 0;
                    }
                    String criteriaName = list.getResult(3);
                    if (list.hasResult(4)) {
                        manager.addScoreboard(new Scoreboard(objectiveName, list.getResult(4), criteriaName, SortOrder.ASCENDING));
                    } else {
                        manager.addScoreboard(new Scoreboard(objectiveName, objectiveName, criteriaName, SortOrder.ASCENDING));
                    }
                    log.addSuccess("commands.scoreboard.objectives.add.success", objectiveName).output();
                    return 1;
                }
                case "objectives-list" -> {
                    log.addSuccess(TextFormat.GREEN + "%commands.scoreboard.objectives.list.count", String.valueOf(manager.getScoreboards().size()));
                    for (var scoreboard : manager.getScoreboards().values()) {
                        log.addSuccess("commands.scoreboard.objectives.list.entry", scoreboard.getObjectiveName(), scoreboard.getDisplayName(), scoreboard.getCriteriaName());
                    }
                    log.output(true);
                    return 1;
                }
                case "objectives-remove" -> {
                    String objectiveName = list.getResult(2);
                    if (!manager.containScoreboard(objectiveName)) {
                        log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                        return 0;
                    }
                    if (manager.removeScoreboard(objectiveName)) {
                        log.addSuccess("commands.scoreboard.objectives.remove.success", objectiveName).output();
                    }
                    return 1;
                }
                case "objectives-setdisplay-list-sidebar" -> {
                    String slotName = list.getResult(2);
                    DisplaySlot slot = switch (slotName) {
                        case "sidebar" -> DisplaySlot.SIDEBAR;
                        case "list" -> DisplaySlot.LIST;
                        default -> DisplaySlot.SIDEBAR;
                    };

                    if (!list.hasResult(3)) {
                        manager.setDisplay(slot, null);
                        log.addSuccess("commands.scoreboard.objectives.setdisplay.successCleared", slot.getSlotName()).output();
                    } else {
                        String objectiveName = list.getResult(3);
                        if (!manager.containScoreboard(objectiveName)) {
                            log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                            return 0;
                        }
                        var scoreboard = manager.getScoreboards().get(objectiveName);
                        String orderName = list.getResult(4);
                        SortOrder order = list.hasResult(4) ? switch (orderName) {
                            case "ascending" -> SortOrder.ASCENDING;
                            case "descending" -> SortOrder.DESCENDING;
                            default -> SortOrder.ASCENDING;
                        } : SortOrder.ASCENDING;
                        scoreboard.setSortOrder(order);
                        manager.setDisplay(slot, scoreboard);
                        log.addSuccess("commands.scoreboard.objectives.setdisplay.successSet", slot.getSlotName(), objectiveName).output();
                    }
                    return 1;
                }
                case "objectives-setdisplay-belowname" -> {
                    if (!list.hasResult(3)) {
                        manager.setDisplay(DisplaySlot.BELOW_NAME, null);
                        log.addSuccess("commands.scoreboard.objectives.setdisplay.successCleared", DisplaySlot.BELOW_NAME.getSlotName()).output();
                        return 1;
                    } else {
                        String objectiveName = list.getResult(3);
                        if (!manager.containScoreboard(objectiveName)) {
                            log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                            return 0;
                        }
                        manager.setDisplay(DisplaySlot.BELOW_NAME, manager.getScoreboard(objectiveName));
                        log.addSuccess("commands.scoreboard.objectives.setdisplay.successSet", DisplaySlot.BELOW_NAME.getSlotName(), objectiveName).output();
                        return 1;
                    }
                }
                case "players-add-remove-set" -> {
                    return this.playersCRUD(list, sender, manager, log);
                }
                case "players-list" -> {
                    if (manager.getScoreboards().isEmpty()) {
                        log.addError("commands.scoreboard.players.list.empty").output();
                        return 0;
                    }
                    if (list.hasResult(2)) {
                        String wildcard_target_str = list.getResult(2);
                        Set<IScorer> scorers = parseScorers(sender, wildcard_target_str);
                        if (scorers.isEmpty()) {
                            log.addError("commands.scoreboard.players.list.empty").output();
                            return 0;
                        }
                        for (IScorer scorer : scorers) {
                            boolean find = false;
                            int count = 0;
                            for (var scoreboard : manager.getScoreboards().values()) {
                                if (scoreboard.getLines().containsKey(scorer)) {
                                    find = true;
                                    count++;
                                }
                            }
                            if (!find) {
                                log.addError("commands.scoreboard.players.list.player.empty", scorer.getName()).output();
                                return 0;
                            }
                            log.addSuccess(TextFormat.GREEN + "%commands.scoreboard.players.list.player.count", String.valueOf(count), scorer.getName());
                            for (var scoreboard : manager.getScoreboards().values()) {
                                if (scoreboard.getLines().containsKey(scorer)) {
                                    log.addSuccess("commands.scoreboard.players.list.player.entry", String.valueOf(scoreboard.getLines().get(scorer).getScore()), scoreboard.getDisplayName(), scoreboard.getObjectiveName());
                                }
                            }
                            log.output();
                        }
                        return 1;
                    } else {
                        Set<String> scorerNames = new LinkedHashSet<>();
                        manager.getScoreboards().values().forEach(
                                scoreboard -> scoreboard.getLines().values().forEach(
                                        line -> scorerNames.add(TextFormat.WHITE + line.getScorer().getName())
                                )
                        );
                        log.addSuccess(TextFormat.GREEN + "%commands.scoreboard.players.list.count", String.valueOf(scorerNames.size()));
                        var join = new StringJoiner(",");
                        scorerNames.forEach(join::add);
                        log.addSuccess(join.toString()).output();
                    }
                }
                case "players-operation" -> {
                    return this.playersOperate(list, sender, manager, log);
                }
                case "players-random" -> {
                    String wildcard_target_str = list.getResult(2);
                    Set<IScorer> scorers = parseScorers(sender, wildcard_target_str);
                    if (scorers.isEmpty()) {
                        log.addNoTargetMatch().output();
                        return 0;
                    }
                    String objectiveName = list.getResult(3);
                    if (!manager.containScoreboard(objectiveName)) {
                        log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                        return 0;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    long min = list.getResult(4);
                    long max = list.getResult(5);
                    if (min > max) {
                        log.addError("commands.scoreboard.players.random.invalidRange", String.valueOf(min), String.valueOf(max)).output();
                        return 0;
                    }
                    Random random = new Random();
                    for (IScorer scorer : scorers) {
                        int score = (int) (min + random.nextLong(max - min + 1));//avoid "java.lang.IllegalArgumentException: bound must be positive"
                        if (!scoreboard.getLines().containsKey(scorer)) {
                            scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                        }
                        scoreboard.getLines().get(scorer).setScore(score);
                        log.addMessage("commands.scoreboard.players.set.success", objectiveName, scorer.getName(), String.valueOf(score));
                    }
                    log.output();
                    return 1;
                }
                case "players-reset" -> {
                    String wildcard_target_str = list.getResult(2);
                    Set<IScorer> scorers = parseScorers(sender, wildcard_target_str);
                    if (scorers.isEmpty()) {
                        log.addNoTargetMatch().output();
                        return 0;
                    }
                    if (list.hasResult(3)) {
                        String objectiveName = list.getResult(3);
                        if (!manager.containScoreboard(objectiveName)) {
                            log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                            return 0;
                        }
                        var scoreboard = manager.getScoreboards().get(objectiveName);
                        for (IScorer scorer : scorers) {
                            if (scoreboard.containLine(scorer)) {
                                scoreboard.removeLine(scorer);
                                log.addMessage("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName());
                            }
                        }
                        log.output();
                        return 1;
                    } else {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            for (IScorer scorer : scorers) {
                                if (scoreboard.containLine(scorer)) {
                                    scoreboard.removeLine(scorer);
                                    log.addMessage("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName());
                                }
                            }
                        }
                        log.output();
                        return 1;
                    }
                }
                case "players-test" -> {
                    String wildcard_target_str = list.getResult(2);
                    Set<IScorer> scorers = parseScorers(sender, wildcard_target_str);
                    if (scorers.isEmpty()) {
                        log.addNoTargetMatch().output();
                        return 0;
                    }
                    String objectiveName = list.getResult(3);
                    if (!manager.containScoreboard(objectiveName)) {
                        log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
                        return 0;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    int min = list.getResult(4);
                    int max = Integer.MAX_VALUE;
                    if (list.hasResult(5)) {
                        max = list.getResult(5);
                    }
                    for (IScorer scorer : scorers) {
                        var line = scoreboard.getLine(scorer);
                        if (line == null) {
                            log.addError("commands.scoreboard.players.score.notFound", objectiveName, scorer.getName()).output();
                            return 0;
                        }
                        int score = line.getScore();
                        if (score < min || score > max) {
                            log.addError("commands.scoreboard.players.test.failed", String.valueOf(score), String.valueOf(min), String.valueOf(max)).output();
                            return 0;
                        }
                        log.addMessage("commands.scoreboard.players.test.success", String.valueOf(score), String.valueOf(min), String.valueOf(max));
                    }
                    log.output();
                    return 1;
                }
            }
        } catch (SelectorSyntaxException e) {
            log.addError(e.getMessage());
            log.output();
        }
        return 0;
    }

    private int playersCRUD(ParamList list, CommandSender sender, IScoreboardManager manager, CommandLogger log) throws SelectorSyntaxException {
        String ars = list.getResult(1);
        String objectiveName = list.getResult(3);
        if (!manager.containScoreboard(objectiveName)) {
            log.addError("commands.scoreboard.objectiveNotFound", objectiveName).output();
            return 0;
        }
        var scoreboard = manager.getScoreboards().get(objectiveName);
        String wildcard_target_str = list.getResult(2);
        Set<IScorer> scorers = parseScorers(sender, wildcard_target_str, scoreboard);
        if (scorers.isEmpty()) {
            log.addError("commands.scoreboard.players.list.empty").output();
            return 0;
        }
        int score = list.getResult(4);
        int count = scorers.size();
        switch (ars) {
            case "add" -> {
                for (IScorer scorer : scorers) {
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                    } else {
                        scoreboard.getLines().get(scorer).addScore(score);
                    }
                }
                if (count == 1) {
                    var scorer = scorers.iterator().next();
                    log.addSuccess("commands.scoreboard.players.add.success", String.valueOf(score), objectiveName, scorer.getName(), String.valueOf(scoreboard.getLines().get(scorer).getScore()));
                } else {
                    log.addSuccess("commands.scoreboard.players.add.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count));
                }
                log.output();
                return 1;
            }
            case "remove" -> {
                for (IScorer scorer : scorers) {
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, -score));
                    }
                    scoreboard.getLines().get(scorer).removeScore(score);
                }
                if (count == 1) {
                    var scorer = scorers.iterator().next();
                    log.addSuccess("commands.scoreboard.players.remove.success", String.valueOf(score), objectiveName, scorer.getName(), String.valueOf(scoreboard.getLines().get(scorer).getScore()));
                } else {
                    log.addSuccess("commands.scoreboard.players.remove.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count));
                }
                log.output();
                return 1;
            }
            case "set" -> {
                for (IScorer scorer : scorers) {
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                    }
                    scoreboard.getLines().get(scorer).setScore(score);
                }
                if (count == 1) {
                    var scorer = scorers.iterator().next();
                    log.addSuccess("commands.scoreboard.players.set.success", objectiveName, scorer.getName(), String.valueOf(score));
                } else {
                    log.addSuccess("commands.scoreboard.players.set.multiple.success", objectiveName, String.valueOf(count), String.valueOf(score));
                }
                log.output();
                return 1;
            }
        }
        return 0;
    }

    private int playersOperate(ParamList list, CommandSender sender, IScoreboardManager manager, CommandLogger log) throws SelectorSyntaxException {
        String targetObjectiveName = list.getResult(3);
        if (!manager.containScoreboard(targetObjectiveName)) {
            log.addError("commands.scoreboard.objectiveNotFound", targetObjectiveName).output();
            return 0;
        }
        var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

        String wildcard_target_str = list.getResult(2);
        Set<IScorer> targetScorers = parseScorers(sender, wildcard_target_str, targetScoreboard);
        if (targetScorers.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        String operation = list.getResult(4);


        String selectorObjectiveName = list.getResult(6);
        if (!manager.containScoreboard(selectorObjectiveName)) {
            log.addError("commands.scoreboard.objectiveNotFound", selectorObjectiveName).output();
            return 0;
        }
        var selectorScoreboard = manager.getScoreboards().get(targetObjectiveName);

        String selector_str = list.getResult(5);
        Set<IScorer> selectorScorers = parseScorers(sender, selector_str, selectorScoreboard);
        if (selectorScorers.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        for (IScorer targetScorer : targetScorers) {
            for (IScorer selectorScorer : selectorScorers) {
                if (!targetScoreboard.getLines().containsKey(targetScorer)) {
                    log.addError("commands.scoreboard.players.operation.notFound", targetObjectiveName, targetScorer.getName()).output();
                    return 0;
                }
                if (!selectorScoreboard.getLines().containsKey(selectorScorer)) {
                    log.addError("commands.scoreboard.players.operation.notFound", selectorObjectiveName, selectorScorer.getName()).output();
                    return 0;
                }
                int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                int selectorScore = selectorScoreboard.getLines().get(selectorScorer).getScore();
                int changedScore = -1;
                switch (operation) {
                    case "+=" -> {
                        changedScore = targetScore + selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "-=" -> {
                        changedScore = targetScore - selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "*=" -> {
                        changedScore = targetScore * selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "/=" -> {
                        changedScore = targetScore / selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "%=" -> {
                        changedScore = targetScore % selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "=" -> {
                        changedScore = selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "<" -> {
                        changedScore = Math.min(targetScore, selectorScore);
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case ">" -> {
                        changedScore = Math.max(targetScore, selectorScore);
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                    }
                    case "><" -> {
                        changedScore = selectorScore;
                        targetScoreboard.getLines().get(targetScorer).setScore(changedScore);
                        selectorScoreboard.getLines().get(selectorScorer).setScore(targetScore);
                    }
                }
                log.addMessage("commands.scoreboard.players.operation.success", String.valueOf(changedScore), targetObjectiveName);
            }
        }
        log.output();
        return 1;
    }

    private Set<IScorer> parseScorers(CommandSender sender, String wildcardTargetStr) throws SelectorSyntaxException {
        return parseScorers(sender, wildcardTargetStr, null);
    }

    private Set<IScorer> parseScorers(CommandSender sender, String wildcardTargetStr, @Nullable IScoreboard wildcardScoreboard) throws SelectorSyntaxException {
        var manager = Server.getInstance().getScoreboardManager();
        Set<IScorer> scorers = new HashSet<>();
        if (wildcardTargetStr.equals("*")) {
            if (wildcardScoreboard != null) {
                scorers.addAll(wildcardScoreboard.getLines().keySet());
            } else {
                for (var scoreboard : manager.getScoreboards().values()) {
                    scorers.addAll(scoreboard.getLines().keySet());
                }
            }
        } else if (EntitySelectorAPI.getAPI().checkValid(wildcardTargetStr)) {
            scorers = EntitySelectorAPI.getAPI().matchEntities(sender, wildcardTargetStr).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
        } else if (Server.getInstance().getPlayer(wildcardTargetStr) != null) {
            scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcardTargetStr)));
        } else {
            scorers.add(new FakeScorer(wildcardTargetStr));
        }
        return scorers;
    }
}
