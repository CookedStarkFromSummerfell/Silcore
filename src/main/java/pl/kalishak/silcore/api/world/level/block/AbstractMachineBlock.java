package pl.kalishak.silcore.api.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.data.tag.SilcoreApiTags;
import pl.kalishak.silcore.api.world.item.component.MachinePropertiesComponent;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;
import pl.kalishak.silcore.api.world.level.block.entity.AbstractMachineBlockEntity;
import pl.kalishak.silcore.api.world.level.block.entity.properties.MachineIO;
import pl.kalishak.silcore.api.world.level.block.entity.properties.PrivacyProperties;

import java.util.List;

public abstract class AbstractMachineBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BlockStateProperties.ACTIVE;

    public AbstractMachineBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.getBlockEntity(pos, type()).ifPresent(menuProvider -> player.openMenu(menuProvider, pos));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SilcoreApiTags.Items.WRENCH)) {
            if (!level.isClientSide) {
                BlockState rotatedState = state.rotate(level, pos, player.isCrouching() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
                level.setBlock(pos, rotatedState, Block.UPDATE_ALL_IMMEDIATE);
            }

            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected abstract MapCodec<? extends AbstractMachineBlock> codec();

    protected abstract BlockEntityType<? extends AbstractMachineBlockEntity> type();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            level.getBlockEntity(pos, type()).ifPresent(blockEntity -> {
                if (!level.isClientSide) {
                    if (blockEntity.hasInventory()) {
                        dropContents(level, pos, blockEntity.getInventory());
                    }
                }

                super.onRemove(state, level, pos, newState, movedByPiston);
                level.updateNeighbourForOutputSignal(pos, this);
            });
        } else {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData, player);
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof AbstractMachineBlockEntity machine) {
            stack.set(SilcoreApiDataComponents.MACHINE_PROPERTIES, new MachinePropertiesComponent(machine.getMachineProperties()));
        }

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        MachinePropertiesComponent machinePropertiesComponent = stack.get(SilcoreApiDataComponents.MACHINE_PROPERTIES);
        Level level = context.level();

        if (machinePropertiesComponent != null && level != null) {
            PrivacyProperties privacyProperties = machinePropertiesComponent.properties().privacyProperties();
            String ownerId = privacyProperties.getOwnerId();

            if (!ownerId.isEmpty()) {
                tooltipComponents.add(machinePropertiesComponent.getOwnerTooltip(level));
            }

            tooltipComponents.add(Component.translatable("machine.properties.privacy.access", Component.translatable("machine.properties.private.access." + privacyProperties.getType().getSerializedName())));

            if (tooltipFlag.hasShiftDown()) {
                MachineIO machineIO = machinePropertiesComponent.properties().machineIo();

                if (machineIO.hasCustomIO()) {
                    tooltipComponents.add(Component.translatable("machine.properties.io.modified"));
                }

                if (!privacyProperties.getSharedAccess().isEmpty()) {
                    tooltipComponents.addAll(machinePropertiesComponent.getSharedAccess(level));
                }
            }
        }
    }

    protected static void dropContents(Level level, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
        }
    }
}
