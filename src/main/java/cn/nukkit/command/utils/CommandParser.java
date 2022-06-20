package cn.nukkit.command.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CommandParser {

    private static final String STRING_PATTERN = "(\\S+)";
    private static final String TARGET_PATTERN = "(\"@(?:[aeprs]|initiator)(?:\\[.*])?\"|@(?:[aeprs]|initiator)(?:\\[[^ ]*])?|\"[A-Za-z][A-Za-z0-9\\s]*\"|[A-Za-z][A-Za-z0-9_]*)";
    private static final String WILDCARD_TARGET_PATTERN = "(\\S+)";
    private static final String MULTIPLE_STRING_PATTERN = "(.+)";
    private static final String INT_PATTERN = "(~-?\\d+|-?\\d+|~)";//only int
    private static final String WILDCARD_INT_PATTERN = "(~-?\\d+|-?\\d+|~|\\*)";//only int
    private static final String FLOAT_PATTERN = "(~-?\\d+(?:\\.\\d+)?|-?\\d+(?:\\.\\d+)?|~)";//float or int
    private static final String COORDINATE_PATTERN = "([~^]-?\\d+(?:\\.\\d+)?|-?\\d+(?:\\.\\d+)?|[~^])";//coordinate part value
    private static final String BLOCK_COORDINATE_PATTERN = "([~^]-?\\d+|-?\\d+|[~^])";//block coordinate part value

    //using cache to improve performance
    private static Cache<String,CommandParser> result_cache = CacheBuilder.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    private static Cache<String,PatternCache> pattern_cache = CacheBuilder.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();

    private record PatternCache(Pattern pattern,int length){}

    private final Command command;
    private final CommandSender sender;
    private final String[] args;
    private String[] parsedArgs;
    private String matchedCommandForm;

    private int cursor = -1;

    public CommandParser(Command command, CommandSender sender, String[] args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
        matchCommandForm();
    }

    public CommandParser(CommandParser parser) {
        this.command = parser.command;
        this.sender = parser.sender;
        this.args = parser.args;
        this.parsedArgs = parser.parsedArgs;
        this.matchedCommandForm = parser.matchedCommandForm;
        this.cursor = parser.cursor;
    }

    private String next() throws ArrayIndexOutOfBoundsException {
        return next(true);
    }

    private String next(boolean moveCursor) throws ArrayIndexOutOfBoundsException {
        if (moveCursor)
            return this.parsedArgs[++this.cursor];
        else
            return this.parsedArgs[this.cursor + 1];
    }

    public String matchCommandForm() {
        StringBuilder argStringBuilder = new StringBuilder();
        for (String arg : this.args) {
            if (!arg.contains(" ")) {
                argStringBuilder.append(arg).append(" ");
            } else {
                argStringBuilder.append("\"").append(arg).append("\" ");
            }
        }
        String argString = argStringBuilder.toString();
        if (matchedCommandForm != null) return matchedCommandForm;//already got its form
        CommandParser tmp_parsedArgs = result_cache.getIfPresent(argString);//get from cache to improve performance
        if (tmp_parsedArgs != null) {
            this.parsedArgs = tmp_parsedArgs.parsedArgs;
            this.matchedCommandForm = tmp_parsedArgs.matchedCommandForm;
            return this.matchedCommandForm;
        }
        Map<String, Pattern> commandPatterns = new HashMap<>();
        Map<String, Integer> commandArgLength = new HashMap<>();//non-optional args' length
        for (Map.Entry<String, CommandParameter[]> entry : command.getCommandParameters().entrySet()) {
            PatternCache pcache = pattern_cache.getIfPresent(command.getName() + "_" + entry.getKey());
            if (pcache != null) {
                commandPatterns.put(entry.getKey(), pcache.pattern);
                commandArgLength.put(entry.getKey(), pcache.length);
            }else {
                StringBuilder pattern = new StringBuilder();
                pattern.append("^");
                int length = 0;//non-optional args' length
                for (CommandParameter parameter : entry.getValue()) {
                    pattern.append("(?:");
                    if (parameter.enumData == null) {
                        switch (parameter.type) {
                            case INT -> {
                                pattern.append(INT_PATTERN);
                                pattern.append("\\s+");
                            }
                            case WILDCARD_INT -> {
                                pattern.append(WILDCARD_INT_PATTERN);
                                pattern.append("\\s+");
                            }
                            case FLOAT, VALUE -> {
                                pattern.append(FLOAT_PATTERN);
                                pattern.append("\\s+");
                            }
                            case POSITION -> {
                                for (int i = 0; i < 3; i++) {
                                    pattern.append(COORDINATE_PATTERN);
                                    if (i != 2) {
                                        pattern.append("\\s*");
                                    } else {
                                        pattern.append("\\s+");
                                    }
                                }
                            }
                            case BLOCK_POSITION -> {
                                for (int i = 0; i < 3; i++) {
                                    pattern.append(BLOCK_COORDINATE_PATTERN);
                                    if (i != 2) {
                                        pattern.append("\\s*");
                                    } else {
                                        pattern.append("\\s+");
                                    }
                                }
                            }
                            case TARGET -> {
                                pattern.append(TARGET_PATTERN);
                                pattern.append("\\s+");
                            }
                            case WILDCARD_TARGET -> {
                                pattern.append(WILDCARD_TARGET_PATTERN);
                                pattern.append("\\s+");
                            }
                            case STRING, FILE_PATH, OPERATOR -> {
                                pattern.append(STRING_PATTERN);
                                pattern.append("\\s+");
                            }
                            case MESSAGE, TEXT, COMMAND, RAWTEXT, JSON -> {
                                pattern.append(MULTIPLE_STRING_PATTERN);
                                pattern.append("\\s+");
                            }
                        }
                    } else {
                        if (parameter.enumData.getName().equals("Block") || parameter.enumData.getName().equals("Item") || !parameter.enumData.isLimited()) {
                            pattern.append(STRING_PATTERN);
                        } else {
                            pattern.append("(");
                            for (String str : parameter.enumData.getValues()) {
                                for (char c : str.toCharArray()) {
                                    if (c == '$' || c == '(' || c == ')' || c == '*' || c == '+' || c == '.' || c == '[' || c == '?' || c == '\\' || c == '^' || c == '{' || c == '|') {
                                        pattern.append("\\");
                                    }
                                    pattern.append(c);
                                }
                                pattern.append("|");
                            }
                            pattern.deleteCharAt(pattern.length() - 1);
                            pattern.append(")");
                        }
                        pattern.append("\\s+");
                    }
                    pattern.append(")");
                    if (!parameter.optional) {
                        if (parameter.type == CommandParamType.POSITION || parameter.type == CommandParamType.BLOCK_POSITION) {
                            length += 3;
                        } else {
                            length++;
                        }
                    } else {
                        pattern.append("?");
                    }
                }

                pattern.append("$");
                Pattern compiled = Pattern.compile(pattern.toString());
                commandPatterns.put(entry.getKey(),compiled);
                commandArgLength.put(entry.getKey(), length);

                pattern_cache.put(command.getName() + "_" + entry.getKey(),new PatternCache(compiled, length));//cache the compiled pattern
            }
        }

        for (Map.Entry<String, Pattern> entry : commandPatterns.entrySet().toArray(new Map.Entry[0])) {
            Matcher matcher = entry.getValue().matcher(argString);
            if (!matcher.find()) {
                commandPatterns.remove(entry.getKey());
            }
        }

        String result = null;

        if (commandPatterns.size() == 1) {
            result = commandPatterns.keySet().iterator().next();
        } else if (commandPatterns.size() == 0) {
            result = null;
        } else if (commandPatterns.size() > 1) {
            String maxLengthForm = commandPatterns.keySet().iterator().next();
            int maxLength = commandArgLength.get(maxLengthForm);
            for (Map.Entry<String, Pattern> entry : commandPatterns.entrySet()) {
                if (commandArgLength.get(entry.getKey()) > maxLength) {
                    maxLength = commandArgLength.get(entry.getKey());
                    maxLengthForm = entry.getKey();
                }
            }
            result = maxLengthForm;
        } else {
            result = null;
        }

        if (result == null) {
            return null;
        }

        Matcher matcher = commandPatterns.get(result).matcher(argString.toString());
        matcher.find();
        String[] pArg = new String[matcher.groupCount()];
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                StringBuilder pArgBuilder = new StringBuilder(matcher.group(i));
                if (pArgBuilder.charAt(0) == '"') {
                    pArgBuilder.deleteCharAt(0);
                }
                if (pArgBuilder.charAt(pArgBuilder.length() - 1) == '"') {
                    pArgBuilder.deleteCharAt(pArgBuilder.length() - 1);
                }
                pArg[i - 1] = pArgBuilder.toString();
            } else {
                pArg[i - 1] = null;
            }
        }
        this.parsedArgs = pArg;

        matchedCommandForm = result;

        result_cache.put(argString.toString(), this);
        return result;
    }

    public Level getTargetLevel() {
        Level level = null;

        level = sender.getPosition().getLevel();

        return level == null ? this.sender.getServer().getDefaultLevel() : level;
    }

    public boolean hasNext() {
        return this.cursor < this.parsedArgs.length - 1 && this.parsedArgs[this.cursor + 1] != null;
    }

    public int parseInt() throws CommandSyntaxException {
        return parseInt(true);
    }

    public int parseInt(boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public int parseWildcardInt(int defaultValue) throws CommandSyntaxException {
        return parseWildcardInt(defaultValue, true);
    }

    public int parseWildcardInt(int defaultValue, boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            if (arg.equals("*")) {
                return defaultValue;
            }
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public double parseDouble() throws CommandSyntaxException {
        return parseDouble(true);
    }

    public double parseDouble(boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public double parseOffsetDouble(double base) throws CommandSyntaxException {
        return parseOffsetDouble(base, true);
    }

    public double parseOffsetDouble(double base, boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            if (arg.startsWith("~")) {
                if (!arg.substring(1).isEmpty()) {
                    double relativeCoordinate = Double.parseDouble(arg.substring(1));
                    return base + relativeCoordinate;
                } else {
                    return base;
                }
            }
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public boolean parseBoolean() throws CommandSyntaxException {
        return parseBoolean(true);
    }

    public boolean parseBoolean(boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            switch (arg.toLowerCase()) {
                case "true":
                    return true;
                case "false":
                    return false;
            }
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
        throw new CommandSyntaxException();
    }

    public String parseString() throws CommandSyntaxException {
        return parseString(true);
    }

    public String parseString(boolean moveCursor) throws CommandSyntaxException {
        try {
            return this.next(moveCursor);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        return parseEnum(enumType, true);
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType, boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    //only for non-wildcard target selector
    public List<Entity> parseTargets() throws CommandSyntaxException {
        return parseTargets(true);
    }

    public List<Entity> parseTargets(boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.parseString(moveCursor);
            if (EntitySelector.hasArguments(arg)) {
                return EntitySelector.matchEntities(this.sender, arg);
            } else {
                Player player = Server.getInstance().getPlayer(arg);
                return player == null? Collections.emptyList() : Collections.singletonList(player);
            }
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public List<Player> parseTargetPlayers() throws CommandSyntaxException {
        return parseTargetPlayers(true);
    }

    public List<Player> parseTargetPlayers(boolean moveCursor) throws CommandSyntaxException {
        try {
            return this.parseTargets(moveCursor).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public Position parsePosition() throws CommandSyntaxException {
        return this.parsePosition(null);
    }

    public Position parsePosition(Vector3 baseVector) throws CommandSyntaxException {
        return this.parsePosition(baseVector, true);
    }

    public Position parsePosition(Vector3 baseVector, boolean moveCursor) throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(baseVector, moveCursor), this.getTargetLevel());
    }

    public Vector3 parseVector3() throws CommandSyntaxException {
        return parseVector3(null);
    }

    public Vector3 parseVector3(Vector3 bv) throws CommandSyntaxException {
        return parseVector3(bv, true);
    }

    public Vector3 parseVector3(Vector3 bv, boolean moveCursor) throws CommandSyntaxException {
        Vector3 baseVector = bv == null ? sender.getPosition() : bv;
        baseVector = parseCoordinate(baseVector, CoordinateType.X);
        baseVector = parseCoordinate(baseVector, CoordinateType.Y);
        baseVector = parseCoordinate(baseVector, CoordinateType.Z);
        if (!moveCursor) {
            this.cursor -= 3;
        }
        return baseVector;
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        return parseVector2(true);
    }

    public Vector2 parseVector2(boolean moveCursor) throws CommandSyntaxException {
        Vector3 baseVector = sender.getPosition();
        baseVector = parseCoordinate(baseVector, CoordinateType.X, moveCursor);
        baseVector = parseCoordinate(baseVector, CoordinateType.Z, moveCursor);
        if (!moveCursor) {
            this.cursor -= 2;
        }
        return new Vector2(baseVector.x, baseVector.z);
    }

    public String parseAllRemain() {
        return parseAllRemain(true);
    }

    public String parseAllRemain(boolean moveCursor) {
        StringBuilder sb = new StringBuilder();
        if (moveCursor) {
            while (this.hasNext()) {
                sb.append(this.next()).append(" ");
            }
        } else {
            for (int i = this.cursor; i < this.parsedArgs.length; i++) {
                sb.append(this.parsedArgs[i]).append(" ");
            }
        }
        return sb.toString();
    }

    private Vector3 parseCoordinate(Vector3 baseVector3, CoordinateType type) throws CommandSyntaxException {
        return parseCoordinate(baseVector3, type, true);
    }

    private Vector3 parseCoordinate(Vector3 baseVector3, CoordinateType type, boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            if (arg.startsWith("~")) {
                String relativeCoordinate = arg.substring(1);
                if (relativeCoordinate.isEmpty()) {
                    return baseVector3;
                }
                return switch (type) {
                    case X -> baseVector3.add(Double.parseDouble(relativeCoordinate), 0, 0);
                    case Y -> baseVector3.add(0, Double.parseDouble(relativeCoordinate), 0);
                    case Z -> baseVector3.add(0, 0, Double.parseDouble(relativeCoordinate));
                };
            }
            if (arg.startsWith("^")) {
                String relativeAngleCoordinate = arg.substring(1);
                if (relativeAngleCoordinate.isEmpty()) {
                    return baseVector3;
                }
                return switch (type) {
                    case X -> BVector3.fromLocation(sender.getLocation()).addAngle(-90, 0).setYAngle(0).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                    case Y -> BVector3.fromLocation(sender.getLocation()).addAngle(0, 90).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                    case Z -> BVector3.fromLocation(sender.getLocation()).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                };
            }
            return switch (type) {
                case X -> baseVector3.clone().setX(Double.parseDouble(arg));
                case Y -> baseVector3.clone().setY(Double.parseDouble(arg));
                case Z -> baseVector3.clone().setZ(Double.parseDouble(arg));//return a new vector
            };
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    enum CoordinateType {
        X, Y, Z
    }
}
