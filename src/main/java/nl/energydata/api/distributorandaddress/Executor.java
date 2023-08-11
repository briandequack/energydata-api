package nl.energydata.api.distributorandaddress;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;


@Service
public class Executor {
	
    private final ExecutorService executorService;

    public Executor() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public <T> Future<T> submitTask(Callable<T> task) {
        return executorService.submit(task);
    }
    
    @PreDestroy
    public void tearDown() {
        executorService.shutdown();
    }
}
