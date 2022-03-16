package cn.powernukkitx.bootstrap.gui.model.values;

import com.jediterm.terminal.ProcessTtyConnector;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.model.CharBuffer;
import com.jediterm.terminal.model.TerminalTextBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class TerminalTty extends ProcessTtyConnector {
    private final Terminal terminal;
    private final TerminalTextBuffer terminalTextBuffer;
    private final StringBuffer stringBuffer;

    public TerminalTty(@NotNull Process process, @NotNull Terminal terminal, @NotNull TerminalTextBuffer terminalTextBuffer) {
        super(process, StandardCharsets.UTF_8);
        this.terminal = terminal;
        this.terminalTextBuffer = terminalTextBuffer;
        this.stringBuffer = new StringBuffer();
    }

    @Override
    public String getName() {
        return "PowerNukkitX";
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        if (bytes.length > 0) {
            if (bytes[0] == 13) {
                super.write(stringBuffer.toString().getBytes(myCharset));
                super.write(bytes);
                terminal.cursorBackward(stringBuffer.length());
                stringBuffer.delete(0, stringBuffer.length());
                terminal.deleteLines(1);
            } else if (bytes[0] == 127) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                terminal.cursorBackward(1);
                terminal.deleteCharacters(1);
            } else {
                terminal.cursorForward(1);
                terminalTextBuffer.writeString(0, terminalTextBuffer.getHeight(), new CharBuffer(stringBuffer.toString()));
            }
        }
    }

    @Override
    public void write(String string) throws IOException {
        stringBuffer.append(string);
        super.write(string);
    }
}
