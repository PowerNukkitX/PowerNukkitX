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
            this.next = stage;//next -> null
        } else {
            this.next.next(stage);//next -> next
        }
    }

    @Override
    public String toString() {
        return name();
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
