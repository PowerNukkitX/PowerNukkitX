package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.camera.data.Ease;
import cn.nukkit.camera.data.EaseType;
import cn.nukkit.camera.data.Time;
import cn.nukkit.camera.instruction.impl.ClearInstruction;
import cn.nukkit.camera.instruction.impl.FadeInstruction;
import cn.nukkit.camera.instruction.impl.SetInstruction;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.FloatNode;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.tree.node.RelativeFloatNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.CameraInstructionPacket;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author daoge_cmd <br>
 * Date: 2023/6/11 <br>
 * PowerNukkitX Project <br>
 * TODO: 此命令的多语言文本似乎不能正常工作
 */


public class CameraCommand extends VanillaCommand {

    public static final String[] EASE_TYPES = Arrays.stream(EaseType.values()).map(EaseType::getType).toArray(String[]::new);

    public CameraCommand(String name) {
        super(name, "commands.camera.description");
        this.setPermission("nukkit.command.camera");
        this.commandParameters.clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("clear", false, new String[]{"clear"})
        });
        this.commandParameters.put("fade", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"})
        });
        this.commandParameters.put("fade-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.FLOAT),
                CommandParameter.newType("green", false, CommandParamType.FLOAT),
                CommandParameter.newType("blue", false, CommandParamType.FLOAT)
        });
        this.commandParameters.put("fade-time-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("time", false, new String[]{"time"}),
                CommandParameter.newType("fadeInSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("holdSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("fadeOutSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.FLOAT),
                CommandParameter.newType("green", false, CommandParamType.FLOAT),
                CommandParameter.newType("blue", false, CommandParamType.FLOAT)
        });
        this.commandParameters.put("set-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("default", true, new String[]{"default"})
        });
        this.commandParameters.put("set-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE, new RelativeFloatNode())
        });
        this.commandParameters.put("set-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
        });
        this.commandParameters.put("set-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE, new RelativeFloatNode())
        });
        this.commandParameters.put("set-ease-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("default", true, new String[]{"default"})
        });
        this.commandParameters.put("set-ease-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE, new RelativeFloatNode())
        });
        this.commandParameters.put("set-ease-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
        });
        this.commandParameters.put("set-ease-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT, new FloatNode()),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE, new RelativeFloatNode()),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE, new RelativeFloatNode())
        });
        this.enableParamTree();
    }

    private static float getFloat(ParamList list, int index) {
        return list.get(index).get();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        var playerNames = players.stream().map(Player::getName).reduce((a, b) -> a + " " + b).orElse("");
        var pk = new CameraInstructionPacket();
        var senderLocation = sender.getLocation();
        switch (result.getKey()) {
            case "clear" -> {
                pk.setInstruction(ClearInstruction.get());
            }
            case "fade" -> {
                pk.setInstruction(FadeInstruction.builder().build());
            }
            case "fade-color" -> {
                pk.setInstruction(FadeInstruction
                        .builder()
                        .color(new Color(getFloat(list, 3), getFloat(list, 4), getFloat(list, 5)))
                        .build());
            }
            case "fade-time-color" -> {
                pk.setInstruction(FadeInstruction
                        .builder()
                        .time(new Time(list.get(3).get(), list.get(4).get(), list.get(5).get()))
                        .color(new Color(getFloat(list, 7), getFloat(list, 8), getFloat(list, 9)))
                        .build());
            }
            case "set-default" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                pk.setInstruction(SetInstruction.builder().preset(preset).build());
            }
            case "set-rot" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .rot(new Vector2f(((RelativeFloatNode) list.get(4)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(5)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-pos" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .pos(new Vector3f((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .build());
            }
            case "set-pos-rot" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                Position position = list.get(4).get();
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .pos(new Vector3f((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .rot(new Vector2f(((RelativeFloatNode) list.get(6)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(7)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-ease-default" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EaseType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .ease(new Ease(easeTime, easeType))
                        .build());
            }
            case "set-ease-rot" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EaseType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .ease(new Ease(easeTime, easeType))
                        .rot(new Vector2f(((RelativeFloatNode) list.get(7)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(8)).get((float) senderLocation.getYaw())))
                        .build());
            }
            case "set-ease-pos" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EaseType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .ease(new Ease(easeTime, easeType))
                        .pos(new Vector3f((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .build());
            }
            case "set-ease-pos-rot" -> {
                var preset = CameraPreset.getPreset(list.get(2).get());
                if (preset == null) {
                    log.addError("commands.camera.invalid-preset").output();
                    return 0;
                }
                float easeTime = list.get(4).get();
                var easeType = EaseType.valueOf(((String) list.get(5).get()).toUpperCase(Locale.ENGLISH));
                Position position = list.get(7).get();
                pk.setInstruction(SetInstruction.builder()
                        .preset(preset)
                        .ease(new Ease(easeTime, easeType))
                        .pos(new Vector3f((float) position.getX(), (float) position.getY(), (float) position.getZ()))
                        .rot(new Vector2f(((RelativeFloatNode) list.get(9)).get((float) senderLocation.getPitch()), ((RelativeFloatNode) list.get(10)).get((float) senderLocation.getYaw())))
                        .build());
            }
            default -> {
                return 0;
            }
        }
        for (Player player : players) {
            player.dataPacket(pk);
        }
        log.addSuccess("commands.camera.success", playerNames).output();
        return 1;
    }
}
