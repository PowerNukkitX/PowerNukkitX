package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.CameraInstructionPacket;
import cn.nukkit.network.protocol.CameraPresetsPacket;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.50-r1")
public class DebugCommand extends TestCommand implements CoreCommand {
    public DebugCommand(String name) {
        super(name, "commands.debug.description");
        this.setPermission("nukkit.command.debug");
        this.commandParameters.clear();
        //生物AI debug模式开关
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity", new String[]{"entity"}),
                CommandParameter.newEnum("option", Arrays.stream(EntityAI.DebugOption.values()).map(option -> option.name().toLowerCase()).toList().toArray(new String[0])),
                CommandParameter.newEnum("value", false, CommandEnum.ENUM_BOOLEAN)
        });
        //Camera debug
        this.commandParameters.put("camera", new CommandParameter[]{
                CommandParameter.newEnum("camera", new String[]{"camera"}),
        });
        this.commandParameters.put("camerafade", new CommandParameter[]{
                CommandParameter.newEnum("camerafade", new String[]{"camerafade"}),
        });
        this.commandParameters.put("camerapreset", new CommandParameter[]{
                CommandParameter.newEnum("camerapreset", new String[]{"camerapreset"}),
        });
        this.enableParamTree();
    }

    @Since("1.19.60-r1")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "entity" -> {
                String str = list.getResult(1);
                var option = EntityAI.DebugOption.valueOf(str.toUpperCase(Locale.ENGLISH));
                boolean value = list.getResult(2);
                EntityAI.setDebugOption(option, value);
                log.addSuccess("Entity AI framework " + option.name() + " debug mode have been set to: " + EntityAI.checkDebugOption(option)).output();
                return 1;
            }
            case "camera" -> {
                var set = new CompoundTag("set");
                set.putInt("preset", 0);
                var data = new CompoundTag();
                data.putCompound(set);
                var pk = new CameraInstructionPacket();
                pk.setData(data);
                sender.asPlayer().dataPacket(pk);
                return 1;
            }
            case "camerafade" -> {
                /**
                 * "fade": {
                 *     "color": {
                 *       "b": 1.0f,
                 *       "g": 1.0f,
                 *       "r": 1.0f
                 *     }
                 *   }
                 */
                var data = new CompoundTag();
                var fade = new CompoundTag("fade");
                var color = new CompoundTag("color");
                color.putFloat("r", 1.0f);
                color.putFloat("g", 1.0f);
                color.putFloat("b", 1.0f);
                fade.putCompound(color);
                data.putCompound(fade);
                var pk = new CameraInstructionPacket();
                pk.setData(data);
                sender.asPlayer().dataPacket(pk);
                return 1;
            }
            case "camerapreset" -> {
                var data = new CompoundTag();
                var presets = new ListTag<>("presets");
                var free = new CompoundTag();
                free.putString("identifier", "minecraft:free");
                free.putString("inherit_from", "");
                free.putFloat("pos_x", 0);
                free.putFloat("pos_y", 0);
                free.putFloat("pos_z", 0);
                free.putFloat("rot_x", 0);
                free.putFloat("rot_y", 0);
                presets.add(0, free);
                data.putList(presets);
                var pk = new CameraPresetsPacket();
                pk.setData(data);
                sender.asPlayer().dataPacket(pk);
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
