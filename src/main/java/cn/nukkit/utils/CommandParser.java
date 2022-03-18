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

    private String next() throws ArrayIndexOutOfBoundsException {
        return this.args[++this.cursor];
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

    public int parseInt() throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public double parseDouble() throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public boolean parseBoolean() throws CommandSyntaxException {
        try {
            String arg = this.next();
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
        try {
            return this.next();
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        try {
            String arg = this.parseString();
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
        try {
            return this.parseTargets().stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    public Position parsePosition() throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(), this.getTargetLevel());
    }

    public Vector3 parseVector3() throws CommandSyntaxException {
        Vector3 baseVector = sender.getPosition();
        baseVector = parseCoordinate(baseVector,CoordinateType.X);
        baseVector = parseCoordinate(baseVector,CoordinateType.Y);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z);
        return baseVector;
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        Vector3 baseVector = sender.getPosition();
        baseVector = parseCoordinate(baseVector,CoordinateType.X);
        baseVector = parseCoordinate(baseVector,CoordinateType.Z);
        return new Vector2(baseVector.x,baseVector.z);
    }

    private Vector3 parseCoordinate(Vector3 baseVector3,CoordinateType type) throws CommandSyntaxException {
        try {
            String arg = this.next();
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
                    case X -> BVector3.fromLocation(sender.getLocation()).addAngle(-90, 0).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                    case Y -> BVector3.fromLocation(sender.getLocation()).addAngle(0, 90).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                    case Z -> BVector3.fromLocation(sender.getLocation()).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(baseVector3);
                };
            }
            return baseVector3;
        } catch (Exception e) {
            throw new CommandSyntaxException();
        }
    }

    enum CoordinateType{
        X, Y, Z
    }
}
