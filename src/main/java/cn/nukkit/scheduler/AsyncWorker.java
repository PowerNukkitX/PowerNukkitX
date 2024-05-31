package cn.nukkit.scheduler;

import cn.nukkit.InterruptibleThread;

import java.util.LinkedList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AsyncWorker extends Thread implements InterruptibleThread {
    private final LinkedList<AsyncTask> stack = new LinkedList<>();
    /**
     * @deprecated 
     */
    

    public AsyncWorker() {
        this.setName("Asynchronous Worker");
    }
    /**
     * @deprecated 
     */
    

    public void stack(AsyncTask task) {
        synchronized (stack) {
            stack.addFirst(task);
        }
    }
    /**
     * @deprecated 
     */
    

    public void unstack() {
        synchronized (stack) {
            stack.clear();
        }
    }
    /**
     * @deprecated 
     */
    

    public void unstack(AsyncTask task) {
        synchronized (stack) {
            stack.remove(task);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        while (true) {
            synchronized (stack) {
                for (AsyncTask task : stack) {
                    if (!task.isFinished()) {
                        task.run();
                    }
                }
            }
            try {
                sleep(5);
            } catch (InterruptedException e) {
                //igonre
            }
        }
    }

}
