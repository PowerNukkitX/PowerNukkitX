package cn.nukkit.command.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.NPCCommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public final class EntitySelector {
    public static final Map<Integer, String> ENTITY_ID2NAME = AddEntityPacket.LEGACY_IDS;
    public static final Map<String, Integer> ENTITY_NAME2ID;

    static {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
        ENTITY_ID2NAME.forEach((id, name) -> builder.put(name, id));
        ENTITY_NAME2ID = builder.build();
    }

    private static final Pattern ENTITY_SELECTOR = Pattern.compile("^@([aeprs]|initiator)(?:\\[(.*)])?$");
    private static final Splitter ARGUMENT_JOINER = Splitter.on('=').limit(2);

    private static final Set<String> ARGS = Sets.newHashSet();

    private static final String ARG_X = registerArgument("x");
    private static final String ARG_Y = registerArgument("y");
    private static final String ARG_Z = registerArgument("z");
    private static final String ARG_DX = registerArgument("dx");
    private static final String ARG_DY = registerArgument("dy");
    private static final String ARG_DZ = registerArgument("dz");
    private static final String ARG_R = registerArgument("r");
    private static final String ARG_RM = registerArgument("rm");
    private static final String ARG_C = registerArgument("c");
    private static final String ARG_L = registerArgument("l");
    private static final String ARG_LM = registerArgument("lm");
    private static final String ARG_M = registerArgument("m");
    private static final String ARG_NAME = registerArgument("name");
    private static final String ARG_RX = registerArgument("rx");
    private static final String ARG_RXM = registerArgument("rxm");
    private static final String ARG_RY = registerArgument("ry");
    private static final String ARG_RYM = registerArgument("rym");
    private static final String ARG_TYPE = registerArgument("type");
    private static final String ARG_TAG = registerArgument("tag");
    private static final String ARG_SCORE = registerArgument("scores");

    private static final Set<String> LEVEL_ARGS = Sets.newHashSet(ARG_X, ARG_Y, ARG_Z, ARG_DX, ARG_DY, ARG_DZ, ARG_RM, ARG_R);
    private static final Predicate<String> VALID_ARGUMENT = arg -> arg != null && ARGS.contains(arg);

    private static Cache<String, Map<String, List<String>>> args_cache = CacheBuilder.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();

    private static String registerArgument(String arg) {
        ARGS.add(arg);
        return arg;
    }

    public static List<Entity> matchEntities(CommandSender sender, String token) {
        Matcher matcher = ENTITY_SELECTOR.matcher(token);

        if (matcher.matches()) {
            Map<String, List<String>> args = args_cache.getIfPresent(token);
            if (args == null) {
                try {
                    args = getArgumentMap(matcher.group(2));
                    args_cache.put(token, args);
                } catch (SelectorSyntaxException e) {
                    e.printStackTrace();
                }
            }

            if (isEntityTypeValid(args)) {
                String selectorType = matcher.group(1);

                BlockVector3 blockVec = getBlockVectorFromArguments(args, sender);
                Vector3 vec = getVector3FromArguments(args, sender);
                List<Level> levels = getLevels(sender, args);

                List<Entity> matchingEntities = Lists.newArrayList();

                for (Level level : levels) {
                    if (level != null) {
                        List<Predicate<Entity>> predicates = Lists.newArrayList();
                        predicates.addAll(getTypePredicates(args, selectorType));
                        predicates.addAll(getXpLevelPredicates(args));
                        predicates.addAll(getGamemodePredicates(args));
                        predicates.addAll(getNamePredicates(args));
                        predicates.addAll(getRadiusPredicates(args, vec));
                        predicates.addAll(getRotationsPredicates(args));
                        predicates.addAll(getTagsPredicates(args));
                        predicates.addAll(getScoresPredicate(args));

                        if ("s".equalsIgnoreCase(selectorType) || "initiator".equalsIgnoreCase(selectorType)) {
                            Entity entity = null;
                            if ("s".equalsIgnoreCase(selectorType)) {
                                if (sender.isEntity()) {
                                    entity = sender.asEntity();
                                }
                            } else if (sender instanceof NPCCommandSender npcCommandSender) {
                                entity = npcCommandSender.getInitiator();
                            } else {
                                return Collections.emptyList();
                            }

                            if (entity != null) {
                                if (args.containsKey(ARG_DX) || args.containsKey(ARG_DY) || args.containsKey(ARG_DZ)) {
                                    int dx = getInt(args, ARG_DX, 0);
                                    int dy = getInt(args, ARG_DY, 0);
                                    int dz = getInt(args, ARG_DZ, 0);
                                    AxisAlignedBB aabb = getAABB(blockVec, dx, dy, dz);

                                    if (!aabb.intersectsWith(entity.getBoundingBox())) {
                                        return Collections.emptyList();
                                    }
                                }

                                for (Predicate<Entity> predicate : predicates) {
                                    if (!predicate.apply(entity)) {
                                        return Collections.emptyList();
                                    }
                                }

                                return Lists.newArrayList(entity);
                            } else {
                                return Collections.emptyList();
                            }
                        }
                        matchingEntities.addAll(filterResults(args, predicates, selectorType, level, blockVec));
                    }
                }
                return getEntitiesFromPredicates(matchingEntities, args, sender, selectorType, vec);
            }
        }

        return Collections.emptyList();
    }

    public static List<Player> matchPlayers(CommandSender sender, String token) {
        return EntitySelector.matchEntities(sender, token).stream().filter(e -> e instanceof Player).map(e -> (Player) e).toList();
    }

    private static List<Level> getLevels(CommandSender sender, Map<String, List<String>> argumentMap) {
        List<Level> levels = Lists.newArrayList();

        if (hasLevelArgument(argumentMap)) {
            levels.add(sender.getPosition().getLevel());
        } else {
            levels.addAll(sender.getServer().getLevels().values());
        }

        return levels;
    }

    private static boolean isEntityTypeValid(Map<String, List<String>> params) {
        List<String> types = getArgument(params, ARG_TYPE);

        if (types == null) {
            return true;
        }

        for (String type : types) {
            if (type != null) {
                String identifier = type.startsWith("!") ? type.substring(1) : type;
                if (!ENTITY_NAME2ID.containsKey(identifier.startsWith("minecraft:") ? identifier : "minecraft:" + identifier)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<Predicate<Entity>> getTypePredicates(Map<String, List<String>> params, String selectorType) {
        List<String> types = getArgument(params, ARG_TYPE);
        if (types == null) {
            return Collections.emptyList();
        }
        List<Predicate<Entity>> predicates = Lists.newArrayList();

        for (String type : types) {
            if (type != null && (selectorType.equals("e") || selectorType.equals("r") || selectorType.equals("s"))) {
                boolean inverted;
                String identifier;

                if (type.startsWith("!")) {
                    inverted = true;
                    type = type.substring(1);
                } else {
                    inverted = false;
                }

                if (!type.startsWith("minecraft:")) {
                    identifier = "minecraft:" + type;
                } else {
                    identifier = type;
                }

                predicates.add(entity -> entity != null && (entity instanceof Player && identifier.equals("minecraft:player") || ENTITY_NAME2ID.get(identifier) == entity.getNetworkId()) != inverted);
            } else {
                if (!selectorType.equals("e") && !selectorType.equals("s")) {
                    predicates.add(entity -> entity instanceof Player);
                }
            }
        }
        return List.of(e -> predicates.stream().allMatch(predicate -> predicate.apply(e)));
    }

    private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, List<String>> params) {
        List<Predicate<Entity>> results = Lists.newArrayList();

        int lm = getInt(params, ARG_LM, -1);
        int l = getInt(params, ARG_L, -1);

        if (lm > -1 || l > -1) {
            results.add(entity -> {
                if (entity instanceof Player) {
                    int level = ((Player) entity).getExperienceLevel();
                    return (lm <= -1 || level >= lm) && (l <= -1 || level <= l);
                }

                return false;
            });
        }

        return results;
    }

    private static List<Predicate<Entity>> getGamemodePredicates(Map<String, List<String>> params) {
        List<Predicate<Entity>> results = Lists.newArrayList();

        List<String> args = getArgument(params, ARG_M);
        String m = args != null ? args.get(0) : null;

        if (m != null) {
            boolean inverted = m.startsWith("!");
            if (inverted) {
                m = m.substring(1);
            }

            int gamemodeId;
            try {
                gamemodeId = Integer.parseInt(m);
            } catch (NumberFormatException e) {
                gamemodeId = parseGameMode(m, -1);
            }

            int f = gamemodeId;
            results.add(entity -> {
                if (entity instanceof Player) {
                    return inverted == (((Player) entity).getGamemode() != f);
                }

                return false;
            });
        }

        return results;
    }

    private static List<Predicate<Entity>> getNamePredicates(Map<String, List<String>> params) {
        List<Predicate<Entity>> predicates = Lists.newArrayList();

        List<String> names = getArgument(params, ARG_NAME);

        if (names == null) {
            return predicates;
        }

        for (String name : names) {
            if (name != null) {
                boolean inverted = name.startsWith("!");
                if (inverted) {
                    name = name.substring(1);
                }
                String finalName = name;
                predicates.add(entity -> entity != null && entity.getName().equals(finalName) != inverted);
            }
        }
        return List.of(e -> predicates.stream().allMatch(predicate -> predicate.apply(e)));
    }

    private static List<Predicate<Entity>> getRadiusPredicates(Map<String, List<String>> params, Vector3 vec) {
        double rm = getInt(params, ARG_RM, -1);
        double r = getInt(params, ARG_R, -1);

        boolean rmInverted = rm < -0.5d;
        boolean rInverted = r < -0.5d;

        if (rmInverted && rInverted) {
            return Collections.emptyList();
        } else {
            double rmSquare = Math.pow(Math.max(rm, 1.0E-4d), 2);
            double rSquare = Math.pow(Math.max(r, 1.0E-4d), 2);

            return Lists.newArrayList(entity -> {
                if (entity != null) {
                    double squaredDistance = vec.distanceSquared(entity);
                    return (rmInverted || squaredDistance >= rmSquare) && (rInverted || squaredDistance <= rSquare);
                }

                return false;
            });
        }
    }

    private static List<Predicate<Entity>> getRotationsPredicates(Map<String, List<String>> params) {
        List<Predicate<Entity>> results = Lists.newArrayList();

        if (params.containsKey(ARG_RYM) || params.containsKey(ARG_RY)) {
            int rym = clampAngle(getInt(params, ARG_RYM, 0));
            int ry = clampAngle(getInt(params, ARG_RY, 359));

            results.add(entity -> {
                if (entity != null) {
                    int i1 = clampAngle(MathHelper.floor(entity.getYaw()));

                    if (rym > ry) {
                        return i1 >= rym || i1 <= ry;
                    } else {
                        return i1 >= rym && i1 <= ry;
                    }
                }
                return false;
            });
        }

        if (params.containsKey(ARG_RXM) || params.containsKey(ARG_RX)) {
            int rxm = clampAngle(getInt(params, ARG_RXM, 0));
            int rx = clampAngle(getInt(params, ARG_RX, 359));

            results.add(entity -> {
                if (entity == null) {
                    return false;
                } else {
                    int pitch = clampAngle(MathHelper.floor(entity.getPitch()));
                    if (rxm > rx) {
                        return pitch >= rxm || pitch <= rx;
                    } else {
                        return pitch >= rxm && pitch <= rx;
                    }
                }
            });
        }

        return results;
    }

    private static List<Predicate<Entity>> getTagsPredicates(Map<String, List<String>> params) {
        List<Predicate<Entity>> predicates = Lists.newArrayList();

        List<String> tags = getArgument(params, ARG_TAG);

        if (tags == null) {
            return predicates;
        }

        for (String tag : tags) {
            if (tag != null) {
                if (!tag.isEmpty()) {
                    boolean inverted = tag.startsWith("!");
                    if (inverted) {
                        tag = tag.substring(1);
                    }
                    String finalTag = tag;
                    predicates.add(entity -> entity != null && entity.containTag(finalTag) != inverted);
                } else {
                    predicates.add(entity -> entity != null && entity.getAllTags().isEmpty());
                }
            }
        }
        return List.of(e -> predicates.stream().allMatch(predicate -> predicate.apply(e)));
    }

    private static final Splitter SCORE_SEPARATOR = Splitter.on(',').omitEmptyStrings();
    private static final Splitter SCORE_JOINER = Splitter.on('=').limit(2);
    private static final Splitter SCORE_SCOPE_SEPARATOR = Splitter.on("..").limit(2);

    private static List<Predicate<Entity>> getScoresPredicate(Map<String, List<String>> params) {
        List<Predicate<Entity>> predicates = Lists.newArrayList();

        List<String> scores = getArgument(params, ARG_SCORE);

        if (scores == null) {
            return predicates;
        }

        for (String score_part : scores) {
            if (score_part != null) {
                score_part = score_part.substring(1, score_part.length() - 1);
                for (String score_entry : SCORE_SEPARATOR.splitToList(score_part)) {
                    Iterator<String> score_entry_split = SCORE_JOINER.split(score_entry).iterator();
                    String objective = score_entry_split.next();
                    var scoreboard = Server.getInstance().getScoreboardManager().getScoreboard(objective);
                    if (scoreboard == null) {
                        predicates.add(entity -> false);
                        return List.of(e -> predicates.stream().allMatch(predicate -> predicate.apply(e)));
                    }
                    String score = score_entry_split.next();
                    boolean inverted = score.startsWith("!");
                    if (inverted) {
                        score = score.substring(1);
                    }
                    if (score.contains("..")) {
                        int min = Integer.MIN_VALUE;
                        int max = Integer.MAX_VALUE;
                        Iterator<String> score_scope_split = SCORE_SCOPE_SEPARATOR.split(score).iterator();
                        String min_str = score_scope_split.next();
                        if (!min_str.isEmpty()) {
                            min = Integer.parseInt(min_str);
                        }
                        String max_str = score_scope_split.next();
                        if (!max_str.isEmpty()) {
                            max = Integer.parseInt(max_str);
                        }
                        final int finalMin = min;
                        final int finalMax = max;
                        predicates.add(entity -> {
                            IScorer scorer;
                            if (entity instanceof Player) {
                                scorer = new PlayerScorer((Player) entity);
                            } else {
                                scorer = new EntityScorer(entity);
                            }
                            if (scoreboard.getLine(scorer) != null) {
                                int currentScore = scoreboard.getLine(scorer).getScore();
                                return (currentScore >= finalMin && currentScore <= finalMax) != inverted;
                            } else {
                                return false;
                            }
                        });
                    } else {
                        final int finalScore = Integer.parseInt(score);
                        predicates.add(entity -> {
                            IScorer scorer;
                            if (entity instanceof Player) {
                                scorer = new PlayerScorer((Player) entity);
                            } else {
                                scorer = new EntityScorer(entity);
                            }
                            if (scoreboard.getLine(scorer) != null) {
                                int currentScore = scoreboard.getLine(scorer).getScore();
                                return (currentScore == finalScore) != inverted;
                            } else {
                                return false;
                            }
                        });
                    }
                }
            }
        }

        return List.of(e -> predicates.stream().allMatch(predicate -> predicate.apply(e)));
    }

    private static int clampAngle(int angle) {
        angle = angle % 360;

        if (angle >= 180) {
            angle -= 360;
        }

        if (angle < -180) {
            angle += 360;
        }

        return angle;
    }

    private static List<Entity> filterResults(Map<String, List<String>> params, List<Predicate<Entity>> predicates, String selectorType, Level level, BlockVector3 vec) {
        List<Entity> results = Lists.newArrayList();

        List<String> types = getArgument(params, ARG_TYPE);

        if (types != null)
            types = types.stream().map(type -> type.startsWith("!") ? type.substring(1) : type).collect(Collectors.toList());
        ///todo: check what the use of value "types"

        boolean playerOnly = !selectorType.equals("e");
        boolean random = selectorType.equals("r") && types != null;

        int dx = getInt(params, ARG_DX, 0);
        int dy = getInt(params, ARG_DY, 0);
        int dz = getInt(params, ARG_DZ, 0);
        int r = getInt(params, ARG_R, -1);

        Predicate<Entity> predicate = Predicates.and(predicates);

        if (!params.containsKey(ARG_DX) && !params.containsKey(ARG_DY) && !params.containsKey(ARG_DZ)) {
            if (r >= 0) {
                AxisAlignedBB aabb = new SimpleAxisAlignedBB(vec.getX() - r, vec.getY() - r, vec.getZ() - r, vec.getX() + r + 1, vec.getY() + r + 1, vec.getZ() + r + 1);

                if (playerOnly && !random) {
                    results.addAll(getPlayers(level, predicate));
                } else {
                    results.addAll(getNearbyEntities(level, aabb, predicate));
                }
            } else if (selectorType.equals("a")) {
                results.addAll(getPlayers(level, predicate));
            } else if (!selectorType.equals("p") && (!selectorType.equals("r") || random)) {
                results.addAll(getEntities(level, predicate));
            } else {
                results.addAll(getPlayers(level, predicate));
            }
        } else {
            AxisAlignedBB aabb = getAABB(vec, dx, dy, dz);

            if (playerOnly && !random) {
                results.addAll(getPlayers(level, Predicates.and(entity -> entity != null && aabb.intersectsWith(entity.getBoundingBox()), predicate)));
            } else {
                results.addAll(getNearbyEntities(level, aabb, predicate));
            }
        }

        return results;
    }

    private static List<Entity> getEntitiesFromPredicates(List<Entity> matchingEntities, Map<String, List<String>> params, CommandSender sender, String selectorType, Vector3 vec) {
        int c = getInt(params, ARG_C, !selectorType.equals("a") && !selectorType.equals("e") ? 1 : 0);

        if (!selectorType.equals("p") && !selectorType.equals("a") && !selectorType.equals("e")) {
            if (selectorType.equals("r")) {
                Collections.shuffle(matchingEntities);
            }
        } else {
            matchingEntities.sort((entity1, entity2) -> ComparisonChain.start().compare(entity1.distanceSquared(vec), entity2.distanceSquared(vec)).result());
        }

        Entity entity = null;
        if (sender.isEntity()) {
            entity = sender.asEntity();
        }

        if (entity != null && c == 1 && matchingEntities.contains(entity) && !"r".equals(selectorType)) {
            matchingEntities = Lists.newArrayList(entity);
        }

        if (c != 0) {
            if (c < 0) {
                Collections.reverse(matchingEntities);
            }

            matchingEntities = matchingEntities.subList(0, Math.min(Math.abs(c), matchingEntities.size()));
        }

        return matchingEntities;
    }

    private static AxisAlignedBB getAABB(BlockVector3 vec, int dx, int dy, int dz) {
        boolean negativeX = dx < 0;
        boolean negativeY = dy < 0;
        boolean negativeZ = dz < 0;

        return new SimpleAxisAlignedBB(vec.getX() + (negativeX ? dx : 0), vec.getY() + (negativeY ? dy : 0), vec.getZ() + (negativeZ ? dz : 0), vec.getX() + (negativeX ? 0 : dx) + 1, vec.getY() + (negativeY ? 0 : dy) + 1, vec.getZ() + (negativeZ ? 0 : dz) + 1);
    }

    private static BlockVector3 getBlockVectorFromArguments(Map<String, List<String>> params, CommandSender sender) {
        int defaultX = 0;
        int defaultY = 0;
        int defaultZ = 0;

        defaultX = sender.getPosition().getFloorX();
        defaultY = sender.getPosition().getFloorY();
        defaultZ = sender.getPosition().getFloorZ();

        return new BlockVector3(getInt(params, ARG_X, defaultX), getInt(params, ARG_Y, defaultY), getInt(params, ARG_Z, defaultZ));
    }

    private static Vector3 getVector3FromArguments(Map<String, List<String>> params, CommandSender sender) {
        double defaultX = 0;
        double defaultY = 0;
        double defaultZ = 0;

        defaultX = sender.getPosition().getX();
        defaultY = sender.getPosition().getY();
        defaultZ = sender.getPosition().getZ();

        return new Vector3(getCoordinate(params, ARG_X, defaultX, true), getCoordinate(params, ARG_Y, defaultY, false), getCoordinate(params, ARG_Z, defaultZ, true));
    }

    private static double getCoordinate(Map<String, List<String>> params, String key, double defaultCoordinate, boolean offset) {
        return params.containsKey(key) ? getInt(params.get(key).get(0), MathHelper.floor(defaultCoordinate)) + (offset ? 0.5D : 0.0D) : defaultCoordinate;
    }

    private static boolean hasLevelArgument(Map<String, List<String>> params) {
        for (String arg : LEVEL_ARGS) {
            if (params.containsKey(arg)) {
                return true;
            }
        }

        return false;
    }

    private static int getInt(Map<String, List<String>> params, String key, int defaultValue) {
        return params.containsKey(key) ? getInt(params.get(key).get(0), defaultValue) : defaultValue;
    }

    private static int getInt(String value, int defaultValue) {
        try {
            if (value.startsWith("~")) {
                return defaultValue + Integer.parseInt(value.substring(1));
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static List<String> getArgument(Map<String, List<String>> params, String key) {
        return params.get(key);
    }

    public static boolean canMatchesMultiplePlayers(String inputSelector) throws SelectorSyntaxException {
        Matcher matcher = ENTITY_SELECTOR.matcher(inputSelector);

        if (matcher.matches()) {
            Map<String, List<String>> map = getArgumentMap(matcher.group(2));
            String selectorType = matcher.group(1);
            return getInt(map, ARG_C, !"a".equals(selectorType) && !"e".equals(selectorType) ? 1 : 0) != 1;
        }

        return false;
    }

    public static boolean hasArguments(String inputSelector) {
        return ENTITY_SELECTOR.matcher(inputSelector).matches();
    }

    private static Map<String, List<String>> getArgumentMap(String inputArguments) throws SelectorSyntaxException {
        Map<String, List<String>> args = Maps.newHashMap();

        if (inputArguments != null) {
            for (String arg : separateArguments(inputArguments)) {
                Iterator<String> iterator = ARGUMENT_JOINER.split(arg).iterator();
                String argName = iterator.next();

                if (!VALID_ARGUMENT.apply(argName)) {
                    throw new SelectorSyntaxException(); //Unknown command argument: argName
                }

                if (!args.containsKey(argName)) {
                    args.put(argName, Lists.newArrayList(iterator.hasNext() ? iterator.next() : ""));
                } else {
                    args.get(argName).add(iterator.hasNext() ? iterator.next() : "");
                }
            }
        }

        return args;
    }

    private static List<String> separateArguments(String inputArguments) {
        boolean go_on = false;
        List<String> result = new ArrayList<String>();
        int start = 0;

        for (int i = 0; i < inputArguments.length(); i++) {
            if (inputArguments.charAt(i) == ',' && !go_on) {
                result.add(inputArguments.substring(start, i));
                start = i + 1;
            }
            if (inputArguments.charAt(i) == '{') {
                go_on = true;
            }
            if (inputArguments.charAt(i) == '}') {
                go_on = false;
                i++;
                result.add(inputArguments.substring(start, i));
                start = i + 1;
            }
        }

        if (start < inputArguments.length())
            result.add(inputArguments.substring(start));

        return result.stream().filter(s -> !s.isEmpty()).toList();
    }

    private static List<Entity> getEntities(Level level, Predicate<Entity> filter) {
        List<Entity> entities = Lists.newArrayList();

        for (Entity entity : level.getEntities()) {
            if (filter.apply(entity)) {
                entities.add(entity);
            }
        }

        return entities;
    }

    private static List<Player> getPlayers(Level level, Predicate<Entity> filter) {
        List<Player> players = Lists.newArrayList();

        for (Player player : level.getPlayers().values()) {
            if (filter.apply(player)) {
                players.add(player);
            }
        }

        return players;
    }

    private static List<Entity> getNearbyEntities(Level level, AxisAlignedBB aabb, Predicate<Entity> filter) {
        List<Entity> entities = Lists.newArrayList();

        for (Entity entity : level.getNearbyEntities(aabb)) {
            if (filter.apply(entity)) {
                entities.add(entity);
            }
        }

        return entities;
    }

    private static int parseGameMode(String name, int fallback) {
        for (GameMode gamemode : GameMode.values()) {
            if (gamemode.name.equals(name) || gamemode.shortName.equals(name)) {
                return gamemode.id;
            }
        }

        return fallback;
    }

    private EntitySelector() {

    }

    private enum GameMode {
        SURVIVAL(0, "survival", "s"),
        CREATIVE(1, "creative", "c"),
        ADVENTURE(2, "adventure", "a"),
        SPECTATOR(3, "spectator", "sp");

        private final int id;
        private final String name;
        private final String shortName;

        GameMode(int id, String name, String shortName) {
            this.id = id;
            this.name = name;
            this.shortName = shortName;
        }
    }
}
