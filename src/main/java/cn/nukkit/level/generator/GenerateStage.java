package cn.nukkit.level.generator;

import cn.nukkit.Server;
import com.google.common.base.Preconditions;

import java.util.concurrent.Executor;

public abstract class GenerateStage {
    private GenerateStage next;

    public abstract void apply(ChunkGenerateContext context);

    public abstract String name();

    public Executor getExecutor() {
        return Server.getInstance().getComputeThreadPool();
    }

    public final GenerateStage getNextStage() {
        return next;
    }

    private void next(GenerateStage stage) {
        if (this.next == null) {
            this.next = stage;
        } else {
            this.next.next(stage);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name());
        GenerateStage next = this.next;
        while (next != null) {
            builder.append(next.name());
            next = next.next;
        }
        return builder.toString();
    }

    public static class Builder {
        private GenerateStage start;
        private GenerateStage end;

        Builder() {
        }

        public Builder start(GenerateStage start) {
            this.start = start;
            end = start;
            return this;
        }

        public Builder next(GenerateStage next) {
            start.next(next);
            end = next;
            return this;
        }

        public GenerateStage getStart() {
            Preconditions.checkNotNull(start, "you must be set start generate stage!");
            return start;
        }

        public GenerateStage getEnd() {
            return end;
        }
    }
}
