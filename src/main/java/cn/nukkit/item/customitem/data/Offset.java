package cn.nukkit.item.customitem.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3f;


/**
 * Offset代表RenderOffsets中的偏移量对象
 * <p>
 * This represents the offset object in RenderOffsets
 */
@PowerNukkitXOnly
@Since("1.19.40-r1")
public class Offset {
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    private Offset() {
    }

    public static Offset builder() {
        return new Offset();
    }

    public Offset position(float x, float y, float z){
        this.position = new Vector3f(x,y,z);
        return this;
    }

    public Offset rotation(float x, float y, float z){
        this.rotation = new Vector3f(x,y,z);
        return this;
    }

    public Offset scale(float x, float y, float z){
        this.scale = new Vector3f(x,y,z);
        return this;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
