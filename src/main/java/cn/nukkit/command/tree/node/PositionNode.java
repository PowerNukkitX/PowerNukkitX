package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 坐标节点基类
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class PositionNode extends ParamNode<Position> {
    private final Pattern pattern;
    protected final double[] coordinate = new double[3];
    protected byte index = 0;
    protected final List<String> TMP = new ArrayList<>();

    public PositionNode(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void fill(String arg) {
        TMP.clear();
        var matcher = pattern.matcher(arg);
        while (matcher.find()) {
            TMP.add(matcher.group());
        }
        var str = TMP.stream().reduce((s1, s2) -> s1 + s2);
        if (str.isEmpty()) this.error();
        else if (str.get().length() != arg.length()) this.error();
        else {
            try {
                Player player = null;
                if (this.parent.parent.getSender() instanceof Player p) {
                    player = p;
                }
                for (String s : TMP) {
                    if (s.charAt(0) == '~') {
                        if (player == null) {
                            this.error();
                            return;
                        }
                        String relativeCoordinate = s.substring(1);
                        if (relativeCoordinate.isEmpty()) {
                            setCorrdinate(player);
                            return;
                        } else {
                            switch (index) {
                                case 0 -> coordinate[index] = player.getX() + Double.parseDouble(relativeCoordinate);
                                case 1 -> coordinate[index] = player.getY() + Double.parseDouble(relativeCoordinate);
                                case 2 -> coordinate[index] = player.getZ() + Double.parseDouble(relativeCoordinate);
                                default -> {
                                    this.error();
                                    return;
                                }
                            }
                        }
                    } else if (s.charAt(0) == '^') {
                        if (player == null) {
                            this.error();
                            return;
                        }
                        String relativeAngleCoordinate = s.substring(1);
                        if (relativeAngleCoordinate.isEmpty()) {
                            setCorrdinate(player);
                            return;
                        } else {
                            switch (index) {
                                case 0 ->
                                        coordinate[index] = BVector3.fromLocation(player.getLocation()).addAngle(-90, 0).setYAngle(0).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getX();
                                case 1 ->
                                        coordinate[index] = BVector3.fromLocation(player.getLocation()).addAngle(0, 90).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getY();
                                case 2 ->
                                        coordinate[index] = BVector3.fromLocation(player.getLocation()).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos(player).getZ();
                                default -> {
                                    this.error();
                                    return;
                                }
                            }
                        }
                    } else {
                        coordinate[index] = Double.parseDouble(s);
                    }
                    index++;
                }
                if (index == 3) {
                    this.value = new Position(coordinate[0], coordinate[1], coordinate[2], player == null ? Server.getInstance().getDefaultLevel() : player.getLevel());
                    index = 0;
                }
            } catch (NumberFormatException ignore) {
                this.error();
            }
        }
    }

    private void setCorrdinate(Player player) {
        switch (index) {
            case 0 -> coordinate[index] = player.getX();
            case 1 -> coordinate[index] = player.getY();
            case 2 -> coordinate[index] = player.getZ();
            default -> {
                this.error();
            }
        }
    }
}
