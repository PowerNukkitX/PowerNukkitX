package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardManager;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.TextFormat;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ExecuteCommand extends VanillaCommand{

    public ExecuteCommand(String name) {
        super(name,"commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, preHandleArgs(args), false);

        return nextSubCommand(sender, parser);
    }

    /**
     * 因为在新的execute命令中我们不能使用命令解析器的模板匹配功能
     * 所以说我们需要预处理参数以将类似于"~~~"的输入分割为"~ ~ ~"
     */
    protected String[] preHandleArgs(String[] args){
        List<String> handled = new LinkedList<>();
        for (int argPointer = 0; argPointer < args.length; argPointer++){
            String arg = args[argPointer];
            //只解析run子命令之前的内容，防止干扰其他命令的解析
            if (arg.equals("run")){
                for (int i = argPointer;i < args.length;i++){
                    handled.add(args[i]);
                }
                break;
            }
            //不需要处理目标选择器
            if (arg.startsWith("@")) {
                handled.add(arg);
                continue;
            }
            char[] chars = arg.toCharArray();
            int pointer = 0;
            int start = 0;
            for (char c : chars){
                if (c == '~' || c == '^'){
                    if (start != pointer)
                        handled.add(arg.substring(start, pointer));
                    start = pointer;
                }
                pointer++;
            }
            //加上剩下的
            handled.add(arg.substring(start));
        }
        return handled.toArray(new String[0]);
    }

    protected boolean nextSubCommand(CommandSender sender,CommandParser parser){
        try {
            String key;
            switch (key = parser.parseString()) {
                case "as" -> {
                    List<Entity> executors = parser.parseTargets();
                    boolean success = true;
                    for (Entity executor : executors) {
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, executor, sender.getLocation());
                        if (!nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender)) && success) {
                            success = false;
                        }
                    }
                    return success;
                }
                case "at" -> {
                    List<Entity> locations = parser.parseTargets();
                    if (locations.isEmpty()) return false;
                    boolean success = true;
                    for (Location location : locations) {
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                        if (!nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender)) && success) {
                            success = false;
                        }
                    }
                    return success;
                }
                case "run" -> {
                    String command = parser.parseAllRemain();
                    if (command.isEmpty()) return false;
                    return sender.getServer().dispatchCommand(sender, command);
                }
                case "positioned" -> {
                    if (parser.parseString(false).equals("as")) {
                        parser.parseString();//skip "as"
                        List<Entity> targets = parser.parseTargets();
                        boolean success = true;
                        for (Vector3 vec : targets) {
                            Location newLoc = sender.getLocation();
                            newLoc.setX(vec.getX());
                            newLoc.setY(vec.getY());
                            newLoc.setZ(vec.getZ());
                            ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                            if (!nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender)) && success) {
                                success = false;
                            }
                        }
                        return success;
                    } else {
                        Vector3 vec = parser.parsePosition();
                        Location newLoc = sender.getLocation();
                        newLoc.setX(vec.getX());
                        newLoc.setY(vec.getY());
                        newLoc.setZ(vec.getZ());
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                        return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
                    }
                }
                case "if", "unless" -> {
                    boolean shouldMatch = key.equals("if");
                    switch(parser.parseString()){
                        case "block" -> {
                            Position pos = parser.parsePosition();
                            String blockName = parser.parseString();
                            int id = BlockState.of(blockName.startsWith("minecraft:") ? blockName : "minecraft:" + blockName).getBlockId();
                            if ((id == pos.getLevelBlock().getId() && shouldMatch) || (id != pos.getLevelBlock().getId() && !shouldMatch)){
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                        case "blocks" -> {

                        }
                        case "entity" -> {
                            boolean found = !parser.parseTargets().isEmpty();
                            if ((found && shouldMatch) || (!found && !shouldMatch)){
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                        case "score" -> {
                            if (!parser.parseString(false).equals("matches")) {
                                ScoreboardManager manager = Server.getInstance().getScoreboardManager();
                                String target_str = parser.parseString(false);

                                Set<Scorer> targetScorers = parser.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                                if (targetScorers.size() > 1) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                                    return false;
                                }
                                if (targetScorers.size() == 0) {
                                    targetScorers.add(new FakeScorer(target_str));
                                }
                                Scorer targetScorer = targetScorers.iterator().next();

                                String targetObjectiveName = parser.parseString();

                                if (!manager.hasScoreboard(targetObjectiveName)) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", targetObjectiveName));
                                    return false;
                                }
                                Scoreboard targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

                                String operation = parser.parseString();

                                String sourceScorer_str = parser.parseString(false);
                                Set<Scorer> selectorScorers = parser.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                                if (selectorScorers.size() > 1) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                                    return false;
                                }
                                if (selectorScorers.size() == 0) {
                                    selectorScorers.add(new FakeScorer(sourceScorer_str));
                                }
                                Scorer sourceScorer = selectorScorers.iterator().next();

                                String sourceObjectiveName = parser.parseString();
                                if (!manager.hasScoreboard(sourceObjectiveName)) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", sourceObjectiveName));
                                    return false;
                                }
                                Scoreboard sourceScoreboard = manager.getScoreboards().get(targetObjectiveName);

                                if (!sourceScoreboard.getLines().containsKey(sourceScorer)) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", sourceObjectiveName, sourceScorer.getName()));
                                    return false;
                                }

                                int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                                int sourceScore = sourceScoreboard.getLines().get(sourceScorer).getScore();

                                boolean matched = switch (operation) {
                                    case "<" -> targetScore < sourceScore;
                                    case "<=" -> targetScore <= sourceScore;
                                    case "=" -> targetScore == sourceScore;
                                    case ">=" -> targetScore >= sourceScore;
                                    case ">" -> targetScore > sourceScore;
                                    default -> false;
                                };

                                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                                    return nextSubCommand(sender, new CommandParser(parser));
                                }
                                return false;
                            }else{

                            }
                        }
                    }
                }
            }
        } catch (CommandSyntaxException e){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
