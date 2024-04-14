package cn.nukkit.command.selector;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.NPCCommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.args.ISelectorArgument;
import cn.nukkit.command.selector.args.impl.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.utils.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.nukkit.command.selector.SelectorType.NEAREST_PLAYER;
import static cn.nukkit.command.selector.SelectorType.NPC_INITIATOR;
import static cn.nukkit.command.selector.SelectorType.RANDOM_PLAYER;
import static cn.nukkit.command.selector.SelectorType.SELF;
import static cn.nukkit.command.selector.SelectorType.parseSelectorType;

/**
 * 目标选择器API<p/>
 * 通过{@code getAPI()}方法获取API对象
 */


public class EntitySelectorAPI {
    private static final EntitySelectorAPI API = new EntitySelectorAPI();

    static {
        registerDefaultArguments();
    }

    public static final Pattern ENTITY_SELECTOR = Pattern.compile("^@([aeprs]|initiator)(?:\\[(.*)])?$");
    public static final String ARGUMENT_JOINER = "=";
    /**
     * 对目标选择器文本的预解析缓存
     */
    public static final Cache<String, Map<String, List<String>>> ARGS_CACHE = Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    public static final Cache<String, Boolean> MATCHES_CACHE = Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();

    Map<String, ISelectorArgument> registry;
    List<ISelectorArgument> orderedArgs;

    private EntitySelectorAPI() {
        registry = new HashMap<>();
        orderedArgs = new ArrayList<>();
    }

    public static EntitySelectorAPI getAPI() {
        return API;
    }

    private static void registerDefaultArguments() {
        API.registerArgument(new X());
        API.registerArgument(new Y());
        API.registerArgument(new Z());
        API.registerArgument(new DX());
        API.registerArgument(new DY());
        API.registerArgument(new DZ());
        API.registerArgument(new C());
        API.registerArgument(new R());
        API.registerArgument(new RM());
        API.registerArgument(new Name());
        API.registerArgument(new Tag());
        API.registerArgument(new L());
        API.registerArgument(new LM());
        API.registerArgument(new M());
        API.registerArgument(new Type());
        API.registerArgument(new RX());
        API.registerArgument(new RXM());
        API.registerArgument(new RY());
        API.registerArgument(new RYM());
        API.registerArgument(new Scores());
    }

    /**
     * 通过给定的命令发送者和目标选择器文本匹配实体
     * @param sender 命令发送者
     * @param token 目标选择器文本
     * @return 目标实体
     */
    public List<Entity> matchEntities(CommandSender sender, String token) throws SelectorSyntaxException {
        var cachedMatches = MATCHES_CACHE.getIfPresent(token);
        //先从缓存确认不是非法选择器
        if (cachedMatches != null && !cachedMatches)
            throw new SelectorSyntaxException("Malformed entity selector token");
        Matcher matcher = ENTITY_SELECTOR.matcher(token);
        //非法目标选择器文本
        if (!matcher.matches()) {
            //记录非法选择器到缓存
            MATCHES_CACHE.put(token, false);
            throw new SelectorSyntaxException("Malformed entity selector token");
        }
        //查询是否存在预解析结果。若不存在则解析
        Map<String, List<String>> arguments = ARGS_CACHE.getIfPresent(token);
        if (arguments == null) {
            arguments = parseArgumentMap(matcher.group(2));
            ARGS_CACHE.put(token, arguments);
        }
        //获取克隆过的执行者位置信息
        var senderLocation = sender.getLocation();
        //获取选择器类型
        var selectorType = parseSelectorType(matcher.group(1));
        //根据选择器类型先确定实体检测范围
        List<Entity> entities;
        if (selectorType != SELF) {
            entities = Lists.newArrayList(senderLocation.level.getEntities());
        } else {
            if (sender.isEntity())
                entities = Lists.newArrayList(sender.asEntity());
            //没有符合条件的实体
            else return Lists.newArrayList();
        }
        //若是NPC触发选择器，则只处理触发NPC对话的玩家
        if (selectorType == NPC_INITIATOR) {
            if (sender instanceof NPCCommandSender npc)
                entities = Lists.newArrayList(npc.getInitiator());
            else
                return Lists.newArrayList();
        }
        //对于确定的玩家类型选择器，排除掉不是玩家的实体
        switch (selectorType) {
            case ALL_PLAYERS, NEAREST_PLAYER ->
                entities.removeIf(e -> !(e instanceof Player));
            default -> {}
        }
        //没符合条件的实体了，return
        if (entities.isEmpty()) return entities;
        //参照坐标
        for (var arg : orderedArgs) {
            try {
                if (!arg.isFilter()) {
                    Predicate<Entity> predicate;
                    if (arguments.containsKey(arg.getKeyName()))
                        predicate = arg.getPredicate(selectorType, sender, senderLocation, arguments.get(arg.getKeyName()).toArray(new String[0]));
                    else if (arg.getDefaultValue(arguments, selectorType, sender) != null)
                        predicate = arg.getPredicate(selectorType, sender, senderLocation, arg.getDefaultValue(arguments, selectorType, sender));
                    else continue;
                    if (predicate == null)
                        continue;
                    entities.removeIf(entity -> !predicate.test(entity));
                } else {
                    if (arguments.containsKey(arg.getKeyName()))
                        entities = arg.getFilter(selectorType, sender, senderLocation, arguments.get(arg.getKeyName()).toArray(new String[0])).apply(entities);
                    else continue;
                }
            } catch (Throwable t) {
                throw new SelectorSyntaxException("Error while parsing selector argument: " + arg.getKeyName(), t);
            }
            //没符合条件的实体了，return
            if (entities.isEmpty()) return entities;
        }
        //随机选择一个
        if (selectorType == RANDOM_PLAYER && !entities.isEmpty()) {
            var index = ThreadLocalRandom.current().nextInt(entities.size()) + 1;
            Entity currentEntity = null;
            int i = 1;
            for (var localCurrent : entities){
                if (i == index) {
                    currentEntity = localCurrent;
                    break;
                }
                i++;
            }
            return Lists.newArrayList(currentEntity);
        }
        //选择最近玩家
        if (selectorType == NEAREST_PLAYER && entities.size() != 1) {
            Entity nearest = null;
            double min = Double.MAX_VALUE;
            for (var entity : entities) {
                var distanceSquared = 0d;
                if ((distanceSquared = senderLocation.distanceSquared(entity)) < min) {
                    min = distanceSquared;
                    nearest = entity;
                }
            }
            entities = Lists.newArrayList(nearest);
        }
        return entities;
    }

    /**
     * 检查给定文本是否是合法目标选择器
     * @param token 给定文本
     * @return 是否是合法目标选择器
     */
    public boolean checkValid(String token) {
        return MATCHES_CACHE.get(token, k -> ENTITY_SELECTOR.matcher(token).matches());
    }

    /**
     * 注册一个选择器参数
     * @param argument 选择器参数对象
     * @return 是否注册成功（若已存在相同key值的选择器参数则注册失败，返回false）
     */
    public boolean registerArgument(ISelectorArgument argument) {
        if (!registry.containsKey(argument.getKeyName())) {
            registry.put(argument.getKeyName(), argument);
            orderedArgs.add(argument);
            Collections.sort(orderedArgs);
            return true;
        }
        return false;
    }

    protected Map<String, List<String>> parseArgumentMap(String inputArguments) throws SelectorSyntaxException {
        Map<String, List<String>> args = Maps.newHashMap();

        if (inputArguments != null) {
            for (String arg : separateArguments(inputArguments)) {
                var split = StringUtils.fastSplit(ARGUMENT_JOINER, arg, 2);
                String argName = split.get(0);

                if (!registry.containsKey(argName)) {
                    throw new SelectorSyntaxException("Unknown selector argument: " + argName);
                }

                if (!args.containsKey(argName)) {
                    args.put(argName, Lists.newArrayList(split.size() > 1 ? split.get(1) : ""));
                } else {
                    args.get(argName).add(split.size() > 1 ? split.get(1) : "");
                }
            }
        }

        return args;
    }

    protected List<String> separateArguments(String inputArguments) {
        boolean go_on = false;
        List<String> result = new ArrayList<>();
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

        return result.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
}
