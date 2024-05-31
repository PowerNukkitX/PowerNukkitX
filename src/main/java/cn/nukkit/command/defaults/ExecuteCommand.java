package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.data.GenericParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.*;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.getLevelBlocks;


public class ExecuteCommand extends VanillaCommand {

    protected static final String $1 = "..";

    private static final Pattern $2 = Pattern.compile("(?<=run\\s).*?(?=\\s|$)");
    /**
     * @deprecated 
     */
    

    public ExecuteCommand(String name) {
        super(name, "commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
        this.getCommandParameters().clear();
        this.addCommandParameters("as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_As", "as")),
                GenericParameter.ORIGIN.get(false),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("at", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_At", "at")),
                GenericParameter.ORIGIN.get(false),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("in", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_In", "in")),
                CommandParameter.newType("dimension", CommandParamType.STRING),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("facing", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newType("pos", CommandParamType.POSITION),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("facing-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Facing", "facing")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("rotated", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newType("yaw", false, CommandParamType.VALUE),
                CommandParameter.newType("pitch", false, CommandParamType.VALUE),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("rotated as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Rotated", "rotated")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                CommandParameter.newType("targets", CommandParamType.TARGET),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("align", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Align", "align")),
                CommandParameter.newType("axes", CommandParamType.STRING),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("anchored", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Anchored", "anchored")),
                CommandParameter.newEnum("anchor", new String[]{"eyes", "feet"}),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("positioned", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("positioned as", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Positioned", "positioned")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_As", "as")),
                GenericParameter.ORIGIN.get(false),
                GenericParameter.CHAINED_COMMAND.get(false)
        });
        this.addCommandParameters("if-unless-block", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Block", "block")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", false, CommandEnum.ENUM_BLOCK),
                GenericParameter.CHAINED_COMMAND.get(true)
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
                GenericParameter.CHAINED_COMMAND.get(true)
        });
        this.addCommandParameters("if-unless-blocks", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Blocks", "blocks")),
                CommandParameter.newType("begin", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("scan mode", true, new String[]{"all", "masked"}),
                GenericParameter.CHAINED_COMMAND.get(true)
        });
        this.addCommandParameters("if-unless-entity", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Entity", "entity")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                GenericParameter.CHAINED_COMMAND.get(true)
        });
        this.addCommandParameters("if-unless-score", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CommandParameter.newType("operation", CommandParamType.COMPARE_OPERATOR),
                CommandParameter.newType("source", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                GenericParameter.CHAINED_COMMAND.get(true)
        });
        this.addCommandParameters("if-unless-score-matches", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_If_Unless", "if", "unless")),
                CommandParameter.newEnum("secondary subcommand", false, new CommandEnum("Option_Score", "score")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("objective", false, new CommandEnum("ScoreboardObjectives", List.of(), true)),
                CommandParameter.newEnum("matches", new String[]{"matches"}),
                CommandParameter.newType("range", CommandParamType.STRING),
                GenericParameter.CHAINED_COMMAND.get(true)
        });
        this.addCommandParameters("run", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("Option_Run", "run")),
                CommandParameter.newType("command", false, CommandParamType.COMMAND, CommandParamOption.HAS_SEMANTIC_CONSTRAINT)
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        int $3 = 0;
        var $4 = result.getValue();
        switch (result.getKey()) {
            case "run" -> {
                String $5 = list.getResult(1);
                if (command.isBlank()) return 0;
                return sender.getServer().executeCommand(sender, command);
            }
            case "as" -> {
                List<Entity> executors = list.getResult(1);
                if (executors.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                String $6 = list.getResult(2);
                for (Entity executor : executors) {
                    ExecutorCommandSender $7 = new ExecutorCommandSender(sender, executor, executor.getLocation());
                    i$8t $1 = executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                    if (n == 0) {
                        var $9 = new ArrayList<String>();
                        Matcher $10 = ERROR_COMMAND_NAME.matcher(chainCommand);
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
                if (locations.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                String $11 = list.getResult(2);
                for (Location location : locations) {
                    ExecutorCommandSender $12 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "in" -> {
                String $13 = list.getResult(1);
                Level $14 = Server.getInstance().getLevelByName(levelName);
                if (level == null) {
                    return 0;
                }
                String $15 = list.getResult(2);
                Location $16 = sender.getLocation();
                location.setLevel(level);
                ExecutorCommandSender $17 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "facing" -> {
                Vector3 $18 = list.getResult(1);
                String $19 = list.getResult(2);
                Location $20 = sender.getLocation();
                BVector3 $21 = BVector3.fromPos(pos.x - source.x, pos.y - source.y, pos.z - source.z);
                source.setPitch(bv.getPitch());
                source.setYaw(bv.getYaw());
                ExecutorCommandSender $22 = new ExecutorCommandSender(sender, sender.asEntity(), source);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "facing-entity" -> {
                List<Entity> targets = list.getResult(2);
                if (targets.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                String $23 = list.getResult(3);
                boolean $24 = anchor.equals("eyes");
                String $25 = list.getResult(4);
                for (Entity target : targets) {
                    Location $26 = sender.getLocation();
                    BVector3 $27 = BVector3.fromPos(target.x - source.x, target.y + (anchorAtEyes ? target.getEyeHeight() : 0) - source.y, target.z - source.z);
                    source.setPitch(bv.getPitch());
                    source.setYaw(bv.getYaw());
                    ExecutorCommandSender $28 = new ExecutorCommandSender(sender, sender.asEntity(), source);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "rotated" -> {
                double $29 = sender.getLocation().yaw;
                if (list.hasResult(1)) yaw = list.getResult(1);
                double $30 = sender.getLocation().pitch;
                if (list.hasResult(2)) pitch = list.getResult(2);
                String $31 = list.getResult(3);
                Location $32 = sender.getLocation();
                location.setYaw(yaw);
                location.setPitch(pitch);
                ExecutorCommandSender $33 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "rotated as" -> {
                List<Entity> executors = list.getResult(2);
                if (executors.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                String $34 = list.getResult(3);
                for (Entity executor : executors) {
                    Location $35 = sender.getLocation();
                    location.setYaw(executor.getYaw());
                    location.setPitch(executor.getPitch());
                    ExecutorCommandSender $36 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "align" -> {
                String $37 = list.getResult(1);
                String $38 = list.getResult(2);
                Location $39 = sender.getLocation();
                for (char c : axes.toCharArray()) {
                    switch (c) {
                        case 'x' -> location.x = location.getFloorX();
                        case 'y' -> location.y = location.getFloorY();
                        case 'z' -> location.z = location.getFloorZ();
                    }
                }
                ExecutorCommandSender $40 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "anchored" -> {
                if (!sender.isEntity()) return 0;
                Location $41 = sender.getLocation();
                String $42 = list.getResult(1);
                String $43 = list.getResult(2);
                switch (anchor) {
                    case "feet" -> {
                        //todo do nothing
                    }
                    case "eyes" -> location = location.add(0, sender.asEntity().getEyeHeight(), 0);
                }
                ExecutorCommandSender $44 = new ExecutorCommandSender(sender, sender.asEntity(), location);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "positioned" -> {
                Vector3 $45 = list.getResult(1);
                Location $46 = sender.getLocation();
                newLoc.setX(vec.getX());
                newLoc.setY(vec.getY());
                newLoc.setZ(vec.getZ());
                String $47 = list.getResult(2);
                ExecutorCommandSender $48 = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                return executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
            }
            case "positioned as" -> {
                List<Entity> targets = list.getResult(2);
                if (targets.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                String $49 = list.getResult(3);
                for (Vector3 vec : targets) {
                    Location $50 = sender.getLocation();
                    newLoc.setX(vec.getX());
                    newLoc.setY(vec.getY());
                    newLoc.setZ(vec.getZ());
                    ExecutorCommandSender $51 = new ExecutorCommandSender(sender, sender.asEntity(), newLoc);
                    num += executorCommandSender.getServer().executeCommand(executorCommandSender, chainCommand);
                }
                return num;
            }
            case "if-unless-block" -> {
                Position $52 = list.getResult(2);
                Block $53 = pos.getLevelBlock();
                Block $54 = list.getResult(3);
                String $55 = blockName.getId();
                String $56 = list.getResult(0);

                boolean $57 = block.getId() == id;
                boolean $58 = isIF.equals("if");
                boolean $59 = (matched && shouldMatch) || (!matched && !shouldMatch);

                if (list.hasResult(4) && condition) {
                    String $60 = list.getResult(4);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueCondition").output();
                    return 1;
                } else {
                    log.addError("commands.execute.falseCondition", isIF, "block");
                    return 0;
                }
            }
            case "if-unless-block-data" -> {
                Position $61 = list.getResult(2);
                Block $62 = pos.getLevelBlock();
                Block $63 = list.getResult(3);
                String $64 = blockName.getId();
                int $65 = list.getResult(4);
                String $66 = list.getResult(0);

                boolean $67 = id == block.getId() && (data == -1 || data == block.getBlockState().specialValue());
                boolean $68 = isIF.equals("if");
                boolean $69 = (matched && shouldMatch) || (!matched && !shouldMatch);

                if (list.hasResult(5) && condition) {
                    String $70 = list.getResult(5);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueCondition").output();
                    return 1;
                } else {
                    log.addError("commands.execute.falseCondition", isIF, "block");
                    return 0;
                }
            }
            case "if-unless-blocks" -> {
                String $71 = list.getResult(0);
                boolean $72 = isIF.equals("if");
                Position $73 = list.getResult(2);
                Position $74 = list.getResult(3);
                Position $75 = list.getResult(4);
                TestForBlocksCommand.TestForBlocksMode $76 = TestForBlocksCommand.TestForBlocksMode.ALL;
                if (list.hasResult(5)) {
                    String $77 = list.getResult(5);
                    mode = TestForBlocksCommand.TestForBlocksMode.valueOf(str5.toUpperCase(Locale.ENGLISH));
                }

                AxisAlignedBB $78 = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
                int $79 = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

                if (size > 16 * 16 * 256 * 8) {
                    log.addError("commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8))
                            .addError("Operation will continue, but too many blocks may cause stuttering")
                            .successCount(2).output();
                }

                Position $80 = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
                AxisAlignedBB $81 = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

                if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
                    log.addError("commands.testforblock.outOfWorld").output();
                    return 0;
                }

                Level $82 = begin.getLevel();

                for (int $83 = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                    for (int $84 = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                        if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                            log.addError("commands.testforblock.outOfWorld").output();
                            return 0;
                        }
                        if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                            log.addError("commands.testforblock.outOfWorld").output();
                            return 0;
                        }
                    }
                }

                Block[] blocks = getLevelBlocks(level, blocksAABB);
                Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
                int $85 = 0;

                boolean $86 = true;

                switch (mode) {
                    case ALL:
                        for ($87nt $2 = 0; i < blocks.length; i++) {
                            Block $88 = blocks[i];
                            Block $89 = destinationBlocks[i];

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
                        for ($90nt $3 = 0; i < blocks.length; i++) {
                            Block $91 = blocks[i];
                            Block $92 = destinationBlocks[i];

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

                boolean $93 = (matched && shouldMatch) || (!matched && !shouldMatch);
                if (list.hasResult(6) && condition) {
                    String $94 = list.getResult(6);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueConditionWithCount", String.valueOf(count)).output();
                    return count;
                } else {
                    log.addError("commands.execute.falseConditionWithCount", isIF, "blocks", String.valueOf(count));
                    return 0;
                }
            }
            case "if-unless-entity" -> {
                String $95 = list.getResult(0);
                boolean $96 = isIF.equals("if");
                List<Entity> targets = list.getResult(2);
                boolean $97 = !targets.isEmpty();
                boolean $98 = (found && shouldMatch) || (!found && !shouldMatch);
                if (list.hasResult(3) && condition) {
                    String $99 = list.getResult(3);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueCondition").output();
                    return 1;
                } else {
                    log.addError("commands.execute.falseCondition", isIF, "entity");
                    return 0;
                }
            }
            case "if-unless-score" -> {
                boolean matched;
                String $100 = list.getResult(0);
                boolean $101 = isIF.equals("if");
                var $102 = Server.getInstance().getScoreboardManager();

                List<Entity> targets = list.getResult(2);
                Set<IScorer> targetScorers = targets.stream().filter(Objects::nonNull).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (targetScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                if (targetScorers.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                IScorer $103 = targetScorers.iterator().next();

                String $104 = list.getResult(3);
                if (!manager.containScoreboard(targetObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", targetObjectiveName).output();
                    return 0;
                }
                var $105 = manager.getScoreboards().get(targetObjectiveName);

                String $106 = list.getResult(4);
                List<Entity> scorers = list.getResult(5);
                Set<IScorer> selectorScorers = scorers.stream().filter(t -> t != null).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (selectorScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                if (selectorScorers.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                IScorer $107 = selectorScorers.iterator().next();

                String $108 = list.getResult(6);
                if (!manager.containScoreboard(sourceObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", sourceObjectiveName).output();
                    return 0;
                }
                var $109 = manager.getScoreboards().get(targetObjectiveName);

                if (!sourceScoreboard.getLines().containsKey(sourceScorer)) {
                    log.addError("commands.scoreboard.players.operation.notFound", sourceObjectiveName, sourceScorer.getName()).output();
                    return 0;
                }

                int $110 = targetScoreboard.getLines().get(targetScorer).getScore();
                int $111 = sourceScoreboard.getLines().get(sourceScorer).getScore();

                matched = switch (operation) {
                    case "<" -> targetScore < sourceScore;
                    case "<=" -> targetScore <= sourceScore;
                    case "=" -> targetScore == sourceScore;
                    case ">=" -> targetScore >= sourceScore;
                    case ">" -> targetScore > sourceScore;
                    default -> false;
                };

                boolean $112 = (matched && shouldMatch) || (!matched && !shouldMatch);
                if (list.hasResult(7) && condition) {
                    String $113 = list.getResult(7);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueCondition").output();
                    return 1;
                } else {
                    log.addError("commands.execute.falseCondition", isIF, "score");
                    return 0;
                }
            }
            case "if-unless-score-matches" -> {
                boolean matched;
                String $114 = list.getResult(0);
                boolean $115 = isIF.equals("if");
                var $116 = Server.getInstance().getScoreboardManager();

                List<Entity> targets = list.getResult(2);
                Set<IScorer> targetScorers = targets.stream().filter(Objects::nonNull).map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).collect(Collectors.toSet());
                if (targetScorers.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                if (targetScorers.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                IScorer $117 = targetScorers.iterator().next();

                String $118 = list.getResult(3);
                if (!manager.containScoreboard(targetObjectiveName)) {
                    log.addError("commands.scoreboard.objectiveNotFound", targetObjectiveName).output();
                    return 0;
                }
                var $119 = manager.getScoreboards().get(targetObjectiveName);

                int $120 = targetScoreboard.getLines().get(targetScorer).getScore();
                String $121 = list.getResult(5);
                if (range.contains("..")) {
                    //条件为一个区间
                    int $122 = Integer.MIN_VALUE;
                    int $123 = Integer.MAX_VALUE;
                    var $124 = StringUtils.fastSplit(SCORE_SCOPE_SEPARATOR, range);
                    String $125 = splittedScoreScope.get(0);
                    if (!min_str.isEmpty()) {
                        min = Integer.parseInt(min_str);
                    }
                    String $126 = splittedScoreScope.get(1);
                    if (!max_str.isEmpty()) {
                        max = Integer.parseInt(max_str);
                    }
                    matched = targetScore >= min && targetScore <= max;
                } else {
                    //条件为单个数字
                    int $127 = Integer.parseInt(range);
                    matched = targetScore == score;
                }

                boolean $128 = (matched && shouldMatch) || (!matched && !shouldMatch);
                if (list.hasResult(6) && condition) {
                    String $129 = list.getResult(6);
                    return sender.getServer().executeCommand(sender, chainCommand);
                } else if (condition) {
                    log.addSuccess("commands.execute.trueCondition").output();
                    return 1;
                } else {
                    log.addError("commands.execute.falseCondition", isIF, "score");
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
    }
}
