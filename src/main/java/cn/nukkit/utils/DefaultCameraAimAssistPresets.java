package cn.nukkit.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistCategory;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistPresetDefinition;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistPresetExclusionDefinition;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistPriority;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Kaooot
 */
public class DefaultCameraAimAssistPresets {

    private static final CameraAimAssistPresetDefinition AIM_ASSIST_DEFAULT;
    private static final CameraAimAssistCategory BUCKET_CATEGORY;
    private static final CameraAimAssistCategory EMPTY_HAND_CATEGORY;
    private static final CameraAimAssistCategory DEFAULT_CATEGORY;

    private static final Set<CameraAimAssistPresetDefinition> AIM_ASSIST_PRESETS = new ObjectOpenHashSet<>();
    private static final Set<CameraAimAssistCategory> AIM_ASSIST_CATEGORIES = new ObjectOpenHashSet<>();

    static {
        AIM_ASSIST_DEFAULT = new CameraAimAssistPresetDefinition();
        AIM_ASSIST_DEFAULT.setIdentifier("minecraft:aim_assist_default");
        AIM_ASSIST_DEFAULT.setExclusionSettings(new CameraAimAssistPresetExclusionDefinition());
        AIM_ASSIST_DEFAULT.getLiquidTargetingList().addAll(
                Arrays.asList(
                        "minecraft:bucket", "minecraft:oak_boat", "minecraft:birch_boat", "minecraft:spruce_boat",
                        "minecraft:jungle_boat", "minecraft:acacia_boat", "minecraft:dark_oak_boat",
                        "minecraft:mangrove_boat", "minecraft:cherry_boat", "minecraft:bamboo_raft",
                        "minecraft:oak_chest_boat", "minecraft:birch_chest_boat", "minecraft:spruce_chest_boat",
                        "minecraft:jungle_chest_boat", "minecraft:acacia_chest_boat", "minecraft:dark_oak_chest_boat",
                        "minecraft:mangrove_chest_boat", "minecraft:cherry_chest_boat", "minecraft:bamboo_chest_raft"
                )
        );
        AIM_ASSIST_DEFAULT.setHandSettings("minecraft:empty_hand");
        AIM_ASSIST_DEFAULT.setDefaultItemSettings("minecraft:default");

        AIM_ASSIST_PRESETS.add(AIM_ASSIST_DEFAULT);

        BUCKET_CATEGORY = new CameraAimAssistCategory();
        BUCKET_CATEGORY.setName("minecraft:bucket");
        BUCKET_CATEGORY.setBlocks(
                List.of(
                        new CameraAimAssistPriority("minecraft:cauldron", 60),
                        new CameraAimAssistPriority("minecraft:lava", 60),
                        new CameraAimAssistPriority("minecraft:water", 60)
                )
        );
        BUCKET_CATEGORY.setEntityDefault(30);
        BUCKET_CATEGORY.setBlockDefault(30);

        EMPTY_HAND_CATEGORY = new CameraAimAssistCategory();
        EMPTY_HAND_CATEGORY.setName("minecraft:empty_hand");
        EMPTY_HAND_CATEGORY.setBlocks(
                List.of(
                        new CameraAimAssistPriority("minecraft:oak_log", 60),
                        new CameraAimAssistPriority("minecraft:cherry_log", 60),
                        new CameraAimAssistPriority("minecraft:birch_log", 60),
                        new CameraAimAssistPriority("minecraft:spruce_log", 60),
                        new CameraAimAssistPriority("minecraft:acacia_log", 60),
                        new CameraAimAssistPriority("minecraft:jungle_log", 60),
                        new CameraAimAssistPriority("minecraft:dark_oak_log", 60),
                        new CameraAimAssistPriority("minecraft:mangrove_log", 60)
                )
        );
        EMPTY_HAND_CATEGORY.setEntityDefault(30);
        EMPTY_HAND_CATEGORY.setBlockDefault(30);

        DEFAULT_CATEGORY = new CameraAimAssistCategory();
        DEFAULT_CATEGORY.setName("minecraft:default");
        DEFAULT_CATEGORY.setBlocks(
                List.of(
                        new CameraAimAssistPriority("minecraft:lever", 60),
                        new CameraAimAssistPriority("minecraft:oak_button", 60),
                        new CameraAimAssistPriority("minecraft:birch_button", 60),
                        new CameraAimAssistPriority("minecraft:spruce_button", 60),
                        new CameraAimAssistPriority("minecraft:dark_oak_button", 60)
                )
        );

        AIM_ASSIST_CATEGORIES.add(BUCKET_CATEGORY);
        AIM_ASSIST_CATEGORIES.add(EMPTY_HAND_CATEGORY);
        AIM_ASSIST_CATEGORIES.add(DEFAULT_CATEGORY);
    }

    public static Set<CameraAimAssistPresetDefinition> getAllPresets() {
        return AIM_ASSIST_PRESETS;
    }

    public static Set<CameraAimAssistCategory> getAllCategories() {
        return AIM_ASSIST_CATEGORIES;
    }
}