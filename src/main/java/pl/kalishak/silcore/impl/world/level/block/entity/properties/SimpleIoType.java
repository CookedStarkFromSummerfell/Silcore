package pl.kalishak.silcore.impl.world.level.block.entity.properties;

import net.minecraft.core.Direction;
import pl.kalishak.silcore.api.world.level.block.entity.properties.IOType;

public class SimpleIoType<T> implements IOType<T> {
    private final Factory<T> factory;
    private T handler;

    public SimpleIoType(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public void setHandler(T handler, Direction face) {
        this.handler = handler;
    }

    @Override
    public T getHandler(Direction face) {
        return this.handler;
    }

}
