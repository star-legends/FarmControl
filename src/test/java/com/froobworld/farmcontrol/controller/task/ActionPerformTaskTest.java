package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.controller.tracker.CycleTracker;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

public class ActionPerformTaskTest {
    @Test
    public void entityThatBecameCustomAfterSnapshotIsProtectedBeforeAction() {
        Entity entity = mock(Entity.class);
        when(entity.isValid()).thenReturn(true);
        SnapshotEntity snapshot = mock(SnapshotEntity.class);
        when(snapshot.getEntity()).thenReturn(entity);
        Action action = mock(Action.class);
        SchedulerHook scheduler = mock(SchedulerHook.class);
        when(scheduler.runEntityTaskAsap(any(), any(), eq(entity))).thenAnswer(call -> {
            call.<Runnable>getArgument(0).run();
            return null;
        });
        TriggerActionPair pair = new TriggerActionPair(mock(Trigger.class), action);
        new ActionPerformTask(mock(World.class), scheduler, Map.of(snapshot, Set.of(pair)), Map.of(),
                mock(CycleTracker.class), candidate -> true).run();
        verify(action, never()).doAction(any());
        verify(entity, never()).getPersistentDataContainer();
    }
}
