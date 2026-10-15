package com.froobworld.farmcontrol.hook.scheduler;

import org.bukkit.Bukkit;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegionisedSchedulerHookTest {
    @Test
    public void globalThreadExecutesImmediatelyExactlyOnceWithoutSchedulingAgain() {
        AtomicInteger runs = new AtomicInteger();
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::isGlobalTickThread).thenReturn(true);
            ScheduledTask task = new RegionisedSchedulerHook(null).runTaskAsap(runs::incrementAndGet);
            assertEquals(1, runs.get());
            assertNotNull(task);
            bukkit.verify(Bukkit::getGlobalRegionScheduler, never());
        }
    }
}
