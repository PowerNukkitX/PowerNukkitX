package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.*;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import com.google.common.base.Splitter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.getLevelBlocks;

@PowerNukkitXOnly
@Since("1.19.20-r2")
public class ExecuteCommand extends VanillaCommand {

    private static final Splitter SCORE_SCOPE_SEPARATOR = Splitter.on("..").limit(2);

    private static final Pattern ERROR_COMMAND_NAME = Pattern.compile("(?<=run\\s).*?(?=\\s|$)");

    public ExecuteCommand(String name) {
        super(name, "commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
        this.getCommandParameters().clear();
        this.addCommandParameters("as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_As", "as")),
                ORIGIN.get(false),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("at", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_At", "at")),
                ORIGIN.get(false),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("in", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_In", "in")),
                CommandParameter.newType("dimension", CommandParamType.STRING),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("facing", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newType("pos", CommandParamType.POSITION),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("facing-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("rotated", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newType("yaw", true, CommandParamType.VALUE),
                CommandParameter.newType("pitch", true, CommandParamType.VALUE),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("rotated as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("align", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Align", "align")),
                CommandParameter.newType("axes", CommandParamType.STRING),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("anchored", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Anchored", "anchored")),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("positioned", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("positioned as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                ORIGIN.get(false),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-block", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                CHAINED_COMMAND.get(false)
        });
        /*todo 暂时没实现，因为我也不知道这个blockStates填什么
        this.addCommandParameters("if-unless-block-blockStates", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("blockStates", CommandParamType.BLOCK_STATES),
                CommandParameter.newEnum("chainedCommand", false, CHAINED_COMMAND_ENUM, new ChainedCommandNode(),CommandParamOption.ENUM_AS_CHAINED_COMMAND)
        });*/
        this.addCommandParameters("if-unless-block-data", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("data", CommandParamType.INT),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-blocks", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Blocks", "blocks")),
                CommandParameter.newType("begin", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("scan mode", true, new String[]{"all", "masked"}),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-score", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CommandParameter.newType("operation", CommandParamType.COMPARE_OPERATOR),
                CommandParameter.newType("source", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-score-matches", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CommandParameter.newEnum("matches", new String[]{"matches"}),
                CommandParameter.newType("range", CommandParamType.STRING),
                CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("run", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Run", "run")),
                CommandParameter.newType("command", false, CommandParamType.COMMAND, CommandParamOption.HAS_SEMANTIC_CONSTRAINT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        int num = 0;
        var list = result.getValue();
        switch (result.getKey()) {
            case "run" -> {
                String command = list.getResult(1);
                if (command.isBlank()) return 0;
                return sender.getServer().executeCommand(sender, command);
            }
            case "as" -> {
                List<Entity> executors = list.getResult(1);
                String chainCommand = list.getResult(2);
                for (Entity executor : executors) {
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, executor, sender.getLocation());
                    int n = executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                    if (n == 0) {
                        var names = new ArrayList<String>();
                        Matcher match = ERROR_COMMAND_NAME.matcher(chainCommand);
                        while (match.find()) {
                            names.add(match.group());
                        }
                        Collections.reverse(names);
                        for (var name : names) {
                            log.addError("commands.execute.failed", name, executor.getName());
                        }
                    } else num += n;
                }
                log.output();
                return num;
            }
            case "at" -> {
                List<Entity> locations = list.getResult(1);
                String chainCommand = list.getResult(2);
                for (Location location : locations) {
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "in" -> {
                String levelName = list.getResult(1);
                Level level = Server.getInstance().getLevelByName(levelName);
                if (level == null) {
                    return 0;
                }
                String chainCommand = list.getResult(2);
                Location location = sender.getLocation();
                location.setLevel(level);
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "facing" -> {
                Vector3 pos = list.getResult(1);
                String chainCommand = list.getResult(2);
                Location source = sender.getLocation();
                BVector3 bv = BVector3.fromPos(pos.x - source.x, pos.y - source.y, pos.z - source.z);
                source.setPitch(bv.getPitch());
                source.setYaw(bv.getYaw());
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), source);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "facing-entity" -> {
                List<Entity> targets = list.getResult(2);
                String anchor = list.getResult(3);
                boolean anchorAtEyes = anchor.equals("eyes");
                String chainCommand = list.getResult(4);
                for (Entity target : targets) {
                    Location source = sender.getLocation();
                    BVector3 bv = BVector3.fromPos(target.x - source.x, target.y + (anchorAtEyes ? target.getEyeHeight() : 0) - source.y, target.z - source.z);
                    source.setPitch(bv.getPitch());
                    source.setYaw(bv.getYaw());
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), source);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "rotated" -> {
                double yaw = sender.getLocation().yaw;
                if (list.hasResult(1)) yaw = list.getResult(1);
                double pitch = sender.getLocation().pitch;
                if (list.hasResult(2)) pitch = list.getResult(2);
                String chainCommand = list.getResult(3);
                Location location = sender.getLocation();
                location.setYaw(yaw);
                location.setPitch(pitch);
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "rotated as" -> {
                List<Entity> executors = list.getResult(2);
                String chainCommand = list.getResult(3);
                for (Entity executor : executors) {
                    Location location = sender.getLocation();
                    location.setYaw(executor.getYaw());
                    location.setPitch(executor.getPitch());
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "align" -> {
                String axes = list.getResult(1);
                String chainCommand = list.getResult(2);
                Location location = sender.getLocation();
                for (char c : axes.toCharArray()) {
                    switch (c) {
                        case 'x' -> location.x = location.getFloorX();
                        case 'y' -> location.y = location.getFloorY();
                        case 'z' -> location.z = location.getFloorZ();
                    }
                }
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "anchored" -> {
                if (!sender.isEntity()) return 0;
                Location location = sender.getLocation();
                String anchor = list.getResult(1);
                String chainCommand = list.getResult(2);
                switch (anchor) {
                    case "feet" -> {
                        //todo do nothing
                    }
                    case "eyes" -> location = location.add(0, sender.asEntity().getEyeHeight(), 0);
                }
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "positioned" -> {
                Vector3 vec = list.getResult(1);
                Location newLoc = sender.getLocation();
                newLoc.setX(vec.getX());
                newLoc.setY(vec.getY());
                newLoc.setZ(vec.getZ());
                String chainCommand = list.getResult(2);
                ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "positioned as" -> {
                List<Entity> targets = list.getResult(2);
                String chainCommand = list.getResult(3);
                for (Vector3 vec : targets) {
                    Location newLoc = sender.getLocation();
                    newLoc.setX(vec.getX());
                    newLoc.setY(vec.getY());
                    newLoc.setZ(vec.getZ());
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "if-unless-block" -> {
                Position pos = list.getResult(2);
                Block block = pos.getLevelBlock();
                Block blockName = list.getResult(3);
                int id = blockName.getId();
                String chainCommand = list.getResult(4);
                String isIF = list.getResult(0);

                boolean matched = block.getId() == id;
                boolean shouldMatch = isIF.equals("if");
                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            case "if-unless-block-data" -> {
                Position pos = list.getResult(2);
                Block block = pos.getLevelBlock();
                Block blockName = list.getResult(3);
                int id = blockName.getId();
                int data = list.getResult(4);
                String chainCommand = list.getResult(5);
                String isIF = list.getResult(0);

                boolean matched = id == block.getId() && (data == -1 || data == block.getDamage());
                boolean shouldMatch = isIF.equals("if");
                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            case "if-unless-blocks" -> {
                String isIF = list.getResult(0);
                boolean shouldMatch = isIF.equals("if");
                Position begin = list.getResult(2);
                Position end = list.getResult(3);
                Position destination = list.getResult(4);
                TestForBlocksCommand.TestForBlocksMode mode = TestForBlocksCommand.TestForBlocksMode.ALL;
                if (list.hasResult(5)) {
                    String str5 = list.getResult(5);
                    mode = TestForBlocksCommand.TestForBlocksMode.valueOf(str5.toUpperCase(Locale.ENGLISH));
                }
                String chainCommand = list.getResult(6);

                AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
                int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

                if (size > 16 * 16 * 256 * 8) {
                    log.addError("commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8))
                            .addError("Operation will continue, but too many blocks may cause stuttering")
                            .successCount(2).output();
                }

                Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
                AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

                if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
                    log.addError("commands.testforblock.outOfWorld").output();
                    if (!shouldMatch) {
                        return sender.getServer().executeCommand(sender, chainCommand);
                    }
                    return 0;
                }

                Level level = begin.getLevel();

                for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                    for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                        if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                            log.addError("commands.testforblock.outOfWorld").output();
                            if (!shouldMatch) {
                                return sender.getServer().executeCommand(sender, chainCommand);
                            }
                            return 0;
                        }
                        if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                            log.addError("commands.testforblock.outOfWorld").output();
                            if (!shouldMatch) {
                                return sender.getServer().executeCommand(sender, chainCommand);
                            }
                            return 0;
                        }
                    }
                }

                Block[] blocks = getLevelBlocks(level, blocksAABB);
                Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
                int count = 0;

                boolean matched = true;

                switch (mode) {
                    case ALL:
                        for (int i = 0; i < blocks.length; i++) {
                            Block block = blocks[i];
                            Block destinationBlock = destinationBlocks[i];

                            if (block.equalsBlock(destinationBlock)) {
                                ++count;
                            } else {
                                log.addError("commands.compare.failed").output();
                                matched = false;
                                break;
                            }
                        }

                        break;
                    case MASKED:
                        for (int i = 0; i < blocks.length; i++) {
                            Block block = blocks[i];
                            Block destinationBlock = destinationBlocks[i];

                            if (block.equalsBlock(destinationBlock)) {
                                ++count;
                            } else if (block.getId() != Block.AIR) {
                                log.addError("commands.compare.failed").output();
                                matched = false;
                                break;
                            }
                        }

                        break;
                }

                log.addSuccess("commands.compare.success", String.valueOf(count)).output();

                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            case "if-unless-entity" -> {
                String isIF = list.getResult(0);
                boolean shouldMatch = isIF.equals("if");
                List<Entity> targets = list.getResult(2);
                String chainCommand = list.getResult(3);
                boolean found = !targets.isEmpty();
                if ((found && shouldMatch) || (!found && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            case "if-unless-score" -> {
                boolean matched = false;
                String isIF = list.getResult(0);
                boolean shouldMatch = isIF.equals("if");
                var manager = Server.getInstance().getScoreboardManager();

                List<Entity> targets = list.getResult(2);
                Set<IScorer> targetScorers = targets.stream().filter(Objects::nonNull).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (targetScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                IScorer targetScorer = targetScorers.iterator().next();

                String targetObjectiveName = list.getResult(3);
                if (!manager.containScoreboard(targetObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", targetObjectiveName).output();
                    return 0;
                }
                var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

                String operation = list.getResult(4);
                List<Entity> scorers = list.getResult(5);
                Set<IScorer> selectorScorers = scorers.stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (selectorScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                IScorer sourceScorer = selectorScorers.iterator().next();

                String sourceObjectiveName = list.getResult(6);
                if (!manager.containScoreboard(sourceObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", sourceObjectiveName).output();
                    return 0;
                }
                var sourceScoreboard = manager.getScoreboards().get(targetObjectiveName);

                if (!sourceScoreboard.getLines().containsKey(sourceScorer)) {
                    log.addError("commands.scoreboard.players.operation.notFound", sourceObjectiveName, sourceScorer.getName()).output();
                    return 0;
                }

                int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                int sourceScore = sourceScoreboard.getLines().get(sourceScorer).getScore();

                matched = switch (operation) {
                    case "<" -> targetScore < sourceScore;
                    case "<=" -> targetScore <= sourceScore;
                    case "=" -> targetScore == sourceScore;
                    case ">=" -> targetScore >= sourceScore;
                    case ">" -> targetScore > sourceScore;
                    default -> false;
                };

                String chainCommand = list.getResult(7);
                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            case "if-unless-score-matches" -> {
                boolean matched = false;
                String isIF = list.getResult(0);
                boolean shouldMatch = isIF.equals("if");
                var manager = Server.getInstance().getScoreboardManager();

                List<Entity> targets = list.getResult(2);
                Set<IScorer> targetScorers = targets.stream().filter(Objects::nonNull).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (targetScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                IScorer targetScorer = targetScorers.iterator().next();

                String targetObjectiveName = list.getResult(3);
                if (!manager.containScoreboard(targetObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", targetObjectiveName).output();
                    return 0;
                }
                var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);

                int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                String range = list.getResult(5);
                if (range.contains("..")) {
                    int min = Integer.MIN_VALUE;
                    int max = Integer.MAX_VALUE;
                    Iterator<String> score_scope_split = SCORE_SCOPE_SEPARATOR.split(range).iterator();
                    String min_str = score_scope_split.next();
                    if (!min_str.isEmpty()) {
                        min = Integer.parseInt(min_str);
                    }
                    String max_str = score_scope_split.next();
                    if (!max_str.isEmpty()) {
                        max = Integer.parseInt(max_str);
                    }
                    matched = targetScore >= min && targetScore <= max;
                } else {
                    int score = Integer.parseInt(range);
                    matched = targetScore == score;
                }
                String chainCommand = list.getResult(6);
                if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                    return sender.getServer().executeCommand(sender, chainCommand);
                }
                return 0;
            }
            default -> {
                return 0;
            }
        }
    }
}
