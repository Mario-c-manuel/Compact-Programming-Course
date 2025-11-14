package de.fhdo.sama.capstone;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationConcurrencyTest {

    @BeforeAll
    public static void initToolkit() throws Exception {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX Platform failed to start in time");
            }
        } catch (IllegalStateException e) {
            // Platform already started -> ignore
        }
    }

    // Ensure multiple "delivery" tasks run concurrently via the controller's scheduler
    @Test
    public void testDeliveryTasksConcurrency() throws Exception {
        MediTrackController controller = new MediTrackController();

        // reflectively obtain the private scheduler
        Field f = MediTrackController.class.getDeclaredField("scheduler");
        f.setAccessible(true);
        ScheduledExecutorService scheduler = (ScheduledExecutorService) f.get(controller);
        assertNotNull(scheduler);

        final int TASKS = 6;
        CountDownLatch done = new CountDownLatch(TASKS);
        AtomicInteger completed = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        // each task simulates a delivery that takes ~250ms
        for (int i = 0; i < TASKS; i++) {
            scheduler.execute(() -> {
                try {
                    Thread.sleep(250);
                    completed.incrementAndGet();
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        boolean finished = done.await(5, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - start;

        // tasks should complete and some should have run concurrently -> duration < TASKS * 250
        assertTrue(finished, "Delivery tasks did not finish in time");
        assertEquals(TASKS, completed.get(), "All delivery tasks should complete");
        assertTrue(duration < TASKS * 250L, "Tasks did not run concurrently as expected (duration=" + duration + "ms)");
    }

    // Simulate charging tasks constrained by a single charging station (Semaphore)
    @Test
    public void testChargingTasksBlockByStationCapacity() throws Exception {
        MediTrackController controller = new MediTrackController();

        Field f = MediTrackController.class.getDeclaredField("scheduler");
        f.setAccessible(true);
        ScheduledExecutorService scheduler = (ScheduledExecutorService) f.get(controller);
        assertNotNull(scheduler);

        final int TASKS = 4;
        CountDownLatch done = new CountDownLatch(TASKS);
        AtomicInteger active = new AtomicInteger(0);
        AtomicInteger maxActiveObserved = new AtomicInteger(0);

        // simulate a single charging station (capacity = 1)
        Semaphore station = new Semaphore(1);

        for (int i = 0; i < TASKS; i++) {
            scheduler.execute(() -> {
                try {
                    station.acquire();
                    int now = active.incrementAndGet();
                    // record peak concurrent chargers
                    maxActiveObserved.updateAndGet(prev -> Math.max(prev, now));
                    // simulate charging duration
                    Thread.sleep(300);
                    active.decrementAndGet();
                } catch (InterruptedException ignored) {
                } finally {
                    station.release();
                    done.countDown();
                }
            });
        }

        boolean finished = done.await(10, TimeUnit.SECONDS);
        assertTrue(finished, "Charging tasks did not finish in time");
        // because capacity is 1, peak concurrent active should be 1
        assertEquals(1, maxActiveObserved.get(), "Charging concurrency exceeded station capacity");
    }
}