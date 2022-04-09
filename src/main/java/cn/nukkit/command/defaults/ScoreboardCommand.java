package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardManager;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                CommandParameter.newType("targetObjective", CommandParamType.STRING)
        });
        this.commandParameters.put("players-random", new CommandParameter[]{
                CommandParameter.newEnum("players",new String[]{"players"}),
                CommandParameter.newEnum("random",new String[]{"random"}),
                CommandParameter.newType("player",CommandParamType.WILDCARD_TARGET),//allow *
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newType("min", CommandParamType.INT),
                CommandParameter.newType("max", CommandParamType.INT)
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
                CommandParameter.newType("max", CommandParamType.WILDCARD_INT)
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
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
            };
            switch(form){
                case "objectives-add" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();
                    String objectiveName = p.parseString();
                    if (manager.containScoreboard(objectiveName)){
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.alreadyExists", objectiveName));
                        return false;
                    }
                    String criteriaName = p.parseString();
                    String displayName = p.parseString();
                    manager.addScoreboard(new Scoreboard(objectiveName,displayName,criteriaName,SortOrder.ASCENDING, manager.getStorage()));
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.add.success", objectiveName));
                    return true;
                }
                case "objectives-list" -> {
                    sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.list.count", String.valueOf(manager.getScoreboards().size())));
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
                        manager.clearDisplaySlot(slot);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", slot.getSlotName()));
                        return true;
                    }else {
                        String objectiveName = p.parseString();
                        if (!manager.containScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer("commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                        SortOrder order = p.hasNext() ? switch (p.parseString()) {
                            case "ascending" -> SortOrder.ASCENDING;
                            case "descending" -> SortOrder.DESCENDING;
                            default -> SortOrder.ASCENDING;
                        } : SortOrder.ASCENDING;
                        scoreboard.setSortOrder(order);
                        manager.setDisplaySlot(slot, objectiveName);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successSet",slot.getSlotName(),objectiveName));
                        return true;
                    }
                }
                case "objectives-setdisplay-belowname" -> {
                    CommandParser p = new CommandParser(parser);
                    p.parseString();p.parseString();p.parseString();
                    if (!p.hasNext()){
                        manager.clearDisplaySlot(DisplaySlot.BELOW_NAME);
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectives.setdisplay.successCleared", DisplaySlot.BELOW_NAME.getSlotName()));
                        return true;
                    }else {
                        String objectiveName = p.parseString();
                        if (!manager.containScoreboard(objectiveName)) {
                            sender.sendMessage(new TranslationContainer("commands.scoreboard.objectiveNotFound", objectiveName));
                            return false;
                        }
                        manager.setDisplaySlot(DisplaySlot.BELOW_NAME, objectiveName);
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
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
                        p.parseString();
                    }
                    String objectiveName = p.parseString();
                    if (!manager.containScoreboard(objectiveName)) {
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.objectiveNotFound", objectiveName));
                        return false;
                    }
                    Scoreboard scoreboard = manager.getScoreboards().get(objectiveName);
                    if (wildcard)
                        scorers.addAll(scoreboard.getLines().keySet());
                    if (scorers.isEmpty()){
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.empty"));
                        return false;
                    }
                    int score = p.parseInt();
                    int count = scorers.size();
                    switch(ars){
                        case "add" -> {
                            for (Scorer scorer : scorers) {
                                if (!scoreboard.getLines().containsKey(scorer)){
                                    scoreboard.addLine(scorer,score);
                                }
                                scoreboard.getLines().get(scorer).addScore(score);
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
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.empty"));
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
                    }else if (EntitySelector.hasArguments(wildcard_target_str)) {
                        scorers = p.parseTargets().stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                    }else if (Server.getInstance().getPlayer(wildcard_target_str) != null) {
                        scorers.add(new PlayerScorer(Server.getInstance().getPlayer(wildcard_target_str)));
                    } else {
                        scorers.add(new FakeScorer(wildcard_target_str));
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
                            sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.empty",scorer.getName()));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.count",String.valueOf(count),scorer.getName()));
                        for (Scoreboard scoreboard : manager.getScoreboards().values()) {
                            if (scoreboard.getLines().containsKey(scorer)){
                                sender.sendMessage(new TranslationContainer("commands.scoreboard.players.list.player.entry",String.valueOf(scoreboard.getLines().get(scorer).getScore()),scoreboard.getDisplayName(),scoreboard.getObjectiveName()));
                            }
                        }
                    }
                    return true;
                }
                case "players-operation" -> {

                }
                case "players-random" -> {

                }
                case "players-reset" -> {

                }
                case "players-test" -> {

                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
