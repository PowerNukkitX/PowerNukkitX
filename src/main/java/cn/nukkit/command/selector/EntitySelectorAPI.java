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
 * Provides the API for parsing, registering, and matching Minecraft entity selectors in PowerNukkitX.
 * <p>
 * EntitySelectorAPI enables advanced command selector functionality, supporting Minecraft's target selector syntax
 * (e.g., @p, @a, @e, @r, @s, @initiator) with argument parsing, caching, and extensible argument registration.
 * It is used to resolve selectors to entity lists, validate selector syntax, and register custom selector arguments.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses and matches entity selectors with arguments (e.g., @e[type=zombie,tag=foo]).</li>
 *   <li>Supports all vanilla selector types and custom NPC initiator.</li>
 *   <li>Extensible argument system: register custom selector arguments implementing {@link ISelectorArgument}.</li>
 *   <li>Efficient caching of parsed arguments and selector validity for performance.</li>
 *   <li>Handles family/type exclusivity, argument validation, and error reporting via {@link SelectorSyntaxException}.</li>
 *   <li>Supports random and nearest player/entity selection, as well as self and NPC initiator targeting.</li>
 *   <li>Thread-safe, singleton access via {@link #getAPI()}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Obtain the singleton instance using {@link #getAPI()}.</li>
 *   <li>Call {@link #matchEntities(CommandSender, String)} to resolve a selector string to a list of entities.</li>
 *   <li>Register custom selector arguments with {@link #registerArgument(ISelectorArgument)}.</li>
 *   <li>Use {@link #checkValid(String)} to validate selector syntax.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * List<Entity> entities = EntitySelectorAPI.getAPI().matchEntities(sender, "@e[type=zombie,r=10]");
 * boolean valid = EntitySelectorAPI.getAPI().checkValid("@p");
 * EntitySelectorAPI.getAPI().registerArgument(new MyCustomSelectorArgument());
 * </pre>
 *
 * <b>Selector Syntax:</b>
 * <ul>
 *   <li>Selectors: @a (all players), @e (all entities), @p (nearest player), @r (random player), @s (self), @initiator (NPC initiator)</li>
 *   <li>Arguments: [key=value,...] (e.g., @e[type=zombie,tag=foo])</li>
 *   <li>Supports custom arguments and advanced filtering.</li>
 * </ul>
 *
 * <b>Thread Safety:</b> All caches and registries are thread-safe for concurrent command execution.
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */


public class EntitySelectorAPI {
    private static final EntitySelectorAPI API = new EntitySelectorAPI();

    static {
        registerDefaultArguments();
    }

    public static final Pattern ENTITY_SELECTOR = Pattern.compile("^@([aeprs]|initiator)(?:\\[(.*)])?$");
    public static final String ARGUMENT_JOINER = "=";
    /**
     * Pre-parsed caching of target selector text
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
        API.registerArgument(new Family());
        API.registerArgument(new RX());
        API.registerArgument(new RXM());
        API.registerArgument(new RY());
        API.registerArgument(new RYM());
        API.registerArgument(new Scores());
    }

    /**
     * Matches entities with the given command sender and target selector text.
     * @param sender Command sender
     * @param token Target selector text
     * @return Target Entity
     */
    public List<Entity> matchEntities(CommandSender sender, String token) throws SelectorSyntaxException {
        var cachedMatches = MATCHES_CACHE.getIfPresent(token);
        // First confirm from the cache that it is not an illegal selector
        if (cachedMatches != null && !cachedMatches)
            throw new SelectorSyntaxException("Malformed entity selector token");
        Matcher matcher = ENTITY_SELECTOR.matcher(token);
        // Illegal target selector text
        if (!matcher.matches()) {
            // Record illegal selectors to cache
            MATCHES_CACHE.put(token, false);
            throw new SelectorSyntaxException("Malformed entity selector token");
        }
        // Check if there is a pre-parsed result. If not, parse it.
        Map<String, List<String>> arguments = ARGS_CACHE.getIfPresent(token);
        if (arguments == null) {
            arguments = parseArgumentMap(matcher.group(2));
            ARGS_CACHE.put(token, arguments);
        }

        if (arguments.containsKey("family") && !arguments.get("family").isEmpty()
                && arguments.containsKey("type") && !arguments.get("type").isEmpty()) {
            MATCHES_CACHE.put(token, false);
            throw new SelectorSyntaxException("Malformed selector: 'type' and 'family' cannot be used together");
        }

        // Get the location information of the cloned executor
        var senderLocation = sender.getLocation();
        // Get the selector type
        var selectorType = parseSelectorType(matcher.group(1));
        // Determine the entity detection scope first according to the selector type
        List<Entity> entities;
        if (selectorType != SELF) {
            entities = Lists.newArrayList(senderLocation.level.getEntities());
        } else {
            if (sender.isEntity())
                entities = Lists.newArrayList(sender.asEntity());
            // No entities matching the criteria
            else return Lists.newArrayList();
        }
        // If the selector is triggered by an NPC, only the player who triggered the NPC dialogue will be processed
        if (selectorType == NPC_INITIATOR) {
            if (sender instanceof NPCCommandSender npc)
                entities = Lists.newArrayList(npc.getInitiator());
            else
                return Lists.newArrayList();
        }
        // For a specific player type selector, exclude entities that are not players.
        switch (selectorType) {
            case ALL_PLAYERS, NEAREST_PLAYER ->
                entities.removeIf(e -> !(e instanceof Player));
            default -> {}
        }
        // There are no entities that meet the conditions, return
        if (entities.isEmpty()) return entities;

        boolean hasFamilyArg = arguments.containsKey("family") && !arguments.get("family").isEmpty();

        // Reference coordinates
        for (var arg : orderedArgs) {
            try {
                if (hasFamilyArg && "type".equals(arg.getKeyName())) {
                    continue; // extra safety; main path now throws earlier
                }

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
            // There are no entities that meet the conditions, return
            if (entities.isEmpty()) return entities;
        }
        // Randomly select one
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
        // Select Recent Player
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
     * Checks if the given text is a valid target selector
     * @param token Given text
     * @return Is it a valid target selector?
     */
    public boolean checkValid(String token) {
        return MATCHES_CACHE.get(token, k -> ENTITY_SELECTOR.matcher(token).matches());
    }

    /**
     * Register a selector parameter
     * @param argument Selector parameter object
     * @return Whether the registration is successful (if a selector parameter with the same key value already exists, the registration fails and returns false)
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
