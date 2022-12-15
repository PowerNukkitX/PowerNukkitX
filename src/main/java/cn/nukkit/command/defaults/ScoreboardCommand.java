package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.WildcardIntNode;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.manager.IScoreboardManager;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.scoreboard.ScoreboardLine;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.TextFormat;

import java.util.*;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ScoreboardCommand extends VanillaCommand {
    private final ParamTree paramTree;

    public ScoreboardCommand(String name) {
        super(name, "commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        this.commandParameters.put("objectives-add", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardAddAction", List.of("add"), false)),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
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
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
        });
        this.commandParameters.put("objectives-setdisplay-list-sidebar", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardSetDisplayAction", List.of("setdisplay"), false)),
                CommandParameter.newEnum("displaySlot", false, new CommandEnum("ScoreboardDisplaySlotSortable", List.of("list", "sidebar"), false), CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
                CommandParameter.newEnum("objective", true, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CommandParameter.newEnum("sortOrder", true, new CommandEnum("ScoreboardSortOrder", List.of("ascending", "descending"), false), CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
        });
        this.commandParameters.put("objectives-setdisplay-belowname", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardObjectivesCategory", List.of("objectives"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardSetDisplayAction", List.of("setdisplay"), false)),
                CommandParameter.newEnum("displaySlot", false, new CommandEnum("ScoreboardDisplaySlotNonSortable", List.of("belowname"), false)),
                CommandParameter.newEnum("objective", true, new CommandEnum("ScoreboardObjectives", List.of(), true))
        });
        this.commandParameters.put("players-add-remove-set", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardPlayersNumAction", List.of("add", "remove", "set"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newEnum("targetObjective", false, "ScoreboardObjectives", true),
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
                CommandParameter.newEnum("targetObjective", false, "ScoreboardObjectives", true),
                CommandParameter.newType("operation", CommandParamType.OPERATOR),
                CommandParameter.newType("selector", CommandParamType.WILDCARD_TARGET),
                CommandParameter.newEnum("objective", false, "ScoreboardObjectives", true)
        });
        this.commandParameters.put("players-random", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardRandomAction", List.of("random"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newEnum("objective", false, "ScoreboardObjectives", true),
                CommandParameter.newType("min", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MIN_VALUE)),
                CommandParameter.newType("max", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MAX_VALUE))
        });
        this.commandParameters.put("players-reset", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardResetAction", List.of("reset"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newEnum("objective", true, "ScoreboardObjectives", true)
        });
        this.commandParameters.put("players-test", new CommandParameter[]{
                CommandParameter.newEnum("category", false, new CommandEnum("ScoreboardPlayersCategory", List.of("players"), false)),
                CommandParameter.newEnum("action", false, new CommandEnum("ScoreboardTestAction", List.of("test"), false)),
                CommandParameter.newType("player", false, CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newEnum("objective", false, "ScoreboardObjectives", true),
                CommandParameter.newType("min", false, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MIN_VALUE)),
                CommandParameter.newType("max", true, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MAX_VALUE))
        });
        this.paramTree = new ParamTree(this);
    }

    /*@Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        var manager = Server.getInstance().getScoreboardManager();
        CommandParser parser = new CommandParser(this, sender, args);
        try {
            String form = parser.matchCommandForm();
            if (form == null) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
            ;
            switch (form) {
                case "objectives-add" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String objectiveName = p.parseString();
                    if (manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectives.add.alreadyExists", objectiveName));
                        return false;
                    }
                    String criteriaName = p.parseString();
                    String displayName = p.parseString();
                    manager.addScoreboard(new Scoreboard(objectiveName, displayName, criteriaName, SortOrder.ASCENDING));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.success", objectiveName));
                    return true;
                }
                case "objectives-list" -> {
                    sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.objectives.list.count", String.valueOf(manager.getScoreboards().size())));
                    for (var scoreboard : manager.getScoreboards().values()) {
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.list.entry", scoreboard.getObjectiveName(), scoreboard.getDisplayName(), scoreboard.getCriteriaName()));
                    }
                    return true;
                }
                case "objectives-remove" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String objectiveName = p.parseString();
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    if (manager.removeScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.remove.success", objectiveName));
                    }
                    return true;
                }
                case "objectives-setdisplay-list-sidebar" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    DisplaySlot slot = switch (p.parseString()) {
                        case "sidebar" -> DisplaySlot.SIDEBAR;
                        case "list" -> DisplaySlot.LIST;
                        default -> DisplaySlot.SIDEBAR;
                    };
                    if (!p.hasNext()) {
                        manager.setDisplay(slot, null);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", slot.getSlotName()));
                        return true;
                    } else {
                        String objectiveName = p.parseString();
                        if (!manager.containScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        var scoreboard = manager.getScoreboards().get(objectiveName);
                        SortOrder order = p.hasNext() ? switch (p.parseString()) {
                            case "ascending" -> SortOrder.ASCENDING;
                            case "descending" -> SortOrder.DESCENDING;
                            default -> SortOrder.ASCENDING;
                        } : SortOrder.ASCENDING;
                        scoreboard.setSortOrder(order);
                        manager.setDisplay(slot, scoreboard);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet", slot.getSlotName(), objectiveName));
                        return true;
                    }
                }
                case "objectives-setdisplay-belowname" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    p.parseString();
                    if (!p.hasNext()) {
                        manager.setDisplay(DisplaySlot.BELOW_NAME, null);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", DisplaySlot.BELOW_NAME.getSlotName()));
                        return true;
                    } else {
                        String objectiveName = p.parseString();
                        if (!manager.containScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        manager.setDisplay(DisplaySlot.BELOW_NAME, manager.getScoreboard(objectiveName));
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet", DisplaySlot.BELOW_NAME.getSlotName(), objectiveName));
                        return true;
                    }
                }
                case "players-add-remove-set" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    String ars = p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    List<IScorer> scorers = new ArrayList<>();
                    boolean wildcard = false;
                    if (wildcard_target_str.equals("*")) {
                        wildcard = true;
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toList());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    if (wildcard)
                        scorers.addAll(scoreboard.getLines().keySet());
                    if (scorers.isEmpty()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
                        return false;
                    }
                    int score = p.parseInt();
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
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.success", String.valueOf(score), objectiveName, scorers.get(0).getName(), String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                            } else {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count)));
                            }
                            return true;
                        }
                        case "remove" -> {
                            for (IScorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)) {
                                    scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, -score));
                                }
                                scoreboard.getLines().get(scorer).removeScore(score);
                            }
                            if (count == 1) {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.success", String.valueOf(score), objectiveName, scorers.get(0).getName(), String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                            } else {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count)));
                            }
                            return true;
                        }
                        case "set" -> {
                            for (IScorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)) {
                                    scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                                }
                                scoreboard.getLines().get(scorer).setScore(score);
                            }
                            if (count == 1) {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success", objectiveName, scorers.get(0).getName(), String.valueOf(score)));
                            } else {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.multiple.success", objectiveName, String.valueOf(count), String.valueOf(score)));
                            }
                            return true;
                        }
                    }
                    return false;
                }
                case "players-list" -> {
                    if (manager.getScoreboards().isEmpty()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
                        return false;
                    }
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<IScorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
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
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.player.empty", scorer.getName()));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.players.list.player.count", String.valueOf(count), scorer.getName()));
                        for (var scoreboard : manager.getScoreboards().values()) {
                            if (scoreboard.getLines().containsKey(scorer)) {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.entry", String.valueOf(scoreboard.getLines().get(scorer).getScore()), scoreboard.getDisplayName(), scoreboard.getObjectiveName()));
                            }
                        }
                    }
                    return true;
                }
                case "players-operation" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String wildcard_target_str = p.parseString(false);

                    Set<IScorer> targetScorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            targetScorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        targetScorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        targetScorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        targetScorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }

                    String targetObjectiveName = p.parseString();
                    if (!manager.containScoreboard(targetObjectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", targetObjectiveName));
                        return false;
                    }
                    var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

                    String operation = p.parseString();

                    String selector_str = p.parseString(false);
                    Set<IScorer> selectorScorers = p.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    if (selectorScorers.size() > 1) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                        return false;
                    }
                    if (selectorScorers.size() == 0) {
                        selectorScorers.add(new FakeScorer(selector_str));
                    }
                    IScorer seletorScorer = selectorScorers.iterator().next();

                    String selectorObjectiveName = p.parseString();
                    if (!manager.containScoreboard(selectorObjectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", selectorObjectiveName));
                        return false;
                    }
                    var selectorScoreboard = manager.getScoreboards().get(targetObjectiveName);

                    if (!selectorScoreboard.getLines().containsKey(seletorScorer)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", selectorObjectiveName, seletorScorer.getName()));
                        return false;
                    }

                    for (IScorer targetScorer : targetScorers) {
                        if (!targetScoreboard.getLines().containsKey(targetScorer)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", targetObjectiveName, targetScorer.getName()));
                            return false;
                        }
                        int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                        int selectorScore = selectorScoreboard.getLines().get(seletorScorer).getScore();
                        switch (operation) {
                            case "+=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(targetScore + selectorScore);
                            }
                            case "-=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(targetScore - selectorScore);
                            }
                            case "*=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(targetScore * selectorScore);
                            }
                            case "/=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(targetScore / selectorScore);
                            }
                            case "%=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(targetScore % selectorScore);
                            }
                            case "=" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(selectorScore);
                            }
                            case "<" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(Math.min(targetScore, selectorScore));
                            }
                            case ">" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(Math.max(targetScore, selectorScore));
                            }
                            case "><" -> {
                                targetScoreboard.getLines().get(targetScorer).setScore(selectorScore);
                                selectorScoreboard.getLines().get(seletorScorer).setScore(targetScore);
                            }
                        }
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.operation.success", targetObjectiveName, String.valueOf(targetScorers.size())));
                    }
                    return true;
                }
                case "players-random" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<IScorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    long min = p.parseWildcardInt(Integer.MIN_VALUE);
                    long max = Integer.MAX_VALUE;
                    if (p.hasNext()) {
                        max = p.parseWildcardInt(Integer.MAX_VALUE);
                    }
                    if (min > max) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.random.invalidRange", String.valueOf(min), String.valueOf(max)));
                        return false;
                    }
                    Random random = new Random();
                    for (IScorer scorer : scorers) {
                        int score = (int) (min + random.nextLong(max - min + 1));//avoid "java.lang.IllegalArgumentException: bound must be positive"
                        if (!scoreboard.getLines().containsKey(scorer)) {
                            scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                        }
                        scoreboard.getLines().get(scorer).setScore(score);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success", objectiveName, scorer.getName(), String.valueOf(score)));
                    }
                    return true;
                }
                case "players-reset" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<IScorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    if (p.hasNext()) {
                        String objectiveName = p.parseString();
                        if (!manager.containScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        var scoreboard = manager.getScoreboards().get(objectiveName);
                        for (IScorer scorer : scorers) {
                            if (scoreboard.containLine(scorer)) {
                                scoreboard.removeLine(scorer);
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName()));
                            }
                        }
                        return true;
                    } else {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            for (IScorer scorer : scorers) {
                                if (scoreboard.containLine(scorer)) {
                                    scoreboard.removeLine(scorer);
                                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName()));
                                }
                            }
                        }
                        return true;
                    }
                }
                case "players-test" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<IScorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    int min = p.parseWildcardInt(Integer.MIN_VALUE);
                    int max = Integer.MAX_VALUE;
                    if (p.hasNext()) {
                        max = p.parseWildcardInt(Integer.MAX_VALUE);
                    }
                    for (IScorer scorer : scorers) {
                        var line = scoreboard.getLine(scorer);
                        if (line == null) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.score.notFound", objectiveName, scorer.getName()));
                            return false;
                        }
                        int score = line.getScore();
                        if (score < min || score > max) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.test.failed", String.valueOf(score), String.valueOf(min), String.valueOf(max)));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.test.success", String.valueOf(score), String.valueOf(min), String.valueOf(max)));
                    }
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
        }
        return false;
    }*/

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        var result = paramTree.matchAndParse(sender, args);
        if (result == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        var manager = Server.getInstance().getScoreboardManager();
        var list = result.getValue();
        switch (result.getKey()) {
            case "objectives-add" -> {
                String objectiveName = list.getResult(2);
                if (manager.containScoreboard(objectiveName)) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectives.add.alreadyExists", objectiveName));
                    return false;
                }
                String criteriaName = list.getResult(3);
                if (list.hasResult(4)) {
                    manager.addScoreboard(new Scoreboard(objectiveName, list.getResult(4), criteriaName, SortOrder.ASCENDING));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.success", objectiveName));
                } else {
                    manager.addScoreboard(new Scoreboard(objectiveName, objectiveName, criteriaName, SortOrder.ASCENDING));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.success", objectiveName));
                }
                return true;
            }
            case "objectives-list" -> {
                sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.objectives.list.count", String.valueOf(manager.getScoreboards().size())));
                for (var scoreboard : manager.getScoreboards().values()) {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.list.entry", scoreboard.getObjectiveName(), scoreboard.getDisplayName(), scoreboard.getCriteriaName()));
                }
                return true;
            }
            case "objectives-remove" -> {
                String objectiveName = list.getResult(2);
                if (!manager.containScoreboard(objectiveName)) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                    return false;
                }
                if (manager.removeScoreboard(objectiveName)) {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.remove.success", objectiveName));
                }
                return true;
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
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", slot.getSlotName()));
                } else {
                    String objectiveName = list.getResult(3);
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
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
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet", slot.getSlotName(), objectiveName));
                }
                return true;
            }
            case "objectives-setdisplay-belowname" -> {
                if (!list.hasResult(3)) {
                    manager.setDisplay(DisplaySlot.BELOW_NAME, null);
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", DisplaySlot.BELOW_NAME.getSlotName()));
                    return true;
                } else {
                    String objectiveName = list.getResult(3);
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    manager.setDisplay(DisplaySlot.BELOW_NAME, manager.getScoreboard(objectiveName));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet", DisplaySlot.BELOW_NAME.getSlotName(), objectiveName));
                    return true;
                }
            }
            case "players-add-remove-set" -> {
                return this.playersCRUD(list, sender, manager);
            }
            case "players-list" -> {
                if (manager.getScoreboards().isEmpty()) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
                    return false;
                }
                if (list.hasResult(2)) {
                    String wildcard_target_str = list.getResult(2);
                    Set<IScorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (var scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                    } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
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
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.player.empty", scorer.getName()));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.players.list.player.count", String.valueOf(count), scorer.getName()));
                        for (var scoreboard : manager.getScoreboards().values()) {
                            if (scoreboard.getLines().containsKey(scorer)) {
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.entry", String.valueOf(scoreboard.getLines().get(scorer).getScore()), scoreboard.getDisplayName(), scoreboard.getObjectiveName()));
                            }
                        }
                    }
                    return true;
                } else {
                    for (var display : manager.getDisplay().values()) {
                        if (display != null) {
                            for (var line : display.getLines().values()) {
                                System.out.println(1);
                                sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.players.list.player.count", String.valueOf(display.getLines().values().size()), sender.getName()));
                                sender.sendMessage(new TranslationContainer(TextFormat.GREEN + line.getScorer().getName()));
                            }
                        }
                    }
                }

            }
            case "players-operation" -> {
                return this.playersOperate(list, sender, manager);
            }
            case "players-random" -> {
                String wildcard_target_str = list.getResult(2);
                Set<IScorer> scorers = new HashSet<>();
                if (wildcard_target_str.equals("*")) {
                    for (var scoreboard : manager.getScoreboards().values()) {
                        scorers.addAll(scoreboard.getLines().keySet());
                    }
                } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                    scorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                    scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                } else {
                    scorers.add(new FakeScorer(wildcard_target_str));
                }
                String objectiveName = list.getResult(3);
                if (!manager.containScoreboard(objectiveName)) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                    return false;
                }
                var scoreboard = manager.getScoreboards().get(objectiveName);
                long min = list.getResult(4);
                long max = list.getResult(5);
                if (min > max) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.random.invalidRange", String.valueOf(min), String.valueOf(max)));
                    return false;
                }
                Random random = new Random();
                for (IScorer scorer : scorers) {
                    int score = (int) (min + random.nextLong(max - min + 1));//avoid "java.lang.IllegalArgumentException: bound must be positive"
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                    }
                    scoreboard.getLines().get(scorer).setScore(score);
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success", objectiveName, scorer.getName(), String.valueOf(score)));
                }
                return true;
            }
            case "players-reset" -> {
                String wildcard_target_str = list.getResult(2);
                Set<IScorer> scorers = new HashSet<>();
                if (wildcard_target_str.equals("*")) {
                    for (var scoreboard : manager.getScoreboards().values()) {
                        scorers.addAll(scoreboard.getLines().keySet());
                    }
                } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                    scorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                    scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                } else {
                    scorers.add(new FakeScorer(wildcard_target_str));
                }
                if (list.hasResult(3)) {
                    String objectiveName = list.getResult(3);
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    var scoreboard = manager.getScoreboards().get(objectiveName);
                    for (IScorer scorer : scorers) {
                        if (scoreboard.containLine(scorer)) {
                            scoreboard.removeLine(scorer);
                            sender.sendMessage(new TranslationContainer("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName()));
                        }
                    }
                    return true;
                } else {
                    for (var scoreboard : manager.getScoreboards().values()) {
                        for (IScorer scorer : scorers) {
                            if (scoreboard.containLine(scorer)) {
                                scoreboard.removeLine(scorer);
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName()));
                            }
                        }
                    }
                    return true;
                }
            }
            case "players-test" -> {
                String wildcard_target_str = list.getResult(2);
                Set<IScorer> scorers = new HashSet<>();
                if (wildcard_target_str.equals("*")) {
                    for (var scoreboard : manager.getScoreboards().values()) {
                        scorers.addAll(scoreboard.getLines().keySet());
                    }
                } else if (EntitySelector.hasArguments(wildcard_target_str)) {
                    scorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                    scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                } else {
                    scorers.add(new FakeScorer(wildcard_target_str));
                }
                String objectiveName = list.getResult(3);
                if (!manager.containScoreboard(objectiveName)) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                    return false;
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
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.score.notFound", objectiveName, scorer.getName()));
                        return false;
                    }
                    int score = line.getScore();
                    if (score < min || score > max) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.test.failed", String.valueOf(score), String.valueOf(min), String.valueOf(max)));
                        return false;
                    }
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.test.success", String.valueOf(score), String.valueOf(min), String.valueOf(max)));
                }
                return true;
            }
        }
        return false;
    }

    private boolean playersCRUD(ParamList list, CommandSender sender, IScoreboardManager manager) {
        String ars = list.getResult(1);
        String wildcard_target_str = list.getResult(2);
        List<IScorer> scorers = new ArrayList<>();
        boolean wildcard = false;
        if (wildcard_target_str.equals("*")) {
            wildcard = true;
        } else if (EntitySelector.hasArguments(wildcard_target_str)) {
            scorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toList());
        } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
            scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
        } else {
            scorers.add(new FakeScorer(wildcard_target_str));
        }
        String objectiveName = list.getResult(3);
        if (!manager.containScoreboard(objectiveName)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
            return false;
        }
        var scoreboard = manager.getScoreboards().get(objectiveName);
        if (wildcard)
            scorers.addAll(scoreboard.getLines().keySet());
        if (scorers.isEmpty()) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
            return false;
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
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.success", String.valueOf(score), objectiveName, scorers.get(0).getName(), String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                } else {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count)));
                }
                return true;
            }
            case "remove" -> {
                for (IScorer scorer : scorers) {
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, -score));
                    }
                    scoreboard.getLines().get(scorer).removeScore(score);
                }
                if (count == 1) {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.success", String.valueOf(score), objectiveName, scorers.get(0).getName(), String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                } else {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.multiple.success", String.valueOf(score), objectiveName, String.valueOf(count)));
                }
                return true;
            }
            case "set" -> {
                for (IScorer scorer : scorers) {
                    if (!scoreboard.getLines().containsKey(scorer)) {
                        scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
                    }
                    scoreboard.getLines().get(scorer).setScore(score);
                }
                if (count == 1) {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success", objectiveName, scorers.get(0).getName(), String.valueOf(score)));
                } else {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.multiple.success", objectiveName, String.valueOf(count), String.valueOf(score)));
                }
                return true;
            }
        }
        return false;
    }

    private boolean playersOperate(ParamList list, CommandSender sender, IScoreboardManager manager) {
        String wildcard_target_str = list.getResult(2);
        Set<IScorer> targetScorers = new HashSet<>();
        if (wildcard_target_str.equals("*")) {
            for (var scoreboard : manager.getScoreboards().values()) {
                targetScorers.addAll(scoreboard.getLines().keySet());
            }
        } else if (EntitySelector.hasArguments(wildcard_target_str)) {
            targetScorers = EntitySelector.matchEntities(sender, wildcard_target_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
        } else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
            targetScorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
        } else {
            targetScorers.add(new FakeScorer(wildcard_target_str));
        }

        String targetObjectiveName = list.getResult(3);
        if (!manager.containScoreboard(targetObjectiveName)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", targetObjectiveName));
            return false;
        }
        var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

        String operation = list.getResult(4);
        String selector_str = list.getResult(5);
        Set<IScorer> selectorScorers = EntitySelector.matchEntities(sender, selector_str).stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
        if (selectorScorers.size() > 1) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
            return false;
        }
        if (selectorScorers.size() == 0) {
            selectorScorers.add(new FakeScorer(selector_str));
        }
        IScorer seletorScorer = selectorScorers.iterator().next();
        String selectorObjectiveName = list.getResult(6);
        if (!manager.containScoreboard(selectorObjectiveName)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", selectorObjectiveName));
            return false;
        }
        var selectorScoreboard = manager.getScoreboards().get(targetObjectiveName);
        if (!selectorScoreboard.getLines().containsKey(seletorScorer)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", selectorObjectiveName, seletorScorer.getName()));
            return false;
        }
        for (IScorer targetScorer : targetScorers) {
            if (!targetScoreboard.getLines().containsKey(targetScorer)) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", targetObjectiveName, targetScorer.getName()));
                return false;
            }
            int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
            int selectorScore = selectorScoreboard.getLines().get(seletorScorer).getScore();
            switch (operation) {
                case "+=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(targetScore + selectorScore);
                }
                case "-=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(targetScore - selectorScore);
                }
                case "*=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(targetScore * selectorScore);
                }
                case "/=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(targetScore / selectorScore);
                }
                case "%=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(targetScore % selectorScore);
                }
                case "=" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(selectorScore);
                }
                case "<" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(Math.min(targetScore, selectorScore));
                }
                case ">" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(Math.max(targetScore, selectorScore));
                }
                case "><" -> {
                    targetScoreboard.getLines().get(targetScorer).setScore(selectorScore);
                    selectorScoreboard.getLines().get(seletorScorer).setScore(targetScore);
                }
            }
            sender.sendMessage(new TranslationContainer("commands.scoreboard.players.operation.success", targetObjectiveName, String.valueOf(targetScorers.size())));
        }
        return true;
    }
}
