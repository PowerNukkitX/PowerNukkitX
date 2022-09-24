package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MultipleExecutor implements IBehaviorExecutor {

    protected Set<IBehaviorExecutor> executors;

    public MultipleExecutor(@NotNull Set<IBehaviorExecutor> executors) {
        this.executors = executors;
    }

    public MultipleExecutor(@NotNull IBehaviorExecutor... executors) {
        this.executors = Set.of(executors);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        var tasks = new ArrayList<CompletableFuture>();
        for (IBehaviorExecutor executor : executors) {
            tasks.add(CompletableFuture.supplyAsync(() -> executor.execute(entity)));
        }
        try {
            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[executors.size()])).whenComplete((s, t) -> {
                if (t != null) {
                    System.out.println("阶段执行过程中存在异常：");
                    t.printStackTrace();
                }
            }).thenApply(v -> tasks.stream().map(task -> {
                try {
                    return (Boolean) task.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).reduce(false, (a, b) -> a || b)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        IBehaviorExecutor.super.onInterrupt(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        IBehaviorExecutor.super.onStop(entity);
    }
}
