package pl.kalishak.silcore.impl.world.level.block.entity.properties;

import net.minecraft.core.Direction;
import pl.kalishak.silcore.api.world.level.block.entity.properties.IOType;

public record EmptyIoType() implements IOType<Void> {
    @Override
    public void setHandler(Void handler, Direction face) {
        throw new UnsupportedOperationException("Empty IO cannot perform any action");
    }

    @Override
    public Void getHandler(Direction face) {
        throw new UnsupportedOperationException("Empty IO cannot perform any action");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
