package com.froobworld.farmcontrol.hook.nms.mobgoal;

import com.froobworld.farmcontrol.controller.action.RemoveRandomMovementAction;
import org.bukkit.entity.Mob;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BaseMobGoalNmsHookTest {
    public static class Goal {}
    public static class RandomGoal extends Goal {}
    public static class Wrapped {
        public final Goal goal;
        public final int priority;
        Wrapped(Goal goal, int priority) { this.goal = goal; this.priority = priority; }
    }
    public static class Selector { public final Set<Object> availableGoals = new LinkedHashSet<>(); }
    public static class Handle {
        public final Selector goals = new Selector();
        public final Selector targets = new Selector();
    }
    public interface HandleProvider { Handle getHandle(); }

    private BaseMobGoalNmsHook hook(String mobClass, String wrappedClass, List<String> movementClasses) {
        return new BaseMobGoalNmsHook("availableGoals", "goal", mobClass, Selector.class.getName(),
                wrappedClass, Goal.class.getName(), movementClasses) {};
    }

    @Test
    public void removesOnlyRandomGoalsAndRestoresTheSameWrappersAndPriorities() {
        BaseMobGoalNmsHook hook = hook(Handle.class.getName(), Wrapped.class.getName(), List.of(RandomGoal.class.getName()));
        assertTrue(hook.isCompatible());
        Handle handle = new Handle();
        Wrapped stroll = new Wrapped(new RandomGoal(), 7);
        Wrapped other = new Wrapped(new Goal(), 2);
        handle.goals.availableGoals.addAll(List.of(stroll, other));
        Mob mob = mock(Mob.class, withSettings().extraInterfaces(HandleProvider.class));
        when(((HandleProvider) mob).getHandle()).thenReturn(handle);
        assertEquals(2, hook.getGoalSelectors(mob).length);
        RemoveRandomMovementAction action = new RemoveRandomMovementAction(hook);
        action.doAction(mob);
        assertEquals(Set.of(other), handle.goals.availableGoals);
        action.undoAction(mob);
        assertEquals(Set.of(stroll, other), handle.goals.availableGoals);
        assertEquals(7, stroll.priority);
        assertSame(stroll, handle.goals.availableGoals.stream().filter(g -> g == stroll).findFirst().orElseThrow());
        action.undoAction(mob);
        assertEquals(2, handle.goals.availableGoals.size());
    }

    @Test
    public void missingClassesNeverReportCompatibility() {
        assertFalse(hook("missing.Mob", Wrapped.class.getName(), List.of(RandomGoal.class.getName())).isCompatible());
        assertFalse(hook(Handle.class.getName(), "missing.Wrapped", List.of(RandomGoal.class.getName())).isCompatible());
        assertFalse(hook(Handle.class.getName(), Wrapped.class.getName(), List.of(RandomGoal.class.getName(), "missing.Goal")).isCompatible());
    }
}
