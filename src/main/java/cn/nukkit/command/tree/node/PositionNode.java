package cn.nukkit.command.tree.node;

import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Abstract base class for position parameter nodes in PowerNukkitX command trees.
 * <p>
 * Provides parsing and validation logic for position arguments, supporting absolute, relative (~), and local (^) coordinates.
 * Subclasses should provide the appropriate regex pattern for their coordinate type (integer or float).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses and validates position arguments using a regex pattern.</li>
 *   <li>Supports absolute, relative (~), and local (^) coordinate notation.</li>
 *   <li>Handles coordinate math for relative and local positions.</li>
 *   <li>Provides methods for retrieving the parsed position with or without a base position.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class for custom position node types (e.g., integer or float positions).</li>
 *   <li>Used as the base for all position parameter nodes.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class MyPositionNode extends PositionNode { ... }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.level.Position
 * @see ParamNode
 * @since PowerNukkitX 1.19.50
 */
/**
 * 坐标节点基类
 */
public abstract class PositionNode extends ParamNode<Position> {
    private final Pattern pattern;
    protected final double[] coordinate = new double[3];
    protected final List<String> TMP = new ArrayList<>();
    private byte relative = 0b0000;
    protected byte index = 0;

    public PositionNode(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public <E> E get() {
        return this.get(paramList.getParamTree().getSender().getPosition());
    }

    @SuppressWarnings("unchecked")
    public <E> E get(Position basePos) {
        if (this.value == null) return null;
        if (this.getRelative(0)) {
            this.value.setX(this.value.x + basePos.getX());
        }
        if (this.getRelative(1)) {
            this.value.setY(this.value.y + basePos.getY());
        }
        if (this.getRelative(2)) {
            this.value.setZ(this.value.z + basePos.getZ());
        }
        return (E) this.value;
    }

    @Override
    public void fill(String arg) {
        TMP.clear();
        //check
        var matcher = pattern.matcher(arg);
        while (matcher.find()) {
            TMP.add(matcher.group());
        }
        var str = TMP.stream().reduce((s1, s2) -> s1 + s2);
        if (str.isEmpty()) this.error();
        else if (str.get().length() != arg.length()) this.error();
        else {
            //parse
            try {
                Location loc = paramList.getParamTree().getSender().getLocation();
                for (String s : TMP) {
                    if (s.charAt(0) == '~') {
                        this.setRelative(index);
                        String relativeCoordinate = s.substring(1);
                        if (relativeCoordinate.isEmpty()) {
                            coordinate[index] = 0;
                        } else {
                            if (relativeCoordinate.charAt(0) == '+')
                                relativeCoordinate = relativeCoordinate.substring(1);
                            coordinate[index] = Double.parseDouble(relativeCoordinate);
                        }
                    } else if (s.charAt(0) == '^') {
                        if (index == 0) {
                            coordinate[0] = 0;
                            coordinate[1] = 0;
                            coordinate[2] = 0;
                        }
                        this.setRelative(index);
                        String relativeAngleCoordinate = s.substring(1);
                        if (!relativeAngleCoordinate.isEmpty()) {
                            Vector3 vector3;
                            if (relativeAngleCoordinate.charAt(0) == '+')
                                relativeAngleCoordinate = relativeAngleCoordinate.substring(1);
                            switch (index) {
                                case 0 -> {
                                    vector3 = BVector3.fromLocation(loc).rotateYaw(-90).setPitch(0).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos();
                                    coordinate[0] += vector3.x;
                                    coordinate[1] += vector3.y;
                                    coordinate[2] += vector3.z;
                                }
                                case 1 -> {
                                    vector3 = BVector3.fromLocation(loc).rotatePitch(-90).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos();
                                    coordinate[0] += vector3.x;
                                    coordinate[1] += vector3.y;
                                    coordinate[2] += vector3.z;
                                }
                                case 2 -> {
                                    vector3 = BVector3.fromLocation(loc).setLength(Double.parseDouble(relativeAngleCoordinate)).addToPos();
                                    coordinate[0] += vector3.x;
                                    coordinate[1] += vector3.y;
                                    coordinate[2] += vector3.z;
                                }
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
                    this.value = new Position(coordinate[0], coordinate[1], coordinate[2], loc.getLevel());
                    index = 0;
                }
            } catch (NumberFormatException ignore) {
                this.error();
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.relative = 0b0000;
    }

    public void setRelative(byte index) {
        switch (index) {
            case 0 -> this.relative |= 0b0001;
            case 1 -> this.relative |= 0b0010;
            case 2 -> this.relative |= 0b0100;
        }
    }

    public boolean getRelative(int index) {
        return switch (index) {
            case 0 -> (this.relative & 0b0001) == 1;
            case 1 -> (this.relative & 0b0010) == 2;
            case 2 -> (this.relative & 0b0100) == 4;
            default -> false;
        };
    }
}
