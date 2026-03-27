package cn.nukkit.entity.components;

import cn.nukkit.math.Vector3f;

import java.util.*;

/**
 * Bedrock component: {@code minecraft:rideable}.
 *
 * Defines the ride behavior of an entity, including seats, rider interaction,
 * dismount rules, and which entities are allowed to mount it.
 *
 * <p>This component controls how passengers attach to the entity, how many
 * seats are available, and which seat acts as the controlling seat.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code controlling_seat} — Index of the seat that controls entity movement.</li>
 *   <li>{@code crouching_skip_interact} — If true, crouching players cannot mount the entity.</li>
 *   <li>{@code dismount_mode} — Defines where riders dismount (default or on_top_center).</li>
 *   <li>{@code family_types} — Entity family types allowed to ride this entity.</li>
 *   <li>{@code interact_text} — Localization key shown when interacting with the entity.</li>
 *   <li>{@code passenger_max_width} — Maximum width of passengers allowed to mount.</li>
 *   <li>{@code pull_in_entities} — If true, entities matching {@code family_types} may be automatically mounted.</li>
 *   <li>{@code rider_can_interact} — If true, riders can interact with the entity while mounted.</li>
 *   <li>{@code seat_count} — Total number of available seats.</li>
 *   <li>{@code seats} — Optional detailed seat configuration.</li>
 * </ul>
 *
 * <p>If {@code seats} are defined, they describe the physical seat positions
 * and camera configuration for riders.</p>
 *
 * <p>Bedrock behavior also allows input handling (ground, air, water) to be
 * implemented separately via input components.</p>
 * 
 * @author Curse
 */
// TODO: Auto pull-in entities
public record RideableComponent(
        int controllingSeat,
        boolean crouchingSkipInteract,
        DismountMode dismountMode,
        Set<String> familyTypes,
        String interactText,
        float passengerMaxWidth,
        boolean pullInEntities,
        boolean riderCanInteract,
        int seatCount,
        List<Seat> seats
    ) {
    public RideableComponent {
        dismountMode = (dismountMode == null) ? DismountMode.DEFAULT : dismountMode;
        interactText = (interactText == null) ? "" : interactText;

        if (!Float.isFinite(passengerMaxWidth)) passengerMaxWidth = 0.0f;
        passengerMaxWidth = Math.max(0.0f, passengerMaxWidth);

        if (familyTypes == null || familyTypes.isEmpty()) {
            familyTypes = Collections.emptySet();
        } else {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            for (String v : familyTypes) {
                if (v == null) continue;
                String s = v.trim();
                if (s.isEmpty()) continue;
                out.add(s.toLowerCase());
            }
            familyTypes = out.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(out);
        }

        if (seats == null || seats.isEmpty()) {
            seats = Collections.emptyList();
        } else {
            ArrayList<Seat> out = new ArrayList<>(seats.size());
            for (Seat s : seats) {
                if (s != null) out.add(s);
            }
            seats = out.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(out);
        }

        seatCount = Math.max(1, seatCount);
        seatCount = Math.max(seatCount, seats.size());

        controllingSeat = Math.max(0, controllingSeat);
        controllingSeat = Math.min(controllingSeat, seatCount - 1);
    }

    public static RideableComponent defaults() {
        return new RideableComponent(
                0,
                true,
                DismountMode.DEFAULT,
                Collections.emptySet(),
                "",
                0.0f,
                false,
                false,
                1,
                Collections.emptyList()
        );
    }

    public enum DismountMode {
        DEFAULT,
        ON_TOP_CENTER;

        public static DismountMode fromString(String v) {
            if (v == null || v.isBlank()) return DEFAULT;
            String s = v.trim().toLowerCase();
            return switch (s) {
                case "on_top_center" -> ON_TOP_CENTER;
                default -> DEFAULT;
            };
        }

        public String toBedrockString() {
            return switch (this) {
                case ON_TOP_CENTER -> "on_top_center";
                default -> "default";
            };
        }
    }

    public record Seat(
            int minRiderCount,
            int maxRiderCount,
            Vector3f position,
            Float lockRiderRotationDegrees,
            Float rotateRiderByDegrees,
            Float thirdPersonCameraRadius,
            Float cameraRelaxDistanceSmoothing
        ) {
        public Seat {
            minRiderCount = Math.max(0, minRiderCount);
            maxRiderCount = Math.max(0, maxRiderCount);

            if (position == null) {
                position = new Vector3f(0f, 0f, 0f);
            } else {
                float x = Float.isFinite(position.x) ? position.x : 0f;
                float y = Float.isFinite(position.y) ? position.y : 0f;
                float z = Float.isFinite(position.z) ? position.z : 0f;
                position = new Vector3f(x, y, z);
            }

            if (lockRiderRotationDegrees != null && !Float.isFinite(lockRiderRotationDegrees)) lockRiderRotationDegrees = null;
            if (rotateRiderByDegrees != null && !Float.isFinite(rotateRiderByDegrees)) rotateRiderByDegrees = null;
            if (thirdPersonCameraRadius != null && !Float.isFinite(thirdPersonCameraRadius)) thirdPersonCameraRadius = null;
            if (cameraRelaxDistanceSmoothing != null && !Float.isFinite(cameraRelaxDistanceSmoothing)) cameraRelaxDistanceSmoothing = null;
        }
    }

    public enum InputType {
        AIR,
        GROUND,
        WATER;

        public static InputType fromString(String v) {
            if (v == null || v.isBlank()) return null;

            return switch (v.trim().toLowerCase()) {
                case "air" -> AIR;
                case "ground" -> GROUND;
                case "water" -> WATER;
                default -> null;
            };
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
