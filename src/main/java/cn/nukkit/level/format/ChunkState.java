package cn.nukkit.level.format;

/**
 * Allay Project 2023/9/10
 *
 * @author daoge_cmd
 */
public enum ChunkState {
    NEW,
    GENERATED,
    POPULATED,
    FINISHED;

    public boolean canSend() {
        return this.ordinal() >= 2;
    }
}
