package threads;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BotExecutorService {
    public static BotExecutorService INSTANCE;
    private ScheduledExecutorService service;

    public BotExecutorService() {
        this.service = new ScheduledThreadPoolExecutor(2);
    }

    public ScheduledFuture<?> start(Runnable task, long milliseconds) {
        return this.service.schedule(task, milliseconds, TimeUnit.MILLISECONDS);
    }

    public static BotExecutorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BotExecutorService();
        }
        return INSTANCE;
    }
}
