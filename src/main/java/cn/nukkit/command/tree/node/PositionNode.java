package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PositionNode extends ParamNode<Position> {
    private final Pattern pattern;

    public PositionNode(boolean optional, Pattern pattern) {
        super(optional);
        this.pattern = pattern;
    }

    protected final double[] coordinate = new double[3];
    protected static final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg, Object... extras) throws CommandSyntaxException {
        if (extras.length < 2) throw new CommandSyntaxException();
        TMP.clear();
        var matcher = pattern.matcher(arg);
        while (matcher.find()) {
            TMP.add(matcher.group());
        }
        byte type = (byte) extras[1];
        var str = TMP.stream().reduce((s1, s2) -> s1 + s2);
        if (str.isEmpty()) throw new CommandSyntaxException();
        if (str.get().length() != arg.length()) throw new CommandSyntaxException();
        else {
            for (String s : TMP) {
                Player player = null;
                if (extras[0] instanceof Player) {
                    player = (Player) extras[0];
                }
                if (s.charAt(0) == '~') {
                    if (player == null) throw new CommandSyntaxException();
                    String relativeCoordinate = s.substring(1);
                    if (relativeCoordinate.isEmpty()) {
                        switch (type) {
                            case 0 -> coordinate[type] = player.getX();
                            case 1 -> coordinate[type] = player.getY();
                            case 2 -> coordinate[type] = player.getZ();
                            default -> throw new CommandSyntaxException();
                        }
                    }
                    switch (type) {
                        case 0 -> coordinate[type] = player.getX() + Double.parseDouble(relativeCoordinate);
                        case 1 -> coordinate[type] = player.getY() + Double.parseDouble(relativeCoordinate);
                        case 2 -> coordinate[type] = player.getZ() + Double.parseDouble(relativeCoordinate);
                        default -> throw new CommandSyntaxException();
                    }
                } else if (s.charAt(0) == '^') {
                    if (player == null) throw new CommandSyntaxException();
                    String relativeAngleCoordinate = s.substring(1);
                    if (relativeAngleCoordinate.isEmpty()) {
                        switch (type) {
                            case 0 -> coordinate[type] = player.getX();
                            case 1 -> coordinate[type] = player.getY();
                            case 2 -> coordinate[type] = player.getZ();
                            default -> throw new CommandSyntaxException();
                        }
                    }
                    switch (type) {
                        case 0 ->
                                coordinate[type] = BVector3.fromLocation(player.getLocation()).addAngle(-90, 0).setYAngle(0).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getX();
                        case 1 ->
                                coordinate[type] = BVector3.fromLocation(player.getLocation()).addAngle(0, 90).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getY();
                        case 2 ->
                                coordinate[type] = BVector3.fromLocation(player.getLocation()).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getZ();
                        default -> throw new CommandSyntaxException();
                    }
                } else {
                    coordinate[type] = Double.parseDouble(s);
                }
                if (type == 2) {
                    this.value = new Position(coordinate[0], coordinate[1], coordinate[2], player == null ? Server.getInstance().getLevel(0) : player.getLevel());
                }
            }
        }
    }
}
