package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardManager;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.TextFormat;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardCommand extends VanillaCommand {

    public ScoreboardCommand(String name) {
        super(name,"commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        this.commandParameters.put("objectives-add", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("add",new String[]{"add"}),
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newEnum("criteria",new String[]{"dummy"}),
                CommandParameter.newType("displayname", CommandParamType.STRING)
        });
        this.commandParameters.put("objectives-list", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("list",new String[]{"list"}),
        });
        this.commandParameters.put("objectives-remove", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("remove",new String[]{"remove"}),
                CommandParameter.newType("objective", CommandParamType.STRING)
        });
        this.commandParameters.put("objectives-setdisplay-list-sidebar", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("setdisplay",new String[]{"setdisplay"}),
                CommandParameter.newEnum("slot",new String[]{"list","sidebar"}),
                CommandParameter.newType("objective",true, CommandParamType.STRING),
                CommandParameter.newEnum("order",true,new String[]{"ascending","descending"})
        });
        this.commandParameters.put("objectives-setdisplay-belowname", new CommandParameter[]{
                CommandParameter.newEnum("objectives",new String[]{"objectives"}),
                CommandParameter.newEnum("setdisplay",new String[]{"setdisplay"}),
                CommandParameter.newEnum("slot",new String[]{"belowname"}),
                CommandParameter.newType("objective",true, CommandParamType.STRING)
        });
        this.commandParameters.put("players-add-remove-set", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("ars",new String[]{"add","remove","set"}),
                CommandParameter.newType("player", CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("score", CommandParamType.INT)
        });
        this.commandParameters.put("players-list", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("list",new String[]{"list"}),
                CommandParameter.newType("playername",CommandParamType.WILDCARD_TARGET)//allow *
        });
        this.commandParameters.put("players-operation", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("op",new String[]{"operation"}),
                CommandParameter.newType("targetName",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("targetObjective", CommandParamType.STRING),
                CommandParameter.newEnum("operation",new String[]{"+=","-=","*=","/=","%=","=","<",">","><"}),
                CommandParameter.newType("selector",CommandParamType.TARGET),
                CommandParameter.newType("selectorObjective", CommandParamType.STRING)
        });
        this.commandParameters.put("players-random", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("random",new String[]{"random"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("min", CommandParamType.WILDCARD_INT),
                CommandParameter.newType("max", true,CommandParamType.WILDCARD_INT)
        });
        this.commandParameters.put("players-reset", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("reset",new String[]{"reset"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING)
        });
        this.commandParameters.put("players-test", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("test",new String[]{"test"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("min", CommandParamType.WILDCARD_INT),
                CommandParameter.newType("max", true,CommandParamType.WILDCARD_INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        ScoreboardManager manager = Server.getInstance().getScoreboardManager();
        CommandParser parser = new CommandParser(this,sender,args);
        try{
            String form = parser.matchCommandForm();
            if (form == null){
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            };
            switch(form){
                case "objectives-add" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String objectiveName = p.parseString();
                    if (manager.hasScoreboard(objectiveName)){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectives.add.alreadyExists", objectiveName));
                        return false;
                    }
                    String criteriaName = p.parseString();
                    String displayName = p.parseString();
                    manager.addScoreboard(new Scoreboard(objectiveName,displayName,criteriaName,SortOrder.ASCENDING, manager));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.success", objectiveName));
                    return true;
                }
                case "objectives-list" -> {
                    sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.objectives.list.count", String.valueOf(manager.getScoreboards().size())));
                    for (Scoreboard scoreboard : manager.getScoreboards().values()){
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.list.entry", scoreboard.getObjectiveName(),scoreboard.getDisplayName(),scoreboard.getCriteriaName()));
                    }
                    return true;
                }
                case "objectives-remove" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String objectiveName = p.parseString();
                    manager.removeScoreboard(objectiveName);
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.remove.success", objectiveName));
                    return true;
                }
                case "objectives-setdisplay-list-sidebar" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    DisplaySlot slot = switch(p.parseString()){
                        case "sidebar" -> DisplaySlot.SIDEBAR;
                        case "list" -> DisplaySlot.LIST;
                        default -> DisplaySlot.SIDEBAR;
                    };
                    if (!p.hasNext()){
                        manager.removeDisplay(slot);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", slot.getSlotName()));
                        return true;
                    }else {
                        String objectiveName = p.parseString();
                        if (!manager.hasScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                        SortOrder order = p.hasNext() ? switch (p.parseString()) {
                            case "ascending" -> SortOrder.ASCENDING;
                            case "descending" -> SortOrder.DESCENDING;
                            default -> SortOrder.ASCENDING;
                        } : SortOrder.ASCENDING;
                        scoreboard.setSortOrder(order);
                        manager.setDisplay(slot, objectiveName);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet",slot.getSlotName(),objectiveName));
                        return true;
                    }
                }
                case "objectives-setdisplay-belowname" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();p.parseString();
                    if (!p.hasNext()){
                        manager.removeDisplay(DisplaySlot.BELOW_NAME);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", DisplaySlot.BELOW_NAME.getSlotName()));
                        return true;
                    }else {
                        String objectiveName = p.parseString();
                        if (!manager.hasScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        manager.setDisplay(DisplaySlot.BELOW_NAME, objectiveName);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet", DisplaySlot.BELOW_NAME.getSlotName(),objectiveName));
                        return true;
                    }
                }
                case "players-add-remove-set" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();
                    String ars = p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    List<Scorer> scorers = new ArrayList<>();
                    boolean wildcard = false;
                    if (wildcard_target_str.equals("*")) {
                        wildcard = true;
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toList());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.hasScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                    if (wildcard)
                        scorers.addAll(scoreboard.getLines().keySet());
                    if (scorers.isEmpty()){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
                        return false;
                    }
                    int score = p.parseInt();
                    int count = scorers.size();
                    switch(ars){
                        case "add" -> {
                            for (Scorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)){
                                    scoreboard.addLine(scorer,score);
                                }else {
                                    scoreboard.getLines().get(scorer).addScore(score);
                                }
                            }
                            if (count == 1){
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.success",String.valueOf(score),objectiveName,scorers.get(0).getName(),String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                            }else{
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.add.multiple.success",String.valueOf(score),objectiveName,String.valueOf(count)));
                            }
                            return true;
                        }
                        case "remove" -> {
                            for (Scorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)){
                                    scoreboard.addLine(scorer,-score);
                                }
                                scoreboard.getLines().get(scorer).removeScore(score);
                            }
                            if (count == 1){
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.success",String.valueOf(score),objectiveName,scorers.get(0).getName(),String.valueOf(scoreboard.getLines().get(scorers.get(0)).getScore())));
                            }else{
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.remove.multiple.success",String.valueOf(score),objectiveName,String.valueOf(count)));
                            }
                            return true;
                        }
                        case "set" -> {
                            for (Scorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)){
                                    scoreboard.addLine(scorer,score);
                                }
                                scoreboard.getLines().get(scorer).setScore(score);
                            }
                            if (count == 1){
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success",objectiveName,scorers.get(0).getName(),String.valueOf(score)));
                            }else{
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.multiple.success",objectiveName,String.valueOf(count),String.valueOf(score)));
                            }
                            return true;
                        }
                    }
                    return false;
                }
                case "players-list" -> {
                    if (manager.getScoreboards().isEmpty()){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.empty"));
                        return false;
                    }
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<Scorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }

                    for (Scorer scorer : scorers) {
                        boolean find = false;
                        int count = 0;
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            if (scoreboard.getLines().containsKey(scorer)){
                                find = true;
                                count++;
                            }
                        }
                        if (!find){
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.list.player.empty",scorer.getName()));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.scoreboard.players.list.player.count",String.valueOf(count),scorer.getName()));
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            if (scoreboard.getLines().containsKey(scorer)){
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.entry",String.valueOf(scoreboard.getLines().get(scorer).getScore()),scoreboard.getDisplayName(),scoreboard.getObjectiveName()));
                            }
                        }
                    }
                    return true;
                }
                case "players-operation" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String wildcard_target_str = p.parseString(false);

                    Set<Scorer> targetScorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            targetScorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        targetScorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        targetScorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        targetScorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }

                    String targetObjectiveName = p.parseString();
                    if (!manager.hasScoreboard(targetObjectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", targetObjectiveName));
                        return false;
                    }
                    Scoreboard targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

                    String operation = p.parseString();

                    String selector_str = p.parseString(false);
                    Set<Scorer> selectorScorers = p.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    if (selectorScorers.size() > 1){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                        return false;
                    }
                    if (selectorScorers.size() == 0){
                        selectorScorers.add(new FakeScorer(selector_str));
                    }
                    Scorer seletorScorer = selectorScorers.iterator().next();

                    String selectorObjectiveName = p.parseString();
                    if (!manager.hasScoreboard(selectorObjectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", selectorObjectiveName));
                        return false;
                    }
                    Scoreboard selectorScoreboard = manager.getScoreboards().get(targetObjectiveName);

                    if(!selectorScoreboard.getLines().containsKey(seletorScorer)){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound",selectorObjectiveName,seletorScorer.getName()));
                        return false;
                    }

                    for (Scorer targetScorer : targetScorers) {
                        if (!targetScoreboard.getLines().containsKey(targetScorer)){
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound",targetObjectiveName,targetScorer.getName()));
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
                    p.parseString();p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<Scorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.hasScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
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
                    for (Scorer scorer : scorers) {
                        int score = (int) (min + random.nextLong(max - min + 1));//avoid "java.lang.IllegalArgumentException: bound must be positive"
                        if (!scoreboard.getLines().containsKey(scorer)){
                            scoreboard.addLine(scorer,score);
                        }
                        scoreboard.getLines().get(scorer).setScore(score);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.set.success",objectiveName,scorer.getName(),String.valueOf(score)));
                    }
                    return true;
                }
                case "players-reset" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<Scorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    if (p.hasNext()) {
                        String objectiveName = p.parseString();
                        if (!manager.hasScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                        for (Scorer scorer : scorers) {
                            if(scoreboard.containLine(scorer)) {
                                scoreboard.removeLine(scorer);
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.resetscore.success", scoreboard.getObjectiveName(), scorer.getName()));
                            }
                        }
                        return true;
                    }else{
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            for (Scorer scorer : scorers) {
                                if(scoreboard.containLine(scorer)) {
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
                    p.parseString();p.parseString();
                    String wildcard_target_str = p.parseString(false);
                    Set<Scorer> scorers = new HashSet<>();
                    if (wildcard_target_str.equals("*")) {
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            scorers.addAll(scoreboard.getLines().keySet());
                        }
                        p.parseString();
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                        p.parseString();
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.hasScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                    int min = p.parseWildcardInt(Integer.MIN_VALUE);
                    int max = Integer.MAX_VALUE;
                    if (p.hasNext()) {
                        max = p.parseWildcardInt(Integer.MAX_VALUE);
                    }
                    for (Scorer scorer : scorers) {
                        Scoreboard.ScoreboardLine line = scoreboard.getLine(scorer);
                        if (line == null) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.score.notFound",objectiveName,scorer.getName()));
                            return false;
                        }
                        int score = line.getScore();
                        if (score < min || score > max) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.test.failed",String.valueOf(score),String.valueOf(min),String.valueOf(max)));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.test.success",String.valueOf(score),String.valueOf(min),String.valueOf(max)));
                    }
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
        }
        return false;
    }
}
