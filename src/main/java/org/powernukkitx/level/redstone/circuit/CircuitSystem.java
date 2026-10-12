package org.powernukkitx.level.redstone.circuit;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.redstone.circuit.components.BaseCircuitComponent;
import org.powernukkitx.level.redstone.circuit.components.PoweredBlockComponent;
import org.powernukkitx.level.redstone.circuit.components.ProducerComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks redstone circuit components by their block coordinates within a level.
 *
 * <p>The system stores at most one component per position and provides helpers
 * for registering producers and powered blocks. Component state is maintained
 * in memory and can be discarded individually, by chunk, or in full.</p>
 *
 * @author Curse
 */
public final class CircuitSystem {
    private final Map<CircuitPosition, BaseCircuitComponent> components = new HashMap<>();

    /**
     * Returns the component registered at a block position.
     *
     * @param position the position to query
     * @return the registered component, or {@code null} when none exists
     */
    public BaseCircuitComponent getBaseComponent(Position position) {
        return this.components.get(key(position));
    }

    /**
     * Registers a powered-block component at a position.
     *
     * @param position the component position
     * @return the newly registered powered-block component
     */
    public PoweredBlockComponent setupPoweredBlock(Position position) {
        PoweredBlockComponent component = new PoweredBlockComponent();
        component.setStrength(0);
        component.setConsumePowerAnyDirection(true);
        this.components.put(key(position), component);
        return component;
    }

    /**
     * Registers a producer with the supplied direction and signal strength.
     *
     * @param position the component position
     * @param direction the producer direction
     * @param strength the signal strength, clamped to {@code 0..15}
     * @return the newly registered producer component
     */
    public ProducerComponent setupProducer(Position position, int direction, int strength) {
        ProducerComponent component = new ProducerComponent();
        component.setDirection(direction);
        component.setStrength(strength);
        this.components.put(key(position), component);
        return component;
    }

    /**
     * Returns the signal strength at a block position.
     *
     * @param position the position to query
     * @return the registered strength, or {@code 0} when no component exists
     */
    public int getStrength(Position position) {
        BaseCircuitComponent component = getBaseComponent(position);
        return component == null ? 0 : component.getStrength();
    }

    /**
     * Removes the component registered at a block position.
     *
     * @param position the position to clear
     */
    public void removeComponent(Position position) {
        this.components.remove(key(position));
    }

    /**
     * Removes every component stored in the specified chunk.
     *
     * @param chunkX the chunk x coordinate
     * @param chunkZ the chunk z coordinate
     */
    public void removeChunk(int chunkX, int chunkZ) {
        this.components.keySet().removeIf(position -> (position.x >> 4) == chunkX && (position.z >> 4) == chunkZ);
    }

    /**
     * Removes all registered circuit components.
     */
    public void clear() {
        this.components.clear();
    }

    private static CircuitPosition key(Position position) {
        return new CircuitPosition(position.getFloorX(), position.getFloorY(), position.getFloorZ());
    }

    private record CircuitPosition(int x, int y, int z) {}
}
