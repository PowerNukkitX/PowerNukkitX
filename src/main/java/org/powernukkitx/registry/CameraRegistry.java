package org.powernukkitx.registry;

import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.powernukkitx.camera.CustomCameraDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Registry containing built-in and custom camera presets.
 *
 * @author Curse
 */
public final class CameraRegistry {
    private final List<CameraPreset> presets = new ArrayList<>();
    private final List<NamedDefinition> definitions = new ArrayList<>();
    private final Map<String, NamedDefinition> definitionsByName = new HashMap<>();
    private final Map<String, CustomCameraDefinition> customDefinitions = new HashMap<>();
    private volatile DefinitionRegistry<NamedDefinition> definitionRegistry =
        SimpleDefinitionRegistry.<NamedDefinition>builder().build();

    /**
     * Registers a camera preset directly.
     *
     * @param preset camera preset
     * @return shared registered definition
     */
    public synchronized NamedDefinition register(CameraPreset preset) {
        Objects.requireNonNull(preset, "preset");

        String identifier = preset.getName();
        this.validateIdentifier(identifier);
        this.checkDuplicate(identifier);

        return this.registerInternal(identifier, preset, null);
    }

    /**
     * Registers a custom camera definition.
     *
     * @param customDefinition custom camera definition
     * @return shared registered definition
     */
    public synchronized NamedDefinition register(CustomCameraDefinition customDefinition) {
        Objects.requireNonNull(customDefinition, "customDefinition");

        String identifier = customDefinition.identifier();
        this.validateIdentifier(identifier);
        this.checkDuplicate(identifier);

        return this.registerInternal(
                identifier,
                customDefinition.preset(),
                customDefinition
        );
    }

    /**
     * Returns every registered camera preset in runtime-ID order.
     *
     * @return immutable camera preset list
     */
    public synchronized List<CameraPreset> getAll() {
        return List.copyOf(this.presets);
    }

    /**
     * Returns every registered protocol definition in runtime-ID order.
     *
     * @return immutable list of registered camera definitions
     */
    public synchronized List<NamedDefinition> getAllDefinitions() {
        return List.copyOf(this.definitions);
    }

    /**
     * Returns the protocol definition registry used by the codec helper.
     *
     * @return camera definition registry
     */
    public DefinitionRegistry<NamedDefinition> getDefinitions() {
        return this.definitionRegistry;
    }

    /**
     * Gets a shared registered definition by identifier.
     *
     * @param identifier camera identifier
     * @return registered definition or {@code null}
     */
    public synchronized NamedDefinition getDefinition(String identifier) {
        return this.definitionsByName.get(identifier);
    }

    /**
     * Gets a shared registered definition by runtime ID.
     *
     * @param runtimeId camera runtime ID
     * @return registered definition or {@code null}
     */
    public NamedDefinition getDefinition(int runtimeId) {
        return this.definitionRegistry.getDefinition(runtimeId);
    }

    /**
     * Gets a registered custom camera definition.
     *
     * <p>Vanilla presets registered directly as {@link CameraPreset} do not
     * have a corresponding {@link CustomCameraDefinition}.</p>
     *
     * @param identifier custom camera identifier
     * @return custom camera definition or {@code null}
     */
    public synchronized CustomCameraDefinition getCustomDefinition(String identifier) {
        return this.customDefinitions.get(identifier);
    }

    /**
     * Checks whether a camera identifier is registered.
     *
     * @param identifier camera identifier
     * @return {@code true} when registered
     */
    public synchronized boolean contains(String identifier) {
        return this.definitionsByName.containsKey(identifier);
    }

    /**
     * Returns the number of registered camera presets.
     *
     * @return number of presets
     */
    public synchronized int size() {
        return this.presets.size();
    }

    private NamedDefinition registerInternal(String identifier, CameraPreset preset, CustomCameraDefinition customDefinition) {
        int runtimeId = this.presets.size();

        NamedDefinition definition = new CameraPresetDefinition(identifier, runtimeId);

        this.presets.add(preset);
        this.definitions.add(definition);
        this.definitionsByName.put(identifier, definition);

        if (customDefinition != null) {
            this.customDefinitions.put(identifier, customDefinition);
        }

        this.rebuildDefinitions();

        return definition;
    }

    private void rebuildDefinitions() {
        this.definitionRegistry =
                SimpleDefinitionRegistry
                        .<NamedDefinition>builder()
                        .addAll(this.definitions)
                        .build();
    }

    private void checkDuplicate(String identifier) {
        if (this.definitionsByName.containsKey(identifier)) {
            throw new IllegalArgumentException("Camera preset is already registered: " + identifier);
        }
    }

    private void validateIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Camera preset identifier cannot be blank");
        }

        int separator = identifier.indexOf(':');

        if (separator <= 0 || separator == identifier.length() - 1) {
            throw new IllegalArgumentException("Camera preset identifier must use namespace:name format: " + identifier);
        }
    }

    private record CameraPresetDefinition(String identifier, int runtimeId) implements NamedDefinition {
        @Override
        public String getIdentifier() {
            return this.identifier;
        }

        @Override
        public int getRuntimeId() {
            return this.runtimeId;
        }
    }
}
