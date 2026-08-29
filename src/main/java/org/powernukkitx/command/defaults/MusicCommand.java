package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.MusicRepeatMode;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Map;

public class MusicCommand extends VanillaCommand {

    private static final float MAX_FADE_SECONDS = 10;

    public MusicCommand(String name) {
        super(name, "commands.music.description");
        this.setPermission("nukkit.command.music");
        this.commandParameters.clear();
        this.commandParameters.put("play", new CommandParameter[]{
                CommandParameter.newEnum("play", false, new String[]{"play"}),
                CommandParameter.newType("trackName", false, CommandParamType.ID),
                CommandParameter.newType("volume", true, CommandParamType.FLOAT),
                CommandParameter.newType("fadeSeconds", true, CommandParamType.FLOAT),
                CommandParameter.newEnum("repeatMode", true, new CommandEnum("MusicRepeatMode", MusicRepeatMode.PLAY_ONCE.getName(), MusicRepeatMode.LOOP.getName()))
        });
        this.commandParameters.put("queue", new CommandParameter[]{
                CommandParameter.newEnum("queue", false, new String[]{"queue"}),
                CommandParameter.newType("trackName", false, CommandParamType.ID),
                CommandParameter.newType("volume", true, CommandParamType.FLOAT),
                CommandParameter.newType("fadeSeconds", true, CommandParamType.FLOAT),
                CommandParameter.newEnum("repeatMode", true, new CommandEnum("MusicRepeatMode", MusicRepeatMode.PLAY_ONCE.getName(), MusicRepeatMode.LOOP.getName()))
        });
        this.commandParameters.put("stop", new CommandParameter[]{
                CommandParameter.newEnum("stop", false, new String[]{"stop"}),
                CommandParameter.newType("fadeSeconds", true, CommandParamType.FLOAT)
        });
        this.commandParameters.put("volume", new CommandParameter[]{
                CommandParameter.newEnum("volume", false, new String[]{"volume"}),
                CommandParameter.newType("volume", false, CommandParamType.FLOAT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Level level = sender.getPosition().getLevel();
        switch (result.getKey()) {
            case "play", "queue" -> {
                String trackName = list.getResult(1);
                if (trackName.isEmpty()) {
                    log.addError("commands.music.failure.emptyTrackName").output();
                    return 0;
                }

                float volume = 1;
                float fadeSeconds = 0;
                MusicRepeatMode repeatMode = MusicRepeatMode.PLAY_ONCE;
                if (list.hasResult(2)) volume = list.getResult(2);
                if (list.hasResult(3)) fadeSeconds = list.getResult(3);
                if (list.hasResult(4)) {
                    MusicRepeatMode mode = MusicRepeatMode.byName(list.getResult(4));
                    if (mode == null) {
                        log.addSyntaxErrors(4).output();
                        return 0;
                    }
                    repeatMode = mode;
                }
                if (!this.checkVolume(volume, 2, log) || !this.checkFadeSeconds(fadeSeconds, 3, log)) {
                    return 0;
                }

                if (result.getKey().equals("play")) {
                    level.playMusic(trackName, volume, fadeSeconds, repeatMode);
                    log.addSuccess("commands.music.success.playAction", trackName).output();
                } else {
                    level.queueMusic(trackName, volume, fadeSeconds, repeatMode);
                    log.addSuccess("commands.music.success.queueAction", trackName).output();
                }
                return 1;
            }
            case "stop" -> {
                float fadeSeconds = 0;
                if (list.hasResult(1)) fadeSeconds = list.getResult(1);
                if (!this.checkFadeSeconds(fadeSeconds, 1, log)) {
                    return 0;
                }

                level.stopMusic(fadeSeconds);
                log.addSuccess("commands.music.success.stopAction").output();
                return 1;
            }
            case "volume" -> {
                float volume = list.getResult(1);
                if (!this.checkVolume(volume, 1, log)) {
                    return 0;
                }

                level.setMusicVolume(volume);
                log.addSuccess("commands.music.success.volumeAction", String.valueOf(volume)).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private boolean checkVolume(float volume, int errorIndex, CommandLogger log) {
        if (volume < 0) {
            log.addDoubleTooSmall(errorIndex, 0).output();
            return false;
        }
        if (volume > 1) {
            log.addDoubleTooBig(errorIndex, 1).output();
            return false;
        }
        return true;
    }

    private boolean checkFadeSeconds(float fadeSeconds, int errorIndex, CommandLogger log) {
        if (fadeSeconds < 0) {
            log.addDoubleTooSmall(errorIndex, 0).output();
            return false;
        }
        if (fadeSeconds > MAX_FADE_SECONDS) {
            log.addDoubleTooBig(errorIndex, MAX_FADE_SECONDS).output();
            return false;
        }
        return true;
    }
}
