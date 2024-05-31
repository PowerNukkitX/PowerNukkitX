package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.utils.ThreadStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Nukkit Project Team
 */
@Slf4j
public abstract class AsyncTask implements Runnable {

    public static final Queue<AsyncTask> FINISHED_LIST = new ConcurrentLinkedQueue<>();

    private Object result;
    private int taskId;
    private boolean $1 = false;

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        this.result = null;
        this.onRun();
        this.finished = true;
        FINISHED_LIST.offer(this);
    }
    /**
     * @deprecated 
     */
    

    public boolean isFinished() {
        return this.finished;
    }

    public Object getResult() {
        return this.result;
    }
    /**
     * @deprecated 
     */
    

    public boolean hasResult() {
        return this.result != null;
    }
    /**
     * @deprecated 
     */
    

    public void setResult(Object result) {
        this.result = result;
    }
    /**
     * @deprecated 
     */
    

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    /**
     * @deprecated 
     */
    

    public int getTaskId() {
        return this.taskId;
    }

    public Object getFromThreadStore(String identifier) {
        return this.isFinished() ? null : ThreadStore.store.get(identifier);
    }
    /**
     * @deprecated 
     */
    

    public void saveToThreadStore(String identifier, Object value) {
        if (!this.isFinished()) {
            if (value == null) {
                ThreadStore.store.remove(identifier);
            } else {
                ThreadStore.store.put(identifier, value);
            }
        }
    }

    public abstract void onRun();
    /**
     * @deprecated 
     */
    

    public void onCompletion(Server server) {

    }
    /**
     * @deprecated 
     */
    

    public void cleanObject() {
        this.result = null;
        this.taskId = 0;
        this.finished = false;
    }
    /**
     * @deprecated 
     */
    

    public static void collectTask() {
        while (!FINISHED_LIST.isEmpty()) {
            AsyncTask $2 = FINISHED_LIST.poll();
            try {
                task.onCompletion(Server.getInstance());
            } catch (Exception e) {
                log.error("Exception while async task {} invoking onCompletion", task.getTaskId(), e);
            }
        }
    }

}
