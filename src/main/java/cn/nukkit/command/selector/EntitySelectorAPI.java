package cn.nukkit.command.selector;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.NPCCommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.args.ISelectorArgument;
import cn.nukkit.command.selector.args.impl.*;
import cn.nukkit.entity.Entity;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.nukkit.command.selector.SelectorType.*;
import static cn.nukkit.utils.StringUtils.fastSplit;

/**
 * 目标选择器API<p/>
 * 通过{@code getAPI()}方法获取API对象
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntitySelectorAPI {
    @Getter
    private static final EntitySelectorAPI API = new EntitySelectorAPI();

    static {
        registerDefaultArguments();
    }

    @Getter
    private static final Pattern ENTITY_SELECTOR = Pattern.compile("^@([aeprs]|initiator)(?:\\[(.*)])?$");
    @Getter
    private static final String ARGUMENT_JOINER = "=";
    /**
     * 对目标选择器文本的预解析缓存
     */
    private static final Cache<String, Map<String, List<String>>> ARGS_CACHE = Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();

    Map<String, ISelectorArgument> registry;
    List<ISelectorArgument> orderedArgs;

    private EntitySelectorAPI() {
        registry = new HashMap<>();
        orderedArgs = new ArrayList<>();
    }

    private static void registerDefaultArguments() {
        API.registerArgument(new X());
        API.registerArgument(new Y());
        API.registerArgument(new Z());
        API.registerArgument(new DX());
        API.registerArgument(new DY());
        API.registerArgument(new DZ());
    }

    /**
     * 通过给定的命令发送者和目标选择器文本匹配实体
     * @param sender 命令发送者
     * @param token 目标选择器文本
     * @return 目标实体
     */
    public List<Entity> matchEntities(CommandSender sender, String token) throws SelectorSyntaxException {
        Matcher matcher = ENTITY_SELECTOR.matcher(token);
        //非法目标选择器文本
        if (!matcher.matches())
            throw new SelectorSyntaxException("Malformed entity selector token");
        //查询是否存在预解析结果。若不存在则解析
        Map<String, List<String>> arguments = ARGS_CACHE.get(token, k -> parseArgumentMap(matcher.group(2)));
        //获取选择器类型
        var selectorType = parseSelectorType(matcher.group(1));
        //根据选择器类型先确定实体检测范围
        List<Entity> entities;
        if (selectorType != SELF) {
            entities = Lists.newArrayList(sender.getLocation().level.getEntities());
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
        //对于玩家类型选择器，需要排除掉不是玩家的实体
        switch (selectorType) {
            case ALL_PLAYERS, RANDOM_PLAYER, NEAREST_PLAYER ->
                entities.removeIf(e -> !(e instanceof Player));
            default -> {}
        }
        //没符合条件的实体了，return
        if (entities.isEmpty()) return entities;
        //参照坐标
        var basePos = sender.getLocation().clone();
        for (var arg : orderedArgs) {
            List<Predicate<Entity>> predicates;
            if (arguments.containsKey(arg.getKeyName()))
                predicates = arg.getPredicates(selectorType, sender, basePos, arguments.get(arg.getKeyName()).toArray(new String[0]));
            else if (arg.getDefaultValue() != null)
                predicates = arg.getPredicates(selectorType, sender, basePos, arg.getDefaultValue());
            else continue;
            if (predicates == null || predicates.isEmpty())
                continue;
            for (Predicate<Entity> predicate : predicates)
                entities.removeIf(entity -> !predicate.test(entity));
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
        return entities;
    }

    /**
     * 检查给定文本是否是合法目标选择器
     * @param token 给定文本
     * @return 是否是合法目标选择器
     */
    public boolean checkValid(String token) {
        return ENTITY_SELECTOR.matcher(token).matches();
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

    protected Map<String, List<String>> parseArgumentMap(String inputArguments) {
        Map<String, List<String>> args = Maps.newHashMap();

        if (inputArguments != null) {
            for (String arg : separateArguments(inputArguments)) {
                var split = fastSplit(ARGUMENT_JOINER, arg, 2);
                String argName = split.get(0);

                if (!registry.containsKey(argName)) {
                    throw new SelectorSyntaxException("Unknown command argument: " + argName);
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
