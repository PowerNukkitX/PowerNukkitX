package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;

import java.util.*;
import java.util.stream.Collectors;

public class CommandParser {

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

        return String.format(TextFormat.RED + "Syntax error: Unexpected \"%2$s\": at \"/%4$s%1$s>>%2$s<<%3$s\"", parameter1, parameter2, parameter3, this.command.getName());
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
        baseVector = parseCoordinate(baseVector,CoordinateType.X,moveCursor);
        baseVector = parseCoordinate(baseVector,CoordinateType.Y,moveCursor);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z,moveCursor);
        return baseVector;
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        return parseVector2(true);
    }

    public Vector2 parseVector2(boolean moveCursor) throws CommandSyntaxException {
        Vector3 baseVector = sender.getPosition();
        baseVector = parseCoordinate(baseVector,CoordinateType.X,moveCursor);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z,moveCursor);
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
}
