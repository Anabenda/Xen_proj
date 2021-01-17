package com.cjs.proj.config;

import com.cjs.proj.entity.TaskBase;
import com.cjs.proj.interf.impl.DelayTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class DelayQueueManager implements CommandLineRunner {

    private DelayQueue<DelayTask> delayQueue = new DelayQueue<>();

    /**
     * 加入到延时队列中
     * @param task
     */
    public void put(DelayTask task) {
        delayQueue.put(task);
    }

    /**
     * 取消延时任务
     * @param task
     * @return
     */
    public boolean remove(DelayTask task) {
        return delayQueue.remove(task);
    }

    /**
     * 取消延时任务   内部是用uuid判断的
     * @param
     * @return
     */
    public boolean remove(String gpuIp, String uuid) {
        return remove(new DelayTask(new TaskBase(gpuIp, uuid, null, null), 0));
    }

    @Override
    public void run(String... args) throws Exception {
        Executors.newSingleThreadExecutor().execute(new Thread(this::excuteThread));
    }

    /**
     * 延时任务执行线程
     */
    private void excuteThread() {
        while (true) {
            try {
                DelayTask task = delayQueue.take();
                processTask(task);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 内部执行延时任务
     * @param task
     */
    private void processTask(DelayTask task) throws Exception {
        // 要处理的业务逻辑
        task.getData().shutDown();
    }
}