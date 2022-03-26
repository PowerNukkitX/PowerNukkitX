package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandParser {

    private static final Pattern STRING_PATTERN = Pattern.compile("[A-Za-z]+");//Start and end are not defined
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^~?((-?\\d+)(\\.\\d+)?)?$");//float or int
    private static final Pattern INT_PATTERN = Pattern.compile("^~?(-?[0-9]*)?$");//only int
    private static final Pattern FLOAT_PATTERN = Pattern.compile("^~?((-?\\d+)(\\.\\d+))?$");//only float
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("^[~^]?(-?\\d+)?(\\.\\d+)?$");//coordinate part value

    private final Command command;
    private final CommandSender sender;
    private final String[] args;

    private int cursor = -1;

    public CommandParser(Command command, CommandSender sender, String[] args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
    }

    public CommandParser(CommandParser parser){
        this.command = parser.command;
        this.sender = parser.sender;
        this.args = parser.args;
        this.cursor = parser.cursor;
    }

    private String next() throws ArrayIndexOutOfBoundsException {
        return next(true);
    }

    private String next(boolean moveCursor) throws ArrayIndexOutOfBoundsException {
        if (moveCursor)
            return this.args[++this.cursor];
        else
            return this.args[this.cursor + 1];
    }

    public String getErrorMessage() {
        String parameter1;
        try {
            StringJoiner joiner = new StringJoiner(" ", " ", " ");
            for (String arg : Arrays.copyOfRange(this.args, 0, this.cursor)) {
                joiner.add(arg);
            }
            parameter1 = joiner.length() < 3 ? "" : joiner.toString();
        } catch (Exception e) {
            parameter1 = "";
        }

        String parameter2;
        try {
            parameter2 = this.args[this.cursor];
        } catch (Exception e) {
            parameter2 = "";
        }

        String parameter3;
        try {
            StringJoiner joiner = new StringJoiner(" ", " ", "");
            for (String arg : Arrays.copyOfRange(this.args, this.cursor + 1, this.args.length)) {
                joiner.add(arg);
            }
            parameter3 = joiner.toString();
        } catch (Exception e) {
            parameter3 = "";
        }

        return new TranslationContainer(TextFormat.RED + "commands.generic.syntax", parameter1, parameter2, parameter3, this.command.getName()).toString();
    }

    public String matchCommandForm() throws CommandSyntaxException {
        int argsLength = args.length;
        Map<String, List<ArgType>> commandParameters = new HashMap<>();
        for (Map.Entry<String,CommandParameter[]> entry : command.getCommandParameters().entrySet()){
            int length = 0;
            List<ArgType> argTypes = new ArrayList<>();
            for (CommandParameter parameter : entry.getValue()){
                if (parameter.enumData == null) {
                    switch (parameter.type) {
                        case INT:
                        case WILDCARD_INT://I don't know what is the difference between the two
                            argTypes.add(new ArgType(ArgType.Type.INT,parameter.optional));
                            length++;
                            break;
                        case FLOAT:
                            argTypes.add(new ArgType(ArgType.Type.FLOAT,parameter.optional));
                            length++;
                            break;
                        case VALUE:
                            argTypes.add(new ArgType(ArgType.Type.NUMBER,parameter.optional));
                            length++;
                            break;
                        case POSITION:
                        case BLOCK_POSITION:
                            argTypes.add(new ArgType(ArgType.Type.COORDINATE,parameter.optional));
                            argTypes.add(new ArgType(ArgType.Type.COORDINATE,parameter.optional));
                            argTypes.add(new ArgType(ArgType.Type.COORDINATE,parameter.optional));///three values
                            length+=3;
                            break;
                        case TARGET:
                        case WILDCARD_TARGET:
                        case STRING:
                        case RAWTEXT:
                        case JSON:
                        case TEXT:
                        case FILE_PATH:
                        case OPERATOR:
                        case MESSAGE:
                        case COMMAND:
                            argTypes.add(new ArgType(ArgType.Type.STRING,parameter.optional));
                            length++;
                            break;
                    }
                }else{
                    argTypes.add(new ArgType(ArgType.Type.LIMITEDVALUE,parameter.optional, parameter.enumData.getValues()));
                    length++;
                }
            }

            if (length < argsLength){
                continue;//no match
            }

            commandParameters.put(entry.getKey(), argTypes);
        }

        if (commandParameters.isEmpty()) return null;//no match

        for (Map.Entry<String, List<ArgType>> entry : commandParameters.entrySet().toArray(new Map.Entry[0])){
            boolean matched = true;
            CommandParser parser = new CommandParser(this);
            List<ArgType> argTypes = entry.getValue();
            while(parser.hasNext()){
                String next = parser.next();
                ArgType argType = argTypes.get(parser.cursor);
                if (argType.type == ArgType.Type.LIMITEDVALUE && argType.limitedValues.contains(next)) continue;
                if (argType.type == ArgType.Type.INT && INT_PATTERN.matcher(next).find()) continue;
                if (argType.type == ArgType.Type.FLOAT && FLOAT_PATTERN.matcher(next).find()) continue;
                if (argType.type == ArgType.Type.NUMBER && NUMBER_PATTERN.matcher(next).find()) continue;
                if (argType.type == ArgType.Type.COORDINATE && COORDINATE_PATTERN.matcher(next).find()) continue;
                if (argType.type == ArgType.Type.STRING && STRING_PATTERN.matcher(next).find()) continue;
                //no match
                matched = false;
            }
            if(!matched) commandParameters.remove(entry.getKey());
            if (argTypes.size() > parser.cursor + 1 && !argTypes.get(parser.cursor + 1).optional) commandParameters.remove(entry.getKey());//排除此格式如果还有必需的参数没有输入
        }

        if (commandParameters.isEmpty()) return null;//no match
        if (commandParameters.size() != 1) throw new CommandSyntaxException();//more than one match
        return commandParameters.keySet().iterator().next();
    }

    public Level getTargetLevel() {
        Level level = null;

        level = sender.getPosition().getLevel();

        return level == null ? this.sender.getServer().getDefaultLevel() : level;
    }

    public boolean hasNext(){
        return this.cursor < this.args.length - 1;
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

    public double parseOffsetDouble(double base) throws CommandSyntaxException{
        return parseOffsetDouble(base, true);
    }

    public double parseOffsetDouble(double base, boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            if (arg.startsWith("~") && !arg.substring(1).isEmpty()) {
                double relativeCoordinate = Double.parseDouble(arg.substring(1));
                return base + relativeCoordinate;
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

    public <T extends Enum<T>> T parseEnum(Class<T> enumType,boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.next(moveCursor);
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        return parseTargets(true);
    }

    public List<Entity> parseTargets(boolean moveCursor) throws CommandSyntaxException {
        try {
            String arg = this.parseString(moveCursor);
            if (EntitySelector.hasArguments(arg)) {
                return EntitySelector.matchEntities(this.sender, arg);
            }else{
                return Collections.singletonList(Server.getInstance().getPlayer(arg));
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

    public Position parsePosition(Vector3 baseVector,boolean moveCursor) throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(baseVector,moveCursor), this.getTargetLevel());
    }

    public Vector3 parseVector3() throws CommandSyntaxException {
        return parseVector3(null);
    }

    public Vector3 parseVector3(Vector3 bv) throws CommandSyntaxException {
        return parseVector3(bv, true);
    }

    public Vector3 parseVector3(Vector3 bv,boolean moveCursor) throws CommandSyntaxException {
        Vector3 baseVector = bv == null ? sender.getPosition() : bv;
        baseVector = parseCoordinate(baseVector,CoordinateType.X);
        baseVector = parseCoordinate(baseVector,CoordinateType.Y);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z);
        if (!moveCursor){
            this.cursor-=3;
        }
        return baseVector;
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        return parseVector2(true);
    }

    public Vector2 parseVector2(boolean moveCursor) throws CommandSyntaxException {
        Vector3 baseVector = sender.getPosition();
        baseVector = parseCoordinate(baseVector,CoordinateType.X,moveCursor);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z,moveCursor);
        if (!moveCursor){
            this.cursor-=2;
        }
        return new Vector2(baseVector.x,baseVector.z);
    }

    public String parseAllRemain(){
        return parseAllRemain(true);
    }

    public String parseAllRemain(boolean moveCursor){
        StringBuilder sb = new StringBuilder();
        if (moveCursor) {
            while (this.hasNext()) {
                sb.append(this.next()).append(" ");
            }
        }else{
            for (int i = this.cursor; i < this.args.length; i++) {
                sb.append(this.args[i]).append(" ");
            }
        }
        return sb.toString();
    }

    private Vector3 parseCoordinate(Vector3 baseVector3,CoordinateType type) throws CommandSyntaxException {
        return parseCoordinate(baseVector3,type,true);
    }

    private Vector3 parseCoordinate(Vector3 baseVector3,CoordinateType type,boolean moveCursor) throws CommandSyntaxException {
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
                case X -> baseVector3.setX(Double.parseDouble(arg));
                case Y -> baseVector3.setY(Double.parseDouble(arg));
                case Z -> baseVector3.setZ(Double.parseDouble(arg));
            };
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    enum CoordinateType{
        X, Y, Z
    }

    class ArgType{
        enum Type {
            INT,//only 0-9
            FLOAT,//0-9 and must include "."
            NUMBER,//int or float
            COORDINATE,//eq: ~0.1
            STRING,//must include A-Z or a-z
            LIMITEDVALUE//limited parameter types
        }
        //can be null
        List<String> limitedValues;
        Type type;
        boolean optional;

        ArgType(Type type){
            this(type,false);
        }

        ArgType(Type type,boolean optional){
            this(type,optional,null);
        }

        ArgType(Type type,boolean optional,List<String> limitedValues) {
            this.type = type;
            this.optional = optional;
            this.limitedValues = limitedValues;
        }
    }
}
