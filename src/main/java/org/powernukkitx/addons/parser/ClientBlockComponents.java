package org.powernukkitx.addons.parser;

import org.powernukkitx.block.customblock.data.Transformation;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.powernukkitx.addons.parser.BehaviorBlockParser.asBoolean;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.asFloat;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.asInt;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.asStringList;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.asVector3;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.faces;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.lowerOr;
import static org.powernukkitx.addons.parser.BehaviorBlockParser.optString;

/**
 * Block components in the schema a dedicated server sends the client, captured from the definitions
 * Bedrock Dedicated Server 1.26.50 writes for the same behaviour pack JSON. The client reads these
 * names and types exactly, so they are not derived from the pack format: face sets become bitmasks,
 * several keys switch to camel case, and absent values are written with the defaults the dedicated
 * server fills in.
 */
final class ClientBlockComponents {

    private static final List<String> CONNECTION_DIRECTIONS = List.of("south", "north", "east", "west");

    private ClientBlockComponents() {
    }

    /**
     * @return the geometry, or null when the value names none
     */
    static @Nullable CompoundTag geometry(JsonElement value) {
        CompoundTag geometry = visualGeometry(value);
        if (geometry == null) {
            return null;
        }
        byte uvLock = geometry.getByte("uv_lock");
        geometry.remove("uv_lock");
        return geometry.putByte("ignoreGeometryForIsSolid", 0)
                .putByte("isV1Fullblock", 0)
                .putByte("needsLegacyTopRotation", 0)
                .putByte("useBlockTypeLightAbsorption", 0)
                .putByte("uv_lock", uvLock);
    }

    /**
     * The geometry fields an embedded visual carries, a subset of the block geometry.
     */
    private static @Nullable CompoundTag visualGeometry(JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : null;
        String identifier = json != null ? optString(json, "identifier") : value.isJsonPrimitive() ? value.getAsString() : "";
        if (identifier.isEmpty()) {
            return null;
        }
        CompoundTag bones = new CompoundTag(new LinkedHashMap<>());
        JsonObject boneVisibility = json == null ? null : json.getAsJsonObject("bone_visibility");
        if (boneVisibility != null) {
            for (Map.Entry<String, JsonElement> bone : boneVisibility.entrySet()) {
                if (bone.getValue().isJsonPrimitive()) {
                    bones.putString(bone.getKey(), bone.getValue().getAsString());
                }
            }
        }
        String cullingLayer = json == null ? "" : optString(json, "culling_layer");
        return new CompoundTag(new LinkedHashMap<>())
                .putCompound("bone_visibility", bones)
                .putString("culling", json == null ? "" : optString(json, "culling"))
                .putString("culling_layer", cullingLayer.isEmpty() ? "minecraft:culling_layer.undefined" : cullingLayer)
                .putString("culling_shape", json == null ? "" : optString(json, "culling_shape"))
                .putString("identifier", identifier.toLowerCase(Locale.ENGLISH))
                .putByte("uv_lock", json != null && asBoolean(json.get("uv_lock"), false) ? 1 : 0);
    }

    /**
     * A face whose value is a string names the material instance it reuses, which the client reads
     * from {@code mappings}.
     */
    static CompoundTag materialInstances(JsonObject materialInstances) {
        CompoundTag mappings = new CompoundTag(new LinkedHashMap<>());
        CompoundTag materials = new CompoundTag(new LinkedHashMap<>());
        for (Map.Entry<String, JsonElement> entry : materialInstances.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                mappings.putString(entry.getKey(), entry.getValue().getAsString());
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject material = entry.getValue().getAsJsonObject();
            int packed = (asBoolean(material.get("face_dimming"), true) ? 0x1 : 0)
                    | (asBoolean(material.get("isotropic"), false) ? 0x2 : 0)
                    | 0x4;
            materials.putCompound(entry.getKey(), new CompoundTag(new LinkedHashMap<>())
                    .putFloat("ambient_occlusion", ambientOcclusion(material.get("ambient_occlusion")))
                    .putByte("packed_bools", packed)
                    .putString("render_method", lowerOr(material, "render_method", "opaque"))
                    .putString("texture", optString(material, "texture"))
                    .putString("tint_method", lowerOr(material, "tint_method", "none")));
        }
        return new CompoundTag(new LinkedHashMap<>())
                .putCompound("mappings", mappings)
                .putCompound("materials", materials);
    }

    /**
     * Older packs give ambient occlusion as a boolean; newer ones as its intensity.
     */
    private static float ambientOcclusion(@Nullable JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean() ? 1f : 0f;
        }
        return asFloat(element, 1f);
    }

    static @Nullable CompoundTag transformation(JsonElement value) {
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject json = value.getAsJsonObject();
        Vector3 rotationPivot = asVector3(json.get("rotation_pivot"), 0);
        Vector3 scalePivot = asVector3(json.get("scale_pivot"), 0);
        return new Transformation(asVector3(json.get("translation"), 0), asVector3(json.get("scale"), 1),
                asVector3(json.get("rotation"), 0)).toCompoundTag()
                .putFloat("RXP", (float) rotationPivot.x)
                .putFloat("RYP", (float) rotationPivot.y)
                .putFloat("RZP", (float) rotationPivot.z)
                .putFloat("SXP", (float) scalePivot.x)
                .putFloat("SYP", (float) scalePivot.y)
                .putFloat("SZP", (float) scalePivot.z)
                .putByte("hasJsonVersionBeforeValidation", 0);
    }

    static @Nullable CompoundTag liquidDetection(JsonElement value) {
        JsonElement rules = value.isJsonObject() ? value.getAsJsonObject().get("detection_rules") : null;
        if (rules == null || !rules.isJsonArray()) {
            return null;
        }
        ListTag<CompoundTag> translated = new ListTag<>();
        for (JsonElement element : rules.getAsJsonArray()) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject rule = element.getAsJsonObject();
            String liquid = optString(rule, "liquid_type");
            String touches = optString(rule, "on_liquid_touches");
            translated.add(new CompoundTag(new LinkedHashMap<>())
                    .putByte("canContainLiquid", asBoolean(rule.get("can_contain_liquid"), false) ? 1 : 0)
                    .putString("liquidType", liquid.isEmpty() ? "water" : liquid)
                    .putString("onLiquidTouches", touches.isEmpty() ? "blocking" : touches)
                    .putByte("stopsLiquidFromDirection", faceMask(rule.get("stops_liquid_flowing_from_direction")))
                    .putByte("use_liquid_clipping", asBoolean(rule.get("use_liquid_clipping"), false) ? 1 : 0));
        }
        return new CompoundTag().putList("detectionRules", translated);
    }

    static CompoundTag redstoneConductivity(JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
        return new CompoundTag(new LinkedHashMap<>())
                .putByte("allowsWireToStepDown", asBoolean(json.get("allows_wire_to_step_down"), true) ? 1 : 0)
                .putByte("redstoneConductor", asBoolean(json.get("redstone_conductor"), false) ? 1 : 0);
    }

    /**
     * Without {@code connected_faces} every face is connected. The dedicated server derives
     * {@code allowPowerDown} from the up face and {@code allowPowerUp} from the down face.
     */
    static @Nullable CompoundTag redstoneProducer(JsonElement value) {
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject json = value.getAsJsonObject();
        Set<BlockFace> connected = json.has("connected_faces") ? faces(json.get("connected_faces")) : Set.of(BlockFace.values());
        ListTag<StringTag> faceList = new ListTag<>();
        for (BlockFace face : BlockFace.values()) {
            if (connected.contains(face)) {
                faceList.add(new StringTag(face.getName()));
            }
        }
        return new CompoundTag(new LinkedHashMap<>())
                .putByte("allowPowerDown", connected.contains(BlockFace.UP) ? 1 : 0)
                .putByte("allowPowerUp", connected.contains(BlockFace.DOWN) ? 1 : 0)
                .putList("connected_faces", faceList)
                .putByte("power", Math.clamp(asInt(json.get("power"), 0), 0, 15))
                .putString("strongly_powered_face", optString(json, "strongly_powered_face").toLowerCase(Locale.ENGLISH))
                .putByte("transform_relative", asBoolean(json.get("transform_relative"), false) ? 1 : 0);
    }

    static @Nullable CompoundTag support(JsonElement value) {
        String shape = value.isJsonObject() ? optString(value.getAsJsonObject(), "shape") : "";
        return shape.isEmpty() ? null : new CompoundTag().putString("shape", shape);
    }

    static CompoundTag connectionRule(JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
        String accepts = optString(json, "accepts_connections_from");
        List<String> enabled = json.has("enabled_directions") ? asStringList(json.get("enabled_directions")) : CONNECTION_DIRECTIONS;
        ListTag<StringTag> directions = new ListTag<>();
        for (String direction : CONNECTION_DIRECTIONS) {
            if (enabled.contains(direction)) {
                directions.add(new StringTag(direction));
            }
        }
        return new CompoundTag(new LinkedHashMap<>())
                .putString("accepts_connections_from", accepts.isEmpty() ? "all" : accepts)
                .putList("enabled_directions", directions);
    }

    static CompoundTag destructionParticles(JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
        return new CompoundTag(new LinkedHashMap<>())
                .putInt("particle_count", asInt(json.get("particle_count"), 100))
                .putString("texture", optString(json, "texture"))
                .putString("tint_method", lowerOr(json, "tint_method", "none"));
    }

    static CompoundTag flowerPottable() {
        return new CompoundTag().putByte("usePreR26U2FlowerPotOffset", 0);
    }

    static CompoundTag randomOffset(JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
        CompoundTag offset = new CompoundTag(new LinkedHashMap<>());
        for (String axis : List.of("x", "y", "z")) {
            JsonObject axisJson = json.getAsJsonObject(axis);
            JsonObject range = axisJson == null ? null : axisJson.getAsJsonObject("range");
            offset.putCompound(axis, new CompoundTag(new LinkedHashMap<>())
                    .putCompound("range", new CompoundTag(new LinkedHashMap<>())
                            .putFloat("max", range == null ? 0f : asFloat(range.get("max"), 0f))
                            .putFloat("min", range == null ? 0f : asFloat(range.get("min"), 0f)))
                    .putInt("steps", axisJson == null ? 0 : asInt(axisJson.get("steps"), 0)));
        }
        return offset;
    }

    static @Nullable CompoundTag precipitationInteractions(JsonElement value) {
        String behavior = value.isJsonObject() ? optString(value.getAsJsonObject(), "precipitation_behavior") : "";
        return behavior.isEmpty() ? null : new CompoundTag().putString("precipitation_behavior", behavior);
    }

    /**
     * Without {@code allowed_faces} every face is allowed. {@code side} stands for the four
     * horizontal faces and {@code all} for every face.
     */
    static @Nullable CompoundTag placementFilter(JsonElement value) {
        JsonElement conditions = value.isJsonObject() ? value.getAsJsonObject().get("conditions") : null;
        if (conditions == null || !conditions.isJsonArray()) {
            return null;
        }
        ListTag<CompoundTag> translated = new ListTag<>();
        for (JsonElement element : conditions.getAsJsonArray()) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject condition = element.getAsJsonObject();
            ListTag<CompoundTag> filter = new ListTag<>();
            JsonElement blockFilter = condition.get("block_filter");
            if (blockFilter != null && blockFilter.isJsonArray()) {
                for (JsonElement entry : blockFilter.getAsJsonArray()) {
                    CompoundTag matcher = blockFilterEntry(entry);
                    if (matcher != null) {
                        filter.add(matcher);
                    }
                }
            }
            translated.add(new CompoundTag(new LinkedHashMap<>())
                    .putByte("allowed_faces", condition.has("allowed_faces") ? faceMask(condition.get("allowed_faces")) : 0x3f)
                    .putList("block_filter", filter));
        }
        return new CompoundTag().putList("conditions", translated);
    }

    private static @Nullable CompoundTag blockFilterEntry(JsonElement entry) {
        if (entry.isJsonPrimitive()) {
            return new CompoundTag().putString("name", entry.getAsString());
        }
        if (!entry.isJsonObject()) {
            return null;
        }
        JsonObject json = entry.getAsJsonObject();
        if (json.has("tags")) {
            return new CompoundTag().putString("tags", optString(json, "tags"));
        }
        String name = optString(json, "name");
        return name.isEmpty() ? null : new CompoundTag().putString("name", name);
    }

    static @Nullable CompoundTag embeddedVisual(JsonElement value) {
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject json = value.getAsJsonObject();
        CompoundTag geometry = json.has("geometry") ? visualGeometry(json.get("geometry")) : null;
        if (geometry == null) {
            return null;
        }
        CompoundTag materials = new CompoundTag(new LinkedHashMap<>());
        JsonObject instances = json.getAsJsonObject("material_instances");
        if (instances != null) {
            for (Map.Entry<String, JsonElement> entry : instances.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                JsonObject material = entry.getValue().getAsJsonObject();
                materials.putCompound(entry.getKey(), new CompoundTag(new LinkedHashMap<>())
                        .putByte("alpha_masked_tint", asBoolean(material.get("alpha_masked_tint"), false) ? 1 : 0)
                        .putFloat("ambient_occlusion", ambientOcclusion(material.get("ambient_occlusion")))
                        .putByte("face_dimming", asBoolean(material.get("face_dimming"), true) ? 1 : 0)
                        .putByte("isotropic", asBoolean(material.get("isotropic"), false) ? 1 : 0)
                        .putString("render_method", lowerOr(material, "render_method", "opaque"))
                        .putString("texture", optString(material, "texture"))
                        .putString("tint_method", lowerOr(material, "tint_method", "none")));
            }
        }
        return new CompoundTag(new LinkedHashMap<>())
                .putCompound("geometry", geometry)
                .putCompound("material_instances", materials);
    }

    static @Nullable CompoundTag itemVisual(JsonElement value) {
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject json = value.getAsJsonObject();
        CompoundTag geometry = json.has("geometry") ? geometry(json.get("geometry")) : null;
        if (geometry == null) {
            return null;
        }
        JsonObject instances = json.getAsJsonObject("material_instances");
        return new CompoundTag(new LinkedHashMap<>())
                .putCompound("geometryDescription", geometry)
                .putCompound("materialInstancesDescription", materialInstances(instances == null ? new JsonObject() : instances));
    }

    /**
     * The block traits, which the client expands into their states itself. Every state a returned
     * trait declares is added to {@code covered}, since the client rejects a block that also lists
     * those states among its properties.
     */
    static ListTag<CompoundTag> traits(@Nullable JsonObject traits, Set<String> covered) {
        ListTag<CompoundTag> list = new ListTag<>();
        if (traits == null) {
            return list;
        }
        JsonObject direction = traits.getAsJsonObject("minecraft:placement_direction");
        if (direction != null) {
            List<String> states = asStringList(direction.get("enabled_states"));
            if (states.contains("minecraft:corner_and_cardinal_direction")) {
                covered.add("minecraft:cardinal_direction");
                covered.add("minecraft:corner");
            }
            list.add(new CompoundTag(new LinkedHashMap<>())
                    .putList("blocks_to_corner_with", cornerBlocks(direction.get("blocks_to_corner_with")))
                    .putCompound("enabled_states", enabledStates(states, covered, "cardinal_direction",
                            "corner_and_cardinal_direction", "facing_direction", "sixteen_way_rotation"))
                    .putString("name", "minecraft:placement_direction")
                    .putFloat("y_rotation_offset", asFloat(direction.get("y_rotation_offset"), 0f)));
        }
        JsonObject connection = traits.getAsJsonObject("minecraft:connection");
        if (connection != null && asStringList(connection.get("enabled_states")).contains("minecraft:cardinal_connections")) {
            for (String side : List.of("north", "south", "east", "west")) {
                covered.add("minecraft:connection_" + side);
            }
            list.add(new CompoundTag(new LinkedHashMap<>())
                    .putInt("enabled_states", 1)
                    .putString("name", "minecraft:connection")
                    .putInt("type", 0)
                    .putInt("update_flags", 3));
        }
        JsonObject position = traits.getAsJsonObject("minecraft:placement_position");
        if (position != null) {
            list.add(new CompoundTag(new LinkedHashMap<>())
                    .putCompound("enabled_states", enabledStates(asStringList(position.get("enabled_states")), covered,
                            "block_face", "vertical_half"))
                    .putString("name", "minecraft:placement_position"));
        }
        JsonObject multiBlock = traits.getAsJsonObject("minecraft:multi_block");
        if (multiBlock != null && asStringList(multiBlock.get("enabled_states")).contains("minecraft:multi_block_part")) {
            Set<BlockFace> directionFaces = faces(multiBlock.get("direction"));
            covered.add("minecraft:multi_block_part");
            list.add(new CompoundTag(new LinkedHashMap<>())
                    .putByte("direction", (directionFaces.isEmpty() ? BlockFace.UP : directionFaces.iterator().next()).getIndex())
                    .putCompound("enabled_states", new CompoundTag().putByte("multi_block", 1))
                    .putString("name", "minecraft:multi_block")
                    .putByte("part_count", Math.clamp(asInt(multiBlock.get("parts"), 2), 2, 4))
                    .putByte("place_behavior", 0));
        }
        return list;
    }

    /**
     * Each block a corner forms with, as a name, its states and a tag query, the unused ones empty.
     */
    private static ListTag<CompoundTag> cornerBlocks(@Nullable JsonElement element) {
        ListTag<CompoundTag> blocks = new ListTag<>();
        if (element == null || !element.isJsonArray()) {
            return blocks;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            JsonObject json = entry.isJsonObject() ? entry.getAsJsonObject() : null;
            String name = json == null ? (entry.isJsonPrimitive() ? entry.getAsString() : "") : optString(json, "name");
            CompoundTag states = new CompoundTag(new LinkedHashMap<>());
            JsonObject statesJson = json == null ? null : json.getAsJsonObject("states");
            if (statesJson != null) {
                for (Map.Entry<String, JsonElement> state : statesJson.entrySet()) {
                    if (state.getValue().isJsonPrimitive()) {
                        states.putString(state.getKey(), state.getValue().getAsString());
                    }
                }
            }
            blocks.add(new CompoundTag(new LinkedHashMap<>())
                    .putString("name", name)
                    .putCompound("states", states)
                    .putString("tags", json == null ? "" : optString(json, "tags")));
        }
        return blocks;
    }

    private static CompoundTag enabledStates(List<String> enabled, Set<String> covered, String... names) {
        CompoundTag states = new CompoundTag(new LinkedHashMap<>());
        for (String name : names) {
            boolean on = enabled.contains("minecraft:" + name);
            if (on) {
                covered.add("minecraft:" + name);
            }
            states.putByte(name, on ? 1 : 0);
        }
        return states;
    }

    /**
     * Faces as the client's bitmask, one bit per face index. {@code side} sets the four horizontal
     * faces and {@code all} every face.
     */
    private static int faceMask(@Nullable JsonElement element) {
        int mask = 0;
        for (String face : asStringList(element)) {
            switch (face.toLowerCase(Locale.ENGLISH)) {
                case "all" -> mask |= 0x3f;
                case "side" -> mask |= 0x3c;
                default -> {
                    for (BlockFace candidate : BlockFace.values()) {
                        if (candidate.getName().equalsIgnoreCase(face)) {
                            mask |= 1 << candidate.getIndex();
                        }
                    }
                }
            }
        }
        return mask;
    }
}
