package org.powernukkitx.camera;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAudioListener;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.powernukkitx.registry.CameraRegistry;

import java.util.Objects;

/**
 * Defines a custom camera preset.
 *
 * <p>The runtime ID and protocol definition are assigned when the camera is
 * registered in {@link CameraRegistry}.</p>
 *
 * @author Curse
 */
public record CustomCameraDefinition(String identifier, CameraPreset preset) {
    public CustomCameraDefinition {
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(preset, "preset");

        validateIdentifier(identifier);

        if (!identifier.equals(preset.getName())) {
            throw new IllegalArgumentException(
                    "Camera preset name does not match definition identifier: "
                            + identifier
            );
        }
    }

    /**
     * Creates a custom camera builder.
     *
     * @param identifier namespaced camera identifier
     * @return camera definition builder
     */
    public static Builder builder(String identifier) {
        return new Builder(identifier);
    }

    private static void validateIdentifier(String identifier) {
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("Camera identifier cannot be blank");
        }

        int separator = identifier.indexOf(':');

        if (separator <= 0 || separator == identifier.length() - 1) {
            throw new IllegalArgumentException("Camera identifier must use namespace:name format: " + identifier);
        }

        String namespace = identifier.substring(0, separator);

        if ("minecraft".equals(namespace)) {
            throw new IllegalArgumentException("Custom camera presets cannot use the minecraft namespace: " + identifier);
        }
    }

    /**
     * Builder for custom camera presets.
     */
    public static final class Builder {
        private final String identifier;

        private String inheritFrom;
        private Vector3f position;
        private Float pitch;
        private Float yaw;
        private Vector2f viewOffset;
        private Vector3f entityOffset;
        private Float radius;
        private Float rotationSpeed;
        private CameraAudioListener listener;
        private OptionalBoolean snapToTarget = OptionalBoolean.empty();
        private OptionalBoolean continueTargeting = OptionalBoolean.empty();
        private Vector2f horizontalRotationLimit;
        private Vector2f verticalRotationLimit;
        private Float blockListeningRadius;

        private Builder(String identifier) {
            Objects.requireNonNull(identifier, "identifier");
            validateIdentifier(identifier);

            this.identifier = identifier;
        }

        /**
         * Sets the camera preset inherited by this custom preset.
         *
         * @param inheritFrom parent camera preset identifier
         * @return this builder
         */
        public Builder inheritFrom(String inheritFrom) {
            if (inheritFrom == null || inheritFrom.isBlank()) {
                throw new IllegalArgumentException("Inherited camera identifier cannot be blank");
            }

            this.inheritFrom = inheritFrom;
            return this;
        }

        /**
         * Inherits from the vanilla free camera.
         *
         * @return this builder
         */
        public Builder inheritFromFree() {
            return this.inheritFrom("minecraft:free");
        }

        /**
         * Inherits from the vanilla first-person camera.
         *
         * @return this builder
         */
        public Builder inheritFromFirstPerson() {
            return this.inheritFrom("minecraft:first_person");
        }

        /**
         * Inherits from the vanilla third-person camera.
         *
         * @return this builder
         */
        public Builder inheritFromThirdPerson() {
            return this.inheritFrom("minecraft:third_person");
        }

        /**
         * Inherits from the vanilla front-facing third-person camera.
         *
         * @return this builder
         */
        public Builder inheritFromThirdPersonFront() {
            return this.inheritFrom("minecraft:third_person_front");
        }

        /**
         * Inherits from the vanilla fixed-boom camera.
         *
         * @return this builder
         */
        public Builder inheritFromFixedBoom() {
            return this.inheritFrom("minecraft:fixed_boom");
        }

        /**
         * Inherits from the vanilla follow-orbit camera.
         *
         * @return this builder
         */
        public Builder inheritFromFollowOrbit() {
            return this.inheritFrom("minecraft:follow_orbit");
        }

        /**
         * Sets the default camera position.
         *
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         * @return this builder
         */
        public Builder position(float x, float y, float z) {
            this.position = Vector3f.from(x, y, z);
            return this;
        }

        /**
         * Sets the default camera position.
         *
         * @param position camera position
         * @return this builder
         */
        public Builder position(Vector3f position) {
            this.position = Objects.requireNonNull(position, "position");
            return this;
        }

        /**
         * Sets the default camera rotation.
         *
         * @param pitch camera pitch
         * @param yaw camera yaw
         * @return this builder
         */
        public Builder rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
            return this;
        }

        /**
         * Sets the default camera pitch.
         *
         * @param pitch camera pitch
         * @return this builder
         */
        public Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        /**
         * Sets the default camera yaw.
         *
         * @param yaw camera yaw
         * @return this builder
         */
        public Builder yaw(float yaw) {
            this.yaw = yaw;
            return this;
        }

        /**
         * Sets the player-relative view offset.
         *
         * @param x horizontal view offset
         * @param y vertical view offset
         * @return this builder
         */
        public Builder viewOffset(float x, float y) {
            this.viewOffset = Vector2f.from(x, y);
            return this;
        }

        /**
         * Sets the player-relative view offset.
         *
         * @param viewOffset view offset
         * @return this builder
         */
        public Builder viewOffset(Vector2f viewOffset) {
            this.viewOffset = Objects.requireNonNull(
                    viewOffset,
                    "viewOffset"
            );
            return this;
        }

        /**
         * Sets the target entity-relative camera offset.
         *
         * @param x x offset
         * @param y y offset
         * @param z z offset
         * @return this builder
         */
        public Builder entityOffset(float x, float y, float z) {
            this.entityOffset = Vector3f.from(x, y, z);
            return this;
        }

        /**
         * Sets the target entity-relative camera offset.
         *
         * @param entityOffset entity offset
         * @return this builder
         */
        public Builder entityOffset(Vector3f entityOffset) {
            this.entityOffset = Objects.requireNonNull(entityOffset, "entityOffset");
            return this;
        }

        /**
         * Sets the follow-orbit camera radius.
         *
         * @param radius camera radius
         * @return this builder
         */
        public Builder radius(float radius) {
            if (radius < 0.0f) {
                throw new IllegalArgumentException("Camera radius cannot be negative");
            }

            this.radius = radius;
            return this;
        }

        /**
         * Sets the camera rotation speed.
         *
         * @param rotationSpeed rotation speed
         * @return this builder
         */
        public Builder rotationSpeed(float rotationSpeed) {
            if (rotationSpeed < 0.0f) {
                throw new IllegalArgumentException("Camera rotation speed cannot be negative");
            }

            this.rotationSpeed = rotationSpeed;
            return this;
        }

        /**
         * Sets the camera audio listener mode.
         *
         * @param listener audio listener mode
         * @return this builder
         */
        public Builder listener(CameraAudioListener listener) {
            this.listener = Objects.requireNonNull(listener, "listener");
            return this;
        }

        /**
         * Sets whether the camera immediately snaps toward its target.
         *
         * @param snapToTarget whether to snap to the target
         * @return this builder
         */
        public Builder snapToTarget(boolean snapToTarget) {
            this.snapToTarget = OptionalBoolean.of(snapToTarget);
            return this;
        }

        /**
         * Sets whether the camera continues following its target.
         *
         * @param continueTargeting whether targeting continues
         * @return this builder
         */
        public Builder continueTargeting(boolean continueTargeting) {
            this.continueTargeting = OptionalBoolean.of(continueTargeting);
            return this;
        }

        /**
         * Sets the horizontal camera rotation limits.
         *
         * @param minimum minimum horizontal rotation
         * @param maximum maximum horizontal rotation
         * @return this builder
         */
        public Builder horizontalRotationLimit(float minimum, float maximum) {
            if (minimum > maximum) {
                throw new IllegalArgumentException("Minimum horizontal rotation cannot exceed maximum");
            }

            this.horizontalRotationLimit = Vector2f.from(minimum, maximum);
            return this;
        }

        /**
         * Sets the vertical camera rotation limits.
         *
         * @param minimum minimum vertical rotation
         * @param maximum maximum vertical rotation
         * @return this builder
         */
        public Builder verticalRotationLimit(float minimum, float maximum) {
            if (minimum > maximum) {
                throw new IllegalArgumentException("Minimum vertical rotation cannot exceed maximum");
            }

            this.verticalRotationLimit = Vector2f.from(minimum, maximum);
            return this;
        }

        /**
         * Sets the block listening radius used by the camera audio listener.
         *
         * @param blockListeningRadius block listening radius
         * @return this builder
         */
        public Builder blockListeningRadius(float blockListeningRadius) {
            if (blockListeningRadius < 0.0f) {
                throw new IllegalArgumentException("Block listening radius cannot be negative");
            }

            this.blockListeningRadius = blockListeningRadius;
            return this;
        }

        /**
         * Builds the custom camera definition.
         *
         * @return custom camera definition
         */
        public CustomCameraDefinition build() {
            CameraPreset.CameraPresetBuilder presetBuilder =
                    CameraPreset.builder()
                            .name(this.identifier);

            if (this.inheritFrom != null) {
                presetBuilder.inheritFrom(this.inheritFrom);
            }

            if (this.position != null) {
                presetBuilder.pos(this.position);
            }

            if (this.pitch != null) {
                presetBuilder.pitch(this.pitch);
            }

            if (this.yaw != null) {
                presetBuilder.yaw(this.yaw);
            }

            if (this.viewOffset != null) {
                presetBuilder.viewOffset(this.viewOffset);
            }

            if (this.entityOffset != null) {
                presetBuilder.entityOffset(this.entityOffset);
            }

            if (this.radius != null) {
                presetBuilder.radius(this.radius);
            }

            if (this.rotationSpeed != null) {
                presetBuilder.rotationSpeed(this.rotationSpeed);
            }

            if (this.listener != null) {
                presetBuilder.listener(this.listener);
            }

            if (this.snapToTarget.isPresent()) {
                presetBuilder.snapToTarget(this.snapToTarget);
            }

            if (this.continueTargeting.isPresent()) {
                presetBuilder.continueTargeting(
                        this.continueTargeting
                );
            }

            if (this.horizontalRotationLimit != null) {
                presetBuilder.horizontalRotationLimit(
                        this.horizontalRotationLimit
                );
            }

            if (this.verticalRotationLimit != null) {
                presetBuilder.verticalRotationLimit(
                        this.verticalRotationLimit
                );
            }

            if (this.blockListeningRadius != null) {
                presetBuilder.blockListeningRadius(
                        this.blockListeningRadius
                );
            }

            return new CustomCameraDefinition(
                    this.identifier,
                    presetBuilder.build()
            );
        }
    }
}
