package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.payload.common.DimensionType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.PrimitiveShapeDataPayload;
import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockColor;

public abstract class PrimitiveShape<T extends PrimitiveShape<T>> {

    protected Vector3f location;
    protected Float scale;
    protected Vector3f rotation;
    protected Float duration;
    protected Float maxRenderDistance;
    protected Integer color;
    protected DimensionType dimension;
    protected Long attachedToEntityId;

    protected PrimitiveShape(Vector3 location) {
        this.location = location == null ? null : location.toNetwork();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T at(Vector3 location) {
        this.location = location.toNetwork();
        return self();
    }

    public T at(double x, double y, double z) {
        this.location = Vector3f.from(x, y, z);
        return self();
    }

    public T scale(float scale) {
        this.scale = scale;
        return self();
    }

    public T rotation(Vector3 rotation) {
        this.rotation = rotation == null ? null : rotation.toNetwork();
        return self();
    }

    public T rotation(float x, float y, float z) {
        this.rotation = Vector3f.from(x, y, z);
        return self();
    }

    public T color(int argb) {
        this.color = argb;
        return self();
    }

    public T color(int red, int green, int blue, int alpha) {
        this.color = ((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
        return self();
    }

    public T color(BlockColor color) {
        return color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public T duration(float seconds) {
        this.duration = seconds;
        return self();
    }

    public T maxRenderDistance(float distance) {
        this.maxRenderDistance = distance;
        return self();
    }

    public T dimension(int dimensionId) {
        this.dimension = DimensionType.from(dimensionId);
        return self();
    }

    public T attachTo(Entity entity) {
        this.attachedToEntityId = entity.getId();
        return self();
    }

    public T attachTo(long entityId) {
        this.attachedToEntityId = entityId;
        return self();
    }

    protected abstract ScriptPrimitiveShapeType shapeType();

    protected abstract ExtraShapeDataPayload buildExtra();

    public PrimitiveShapeDataPayload toPayload() {
        PrimitiveShapeDataPayload payload = new PrimitiveShapeDataPayload();
        payload.setShapeType(shapeType());
        payload.setLocation(location);
        payload.setScale(scale);
        payload.setRotation(rotation);
        payload.setTotalTimeLeft(duration);
        payload.setMaximumRenderDistance(maxRenderDistance);
        payload.setColor(color);
        payload.setDimension(dimension);
        payload.setAttachedToEntityID(attachedToEntityId);
        payload.setExtraShapeData(buildExtra());
        return payload;
    }

    public int showTo(Player player) {
        return player.sendPrimitiveShape(toPayload());
    }

    public void updateFor(Player player, int networkId) {
        player.updatePrimitiveShape(networkId, toPayload());
    }
}
