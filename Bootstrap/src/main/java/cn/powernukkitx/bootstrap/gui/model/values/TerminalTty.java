package cn.powernukkitx.bootstrap.gui.model.values;

import com.jediterm.terminal.ProcessTtyConnector;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public final class TerminalTty extends ProcessTtyConnector {

    public TerminalTty(@NotNull Process process) {
        super(process, StandardCharsets.UTF_8);
    }

    @Override
    public String getName() {
        return "PowerNukkitX";
    }
}
