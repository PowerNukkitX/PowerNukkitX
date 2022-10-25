package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.*;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.getLevelBlocks;

@PowerNukkitXOnly
@Since("1.19.20-r2")
public class ExecuteCommand extends VanillaCommand {

    private static final Splitter SCORE_SCOPE_SEPARATOR = Splitter.on("..").limit(2);
    private static final CommandEnum CHAINED_COMMAND_ENUM = new CommandEnum("ExecuteChainedOption_0", "run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");
    private static final CommandParameter CHAINED_COMMAND_PARAM = CommandParameter.newEnum("chainedCommand", false, CHAINED_COMMAND_ENUM);
    private static final CommandParameter COMMAND_PARAM = CommandParameter.newType("command", CommandParamType.COMMAND);

    static {
        CHAINED_COMMAND_PARAM.paramOptions = List.of(CommandParamOption.ENUM_AS_CHAINED_COMMAND);
        COMMAND_PARAM.paramOptions = List.of(CommandParamOption.HAS_SEMANTIC_CONSTRAINT);
    }

    public ExecuteCommand(String name) {
        super(name, "commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
        this.getCommandParameters().clear();
        this.addCommandParameters("as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_As", "as")),
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("at", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_At", "at")),
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("in", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_In", "in")),
                CommandParameter.newType("dimension", CommandParamType.STRING),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("facing", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newType("pos", CommandParamType.POSITION),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("facing-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("rotated", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newType("yaw", true, CommandParamType.VALUE),
                CommandParameter.newType("pitch", true, CommandParamType.VALUE),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("rotated as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("align", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Align", "align")),
                CommandParameter.newType("axes", CommandParamType.STRING),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("anchored", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Anchored", "anchored")),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("positioned", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("positioned as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-block", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-block-data", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("data", CommandParamType.INT),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-blocks", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Blocks", "blocks")),
                CommandParameter.newType("begin", CommandParamType.POSITION),
                CommandParameter.newType("end", CommandParamType.POSITION),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("scan mode", true, new String[]{"all", "masked"}),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-score", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newEnum("operation", new String[]{"<", "<=", "=", ">=", ">"}),
                CommandParameter.newType("source", CommandParamType.TARGET),
                CommandParameter.newType("objective", CommandParamType.STRING),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("if-unless-score-matches", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newType("objective", CommandParamType.STRING),
                CommandParameter.newEnum("matches", new String[]{"matches"}),
                CommandParameter.newType("range", CommandParamType.STRING),
                CHAINED_COMMAND_PARAM
        });
        this.addCommandParameters("run", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Run", "run")),
                COMMAND_PARAM
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
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
    protected String[] preHandleArgs(String[] args) {
        List<String> handled = new LinkedList<>();
        for (int argPointer = 0; argPointer < args.length; argPointer++) {
            String arg = args[argPointer];
            //只解析run子命令之前的内容，防止干扰其他命令的解析
            if (arg.equals("run")) {
                for (int i = argPointer; i < args.length; i++) {
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
            for (char c : chars) {
                if (c == '~' || c == '^') {
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

    protected boolean nextSubCommand(CommandSender sender, CommandParser parser) {
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
                case "facing" -> {
                    if (parser.parseString(false).equals("entity")) {
                        parser.parseString();
                        List<Entity> targets = parser.parseTargets();
                        if (targets.isEmpty()) return false;
                        boolean anchorAtEyes = parser.parseString().equals("eyes");
                        boolean success = true;
                        for (Entity target : targets) {
                            Location source = sender.getLocation();
                            BVector3 bv = BVector3.fromPos(target.x - source.x, target.y + (anchorAtEyes ? target.getEyeHeight() : 0) - source.y, target.z - source.z);
                            source.setPitch(bv.getPitch());
                            source.setYaw(bv.getYaw());
                            ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), source);
                            if (!nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender)) && success) {
                                success = false;
                            }
                        }
                        return success;
                    } else {
                        Vector3 pos = parser.parseVector3();
                        Location source = sender.getLocation();
                        BVector3 bv = BVector3.fromPos(pos.x - source.x, pos.y - source.y, pos.z - source.z);
                        source.setPitch(bv.getPitch());
                        source.setYaw(bv.getYaw());
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), source);
                        return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
                    }
                }
                case "rotated" -> {
                    if (parser.parseString(false).equals("as")) {
                        parser.parseString();
                        List<Entity> executors = parser.parseTargets();
                        boolean success = true;
                        for (Entity executor : executors) {
                            Location location = sender.getLocation();
                            location.setYaw(executor.getYaw());
                            location.setPitch(executor.getPitch());
                            ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                            if (!nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender)) && success) {
                                success = false;
                            }
                        }
                        return success;
                    } else {
                        double yaw = sender.getLocation().yaw;
                        yaw = parser.parseOffsetDouble(yaw);
                        double pitch = sender.getLocation().pitch;
                        pitch = parser.parseOffsetDouble(pitch);
                        Location location = sender.getLocation();
                        location.setYaw(yaw);
                        location.setPitch(pitch);
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                        return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
                    }
                }
                case "in" -> {
                    String levelName = parser.parseString();
                    Level level = Server.getInstance().getLevelByName(levelName);
                    if (level == null) {
                        return false;
                    }
                    Location location = sender.getLocation();
                    location.setLevel(level);
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
                }
                case "align" -> {
                    String axes = parser.parseString();
                    Location location = sender.getLocation();
                    for (char c : axes.toCharArray()) {
                        switch (c) {
                            case 'x' -> location.x = location.getFloorX();
                            case 'y' -> location.y = location.getFloorY();
                            case 'z' -> location.z = location.getFloorZ();
                        }
                    }
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
                }
                case "anchored" -> {
                    if (!sender.isEntity()) return false;
                    Location location = sender.getLocation();
                    switch (parser.parseString()) {
                        case "feet" -> {
                            //do nothing
                        }
                        case "eyes" -> location = location.add(0, sender.asEntity().getEyeHeight(), 0);
                    }
                    ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    return nextSubCommand(executorCommandSender, new CommandParser(parser, executorCommandSender));
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
                    switch (parser.parseString()) {
                        case "block" -> {
                            Position pos = parser.parsePosition();
                            Block block = pos.getLevelBlock();
                            String blockName = parser.parseString();
                            int id = BlockState.of(blockName.startsWith("minecraft:") ? blockName : "minecraft:" + blockName).getBlockId();
                            String next = parser.parseString(false);
                            int data = -1;
                            try {
                                data = Integer.parseInt(next);
                                parser.parseString();
                            } catch (NumberFormatException e) {
                                //failed
                            }
                            boolean matched = id == block.getId() && (data == -1 || data == block.getDamage());
                            if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                        case "blocks" -> {
                            Position begin = parser.parsePosition().floor();
                            Position end = parser.parsePosition().floor();
                            Position destination = parser.parsePosition().floor();
                            TestForBlocksCommand.TestForBlocksMode mode = TestForBlocksCommand.TestForBlocksMode.ALL;

                            if (parser.hasNext()) {
                                mode = parser.parseEnum(TestForBlocksCommand.TestForBlocksMode.class);
                            }

                            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
                            int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

                            if (size > 16 * 16 * 256 * 8) {
                                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8)));
                                sender.sendMessage(TextFormat.RED + "Operation will continue, but too many blocks may cause stuttering");
                            }

                            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
                            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

                            if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
                                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                                if (!shouldMatch) {
                                    return nextSubCommand(sender, new CommandParser(parser));
                                }
                                return false;
                            }

                            Level level = begin.getLevel();

                            for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                                for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                                        if (!shouldMatch) {
                                            return nextSubCommand(sender, new CommandParser(parser));
                                        }
                                        return false;
                                    }
                                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                                        if (!shouldMatch) {
                                            return nextSubCommand(sender, new CommandParser(parser));
                                        }
                                        return false;
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
                                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.compare.failed"));
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
                                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.compare.failed"));
                                            matched = false;
                                            break;
                                        }
                                    }

                                    break;
                            }

                            sender.sendMessage(new TranslationContainer("%commands.compare.success", String.valueOf(count)));

                            if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                        case "entity" -> {
                            boolean found = !parser.parseTargets().isEmpty();
                            if ((found && shouldMatch) || (!found && !shouldMatch)) {
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                        case "score" -> {
                            boolean matched = false;
                            var manager = Server.getInstance().getScoreboardManager();
                            String target_str = parser.parseString(false);

                            Set<IScorer> targetScorers = parser.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                            if (targetScorers.size() > 1) {
                                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                                return false;
                            }
                            if (targetScorers.size() == 0) {
                                targetScorers.add(new FakeScorer(target_str));
                            }
                            IScorer targetScorer = targetScorers.iterator().next();

                            String targetObjectiveName = parser.parseString();

                            if (!manager.containScoreboard(targetObjectiveName)) {
                                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", targetObjectiveName));
                                return false;
                            }
                            var targetScoreboard = manager.getScoreboards().get(targetObjectiveName);
                            if (!parser.parseString(false).equals("matches")) {

                                String operation = parser.parseString();

                                String sourceScorer_str = parser.parseString(false);
                                Set<IScorer> selectorScorers = parser.parseTargets().stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                                if (selectorScorers.size() > 1) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.tooManyTargets"));
                                    return false;
                                }
                                if (selectorScorers.size() == 0) {
                                    selectorScorers.add(new FakeScorer(sourceScorer_str));
                                }
                                IScorer sourceScorer = selectorScorers.iterator().next();

                                String sourceObjectiveName = parser.parseString();
                                if (!manager.containScoreboard(sourceObjectiveName)) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.objectiveNotFound", sourceObjectiveName));
                                    return false;
                                }
                                var sourceScoreboard = manager.getScoreboards().get(targetObjectiveName);

                                if (!sourceScoreboard.getLines().containsKey(sourceScorer)) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.scoreboard.players.operation.notFound", sourceObjectiveName, sourceScorer.getName()));
                                    return false;
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
                            } else {
                                parser.parseString();//skip "matches"
                                int targetScore = targetScoreboard.getLines().get(targetScorer).getScore();
                                String range = parser.parseString();
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
                            }
                            if ((matched && shouldMatch) || (!matched && !shouldMatch)) {
                                return nextSubCommand(sender, new CommandParser(parser));
                            }
                            return false;
                        }
                    }
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
