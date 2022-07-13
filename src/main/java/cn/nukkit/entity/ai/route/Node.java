package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 寻路节点
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public class Node implements Comparable<Node> {

    protected Vector3 vector3;
    protected Node parent;
    protected int G;
    protected int H;
    protected int F;

    public Node(Vector3 vector3, Node parent, int G, int H) {
        this.vector3 = vector3;
        this.parent = parent;
        this.G = G;
        this.H = H;
        this.F = G + H;
    }

    @Override
    public int compareTo(Node o) {
        Objects.requireNonNull(o);
        if (this.getF() != o.getF()) {
            return this.getF() - o.getF();
        }
        double breaking;
        if ((breaking = this.getG() + (this.getH() * 0.1) - (o.getG() + (this.getH() * 0.1))) > 0) {
            return 1;
        } else if (breaking < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return vector3.toString() + "| G:" + this.G + " H:" + this.H + " F" + this.getF() + (this.parent != null ? "\tparent:" + String.valueOf(this.parent.getVector3()) : "");
    }
}
