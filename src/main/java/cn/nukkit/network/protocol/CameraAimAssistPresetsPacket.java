package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistCategories;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistCategory;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistCategoryPriorities;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistPreset;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistPresetsPacketOperation;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraAimAssistPresetsPacket extends DataPacket {
    private final List<CameraAimAssistCategories> categories = new ObjectArrayList<>();
    private final List<CameraAimAssistPreset> presets = new ObjectArrayList<>();
    private CameraAimAssistPresetsPacketOperation cameraAimAssistPresetsPacketOperation;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.categories.addAll(List.of(byteBuf.readArray(CameraAimAssistCategories.class, this::readCategories)));
        this.presets.addAll(List.of(byteBuf.readArray(CameraAimAssistPreset.class, this::readPreset)));
        this.cameraAimAssistPresetsPacketOperation = CameraAimAssistPresetsPacketOperation.VALUES[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(categories, this::writeCategories);
        byteBuf.writeArray(presets, this::writeCameraAimAssist);
        byteBuf.writeByte(cameraAimAssistPresetsPacketOperation.ordinal());
    }

    private void writeCategories(HandleByteBuf byteBuf, CameraAimAssistCategories categories) {
        byteBuf.writeString(categories.getIdentifier());
        byteBuf.writeArray(categories.getCategories(), this::writeCategory);
    }

    private void writeCategory(HandleByteBuf byteBuf, CameraAimAssistCategory category) {
        byteBuf.writeString(category.getName());
        writePriorities(byteBuf, category.getPriorities());
    }

    private void writePriorities(HandleByteBuf byteBuf, CameraAimAssistCategoryPriorities priorities) {
        byteBuf.writeArray(priorities.entities.entrySet(), this::writePriority);
        byteBuf.writeArray(priorities.blocks.entrySet(), this::writePriority);
    }

    private void writePriority(HandleByteBuf byteBuf, Map.Entry<String, Integer> priority) {
        byteBuf.writeString(priority.getKey());
        byteBuf.writeIntLE(priority.getValue());
    }

    private void writeCameraAimAssist(HandleByteBuf byteBuf, CameraAimAssistPreset preset) {
        byteBuf.writeString(preset.getIdentifier());
        byteBuf.writeString(preset.getCategories());
        byteBuf.writeArray(preset.getExclusionList(), byteBuf::writeString);
        byteBuf.writeArray(preset.getLiquidTargetingList(), byteBuf::writeString);
        byteBuf.writeArray(preset.getItemSettings().entrySet(), this::writeItemSetting);
        byteBuf.writeOptional(preset.getDefaultItemSettings(), byteBuf::writeString);
        byteBuf.writeOptional(preset.getHandSettings(), byteBuf::writeString);
    }

    private void writeItemSetting(HandleByteBuf byteBuf, Map.Entry<String, String> itemSetting) {
        byteBuf.writeString(itemSetting.getKey());
        byteBuf.writeString(itemSetting.getValue());
    }

    // READ
    private CameraAimAssistCategories readCategories(HandleByteBuf byteBuf) {
        CameraAimAssistCategories categories = new CameraAimAssistCategories();
        categories.setIdentifier(byteBuf.readString());
        int categoryLength = byteBuf.readUnsignedVarInt();
        for(int i = 0; i < categoryLength; i++) {
            categories.getCategories().add(readCategory(byteBuf));
        }
        return categories;
    }

    private CameraAimAssistCategory readCategory(HandleByteBuf byteBuf) {
        CameraAimAssistCategory category = new CameraAimAssistCategory();
        category.setName(byteBuf.readString());
        category.setPriorities(readPriorities(byteBuf));
        return category;
    }

    private CameraAimAssistCategoryPriorities readPriorities(HandleByteBuf byteBuf) {
        CameraAimAssistCategoryPriorities priorities = new CameraAimAssistCategoryPriorities();
        int entityPriorityLength = byteBuf.readUnsignedVarInt();
        for(int i = 0; i < entityPriorityLength; i++) {
            Map.Entry<String, Integer> entry = readPriority(byteBuf);
            priorities.getEntities().put(entry.getKey(), entry.getValue());
        }
        int blockPriorityLength = byteBuf.readUnsignedVarInt();
        for(int i = 0; i < blockPriorityLength; i++) {
            Map.Entry<String, Integer> entry = readPriority(byteBuf);
            priorities.getBlocks().put(entry.getKey(), entry.getValue());
        }
        return priorities;
    }

    private Map.Entry<String, Integer> readPriority(HandleByteBuf byteBuf) {
        return Map.entry(byteBuf.readString(), byteBuf.readIntLE());
    }

    private CameraAimAssistPreset readPreset(HandleByteBuf byteBuf) {
        CameraAimAssistPreset preset = new CameraAimAssistPreset();
        preset.setIdentifier(byteBuf.readString());
        preset.setCategories(byteBuf.readString());
        preset.getExclusionList().addAll(List.of(byteBuf.readArray(String.class, HandleByteBuf::readString)));
        preset.getLiquidTargetingList().addAll(List.of(byteBuf.readArray(String.class, HandleByteBuf::readString)));
        int itemSettingsLength = byteBuf.readUnsignedVarInt();
        for(int i = 0; i < itemSettingsLength; i++) {
            Map.Entry<String, String> entry = readItemSetting(byteBuf);
            preset.getItemSettings().put(entry.getKey(), entry.getValue());
        }
        preset.setDefaultItemSettings(OptionalValue.of(byteBuf.readOptional(null, byteBuf::readString)));
        preset.setHandSettings(OptionalValue.of(byteBuf.readOptional(null, byteBuf::readString)));
        return preset;
    }

    private Map.Entry<String, String> readItemSetting(HandleByteBuf byteBuf) {
        return Map.entry(byteBuf.readString(), byteBuf.readString());
    }

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_AIM_ASSIST_PRESETS_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
