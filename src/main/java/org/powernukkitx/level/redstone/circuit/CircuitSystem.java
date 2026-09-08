package org.powernukkitx.level.redstone.circuit;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.redstone.circuit.components.BaseCircuitComponent;
import org.powernukkitx.level.redstone.circuit.components.PoweredBlockComponent;
import org.powernukkitx.level.redstone.circuit.components.ProducerComponent;

import java.util.HashMap;
import java.util.Map;

public final class CircuitSystem {
    private final Map<CircuitPosition, BaseCircuitComponent> components = new HashMap<>();

    public BaseCircuitComponent getBaseComponent(Position position) {
        return this.components.get(key(position));
    }

    public PoweredBlockComponent setupPoweredBlock(Position position) {
        PoweredBlockComponent component = new PoweredBlockComponent();
        component.setStrength(0);
        component.setConsumePowerAnyDirection(true);
        this.components.put(key(position), component);
        return component;
    }

    public ProducerComponent setupProducer(Position position, int direction, int strength) {
        ProducerComponent component = new ProducerComponent();
        component.setDirection(direction);
        component.setStrength(strength);
        this.components.put(key(position), component);
        return component;
    }

    public int getStrength(Position position) {
        BaseCircuitComponent component = getBaseComponent(position);
        return component == null ? 0 : component.getStrength();
    }

    public void removeComponent(Position position) {
        this.components.remove(key(position));
    }

    public void removeChunk(int chunkX, int chunkZ) {
        this.components.keySet().removeIf(position -> (position.x >> 4) == chunkX && (position.z >> 4) == chunkZ);
    }

    public void clear() {
        this.components.clear();
    }

    private static CircuitPosition key(Position position) {
        return new CircuitPosition(position.getFloorX(), position.getFloorY(), position.getFloorZ());
    }

    private record CircuitPosition(int x, int y, int z) {}
}
