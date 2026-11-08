package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.FloatNode;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.tree.node.RelativeFloatNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Position;
import org.powernukkitx.utils.DefaultCameraPresets;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.EasingFunction;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.CameraFadeInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.CameraSetInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.ColorOption;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.EaseOption;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.PosOption;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.RotOption;
import org.cloudburstmc.protocol.bedrock.data.camera.instruction.TimeOption;
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

    public static final String[] EASE_TYPES = Arrays.stream(EasingFunction.values()).map(EasingFunction::getSerializeName).toArray(String[]::new);

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

    private NamedDefinition findPreset(String name) {
        return DefaultCameraPresets.getDefinition(name);
    }

    private static CameraSetInstruction setInstruction(NamedDefinition preset) {
        CameraSetInstruction set = new CameraSetInstruction();
        set.setPreset(preset);
        return set;
    }

    private static EaseOption easeOption(EasingFunction type, float time) {
        EaseOption ease = new EaseOption();
        ease.setType(type);
        ease.setTime(time);
        return ease;
    }

    private static PosOption posOption(Position position) {
        PosOption pos = new PosOption();
        pos.setPos(Vector3f.from((float) position.getX(), (float) position.getY(), (float) position.getZ()));
        return pos;
    }

    private static RotOption rotOption(float pitch, float yaw) {
        RotOption rot = new RotOption();
        rot.setX(pitch);
        rot.setY(yaw);
        return rot;
    }

    // command takes 0..255 per channel, the client wants 0..1
    private static ColorOption colorOption(int red, int green, int blue) {
        ColorOption color = new ColorOption();
        color.setRed(red / 255f);
        color.setGreen(green / 255f);
        color.setBlue(blue / 255f);
        return color;
    }

    private static TimeOption timeOption(float fadeIn, float hold, float fadeOut) {
        TimeOption time = new TimeOption();
        time.setFadeInTime(fadeIn);
        time.setHoldTime(hold);
        time.setFadeOutTime(fadeOut);
        return time;
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
            case "clear" -> pk.getCameraInstruction().setClear(OptionalBoolean.of(true));
            case "fade" -> pk.getCameraInstruction().setFade(new CameraFadeInstruction());
            case "fade-color" -> {
                CameraFadeInstruction fade = new CameraFadeInstruction();
                fade.setColor(colorOption(getInteger(list, 3), getInteger(list, 4), getInteger(list, 5)));
                pk.getCameraInstruction().setFade(fade);
            }
            case "fade-time-color" -> {
                CameraFadeInstruction fade = new CameraFadeInstruction();
                fade.setTime(timeOption(list.get(3).get(), list.get(4).get(), list.get(5).get()));
                fade.setColor(colorOption(getInteger(list, 7), getInteger(list, 8), getInteger(list, 9)));
                pk.getCameraInstruction().setFade(fade);
            }
            case "set-default" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                CameraSetInstruction set = setInstruction(preset);
                set.setDefaultValue(OptionalBoolean.of(true));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                CameraSetInstruction set = setInstruction(preset);
                set.setRot(rotOption(((RelativeFloatNode) list.get(4)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(5)).get((float) senderLocation.getYaw())));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-pos" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                CameraSetInstruction set = setInstruction(preset);
                set.setPos(posOption(position));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-pos-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                CameraSetInstruction set = setInstruction(preset);
                set.setPos(posOption(position));
                set.setRot(rotOption(((RelativeFloatNode) list.get(6)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(7)).get((float) senderLocation.getYaw())));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-ease-default" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingFunction.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                CameraSetInstruction set = setInstruction(preset);
                set.setEase(easeOption(easeType, easeTime));
                set.setDefaultValue(OptionalBoolean.of(true));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-ease-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingFunction.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                CameraSetInstruction set = setInstruction(preset);
                set.setEase(easeOption(easeType, easeTime));
                set.setRot(rotOption(((RelativeFloatNode) list.get(7)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(8)).get((float) senderLocation.getYaw())));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-ease-pos" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingFunction.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                CameraSetInstruction set = setInstruction(preset);
                set.setEase(easeOption(easeType, easeTime));
                set.setPos(posOption(position));
                pk.getCameraInstruction().setSet(set);
            }
            case "set-ease-pos-rot" -> {
                var preset = findPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EasingFunction.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                CameraSetInstruction set = setInstruction(preset);
                set.setEase(easeOption(easeType, easeTime));
                set.setPos(posOption(position));
                set.setRot(rotOption(((RelativeFloatNode) list.get(9)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(10)).get((float) senderLocation.getYaw())));
                pk.getCameraInstruction().setSet(set);
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
