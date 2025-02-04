package pl.kalishak.silcore.api.world.level.block.entity.properties;

import net.minecraft.core.Direction;

public interface IOType<T> {
    void setHandler(T handler, Direction face);

    T getHandler(Direction face);

    default boolean isEmpty() {
        return false;
    }

    @FunctionalInterface
    interface Factory<T> {
        T get(int size);
    }
}
