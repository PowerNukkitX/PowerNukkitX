package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.FloatNode;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.tree.node.RelativeFloatNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Position;
import cn.nukkit.utils.DefaultCameraPresets;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraFadeInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSetInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.EasingType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.protocol.bedrock.packet.CameraInstructionPacket;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */

public class CameraCommand extends VanillaCommand {

    public static final String[] EASE_TYPES = Arrays.stream(EasingType.values()).map(EasingType::getSerializeName).toArray(String[]::new);

    public CameraCommand(String name) {
        super(name, "commands.camera.description");
        this.setPermission("nukkit.command.camera");
        this.commandParameters.clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("clear", false, new String[]{"clear"})
        });
        this.commandParameters.put("fade", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"})
        });
        this.commandParameters.put("fade-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.INT),
                CommandParameter.newType("green", false, CommandParamType.INT),
                CommandParameter.newType("blue", false, CommandParamType.INT)
        });
        this.commandParameters.put("fade-time-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("time", false, new String[]{"time"}),
                CommandParameter.newType("fadeInSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("holdSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("fadeOutSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.INT),
                CommandParameter.newType("green", false, CommandParamType.INT),
                CommandParameter.newType("blue", false, CommandParamType.INT)
        });
        this.commandParameters.put("set-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("default", true, new String[]{"default"})
        });
        this.commandParameters.put("set-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VAL, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VAL, new RelativeFloatNode())
        });
        this.commandParameters.put("set-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
        });
        this.commandParameters.put("set-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VAL, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VAL, new RelativeFloatNode())
        });
        this.commandParameters.put("set-ease-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("default", true, new String[]{"default"})
        });
        this.commandParameters.put("set-ease-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VAL, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VAL, new RelativeFloatNode())
        });
        this.commandParameters.put("set-ease-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
        });
        this.commandParameters.put("set-ease-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VAL, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VAL, new RelativeFloatNode())
        });
        this.enableParamTree();
    }

    private int getInteger(ParamList list, int index) {
        return list.get(index).get();
    }

    private CameraPreset findPreset(String name) {
        for (CameraPreset preset : DefaultCameraPresets.getAll()) {
            if (preset.getName().equals(name)) {
                return preset;
            }
        }
        return null;
    }

    private NamedDefinition asDefinition(CameraPreset preset) {
        final String identifier = preset.getName();
        return new NamedDefinition() {
            @Override
            public String getIdentifier() {
                return identifier;
            }

            @Override
            public int getRuntimeId() {
                return 0;
            }
        };
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        var playerNames = players.stream().map(Player::getName).reduce((a, b) -> a + " " + b).orElse("");
        var pk = new CameraInstructionPacket();
        var senderLocation = sender.getLocation();
        switch (result.getKey()) {
            case "clear" -> pk.setClear(OptionalBoolean.of(true));
            case "fade" -> pk.setFadeInstruction(new CameraFadeInstruction());
            case "fade-color" -> {
                CameraFadeInstruction fade = new CameraFadeInstruction();
                fade.setColor(new CameraFadeInstruction.ColorOption(getInteger(list, 3) / 255f, getInteger(list, 4) / 255f, getInteger(list, 5) / 255f));
                pk.setFadeInstruction(fade);
            }
            case "fade-time-color" -> {
                CameraFadeInstruction fade = new CameraFadeInstruction();
                fade.setTime(new CameraFadeInstruction.TimeOption(list.get(3).get(), list.get(4).get(), list.get(5).get()));
                fade.setColor(new CameraFadeInstruction.ColorOption(getInteger(list, 7) / 255f, getInteger(list, 8) / 255f, getInteger(list, 9) / 255f));
                pk.setFadeInstruction(fade);
            }
            case "set-default" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .defaultPreset(OptionalBoolean.of(true))
                        .build());
            }
            case "set-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .rot(Vector2f.from(((RelativeFloatNode) list.get(4)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(5)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-pos" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .pos(Vector3f.from((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .build());
            }
            case "set-pos-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .pos(Vector3f.from((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .rot(Vector2f.from(((RelativeFloatNode) list.get(6)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(7)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-ease-default" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .ease(new CameraSetInstruction.EaseOption(easeType, easeTime))
                        .defaultPreset(OptionalBoolean.of(true))
                        .build());
            }
            case "set-ease-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .ease(new CameraSetInstruction.EaseOption(easeType, easeTime))
                        .rot(Vector2f.from(((RelativeFloatNode) list.get(7)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(8)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-ease-pos" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .ease(new CameraSetInstruction.EaseOption(easeType, easeTime))
                        .pos(Vector3f.from((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .build());
            }
            case "set-ease-pos-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                pk.setSetInstruction(CameraSetInstruction.builder()
                        .preset(asDefinition(preset))
                        .ease(new CameraSetInstruction.EaseOption(easeType, easeTime))
                        .pos(Vector3f.from((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .rot(Vector2f.from(((RelativeFloatNode) list.get(9)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(10)).get((float) senderLocation.getYaw())))
                        .build());
            }
            default -> {
                return 0;
            }
        }
        for (Player player : players) {
            player.sendPacket(pk);
        }
        log.addSuccess("commands.camera.success", playerNames).output();
        return 1;
    }
}
