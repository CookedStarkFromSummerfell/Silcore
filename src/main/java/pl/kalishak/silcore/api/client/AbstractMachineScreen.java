package pl.kalishak.silcore.api.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.client.gui.FluidTankSlotMouseAction;
import pl.kalishak.silcore.api.network.ServerboundDrainTankPacket;
import pl.kalishak.silcore.api.network.ServerboundFillTankPacket;
import pl.kalishak.silcore.api.world.inventory.AbstractContainerWithTankMenu;
import pl.kalishak.silcore.api.world.inventory.FluidSlot;
import pl.kalishak.silcore.api.world.level.block.entity.AbstractMachineBlockEntity;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMachineScreen<M extends AbstractMachineBlockEntity, R extends AbstractContainerWithTankMenu<M>> extends AbstractContainerScreen<R> {
    public static final ResourceLocation TANK_SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "container/tank_slot_highlight_back");
    public static final ResourceLocation TANK_SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "container/tank_slot_highlight_front");
    private final List<FluidTankSlotMouseAction> fluidTankSlotMouseActions;

    protected @Nullable FluidSlot hoveredTank;
    private @Nullable FluidSlot clickedSlot;

    public AbstractMachineScreen(R menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.fluidTankSlotMouseActions = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        this.fluidTankSlotMouseActions.clear();
        //addFluidTankSlotMouseAction();
    }

    protected void addFluidTankSlotMouseAction(FluidTankSlotMouseAction action) {
        this.fluidTankSlotMouseActions.add(action);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float ticks) {
        super.render(guiGraphics, x, y, ticks);

        int i = this.leftPos;
        int j = this.topPos;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float) i, (float) j, 0.0F);

        FluidSlot fluidSlot = this.hoveredTank;

        renderTankSlotHighlightBack(guiGraphics);
        renderTankSlotHighlightFront(guiGraphics);

        if (fluidSlot != null && fluidSlot != this.hoveredTank) {
            onStopHoveringTankSlot(fluidSlot);
        }

        guiGraphics.pose().popPose();
    }

    private void renderTankSlotHighlightBack(GuiGraphics guiGraphics) {
        if (this.hoveredTank != null && this.hoveredTank.isHighlightable()) {
            guiGraphics.blitSprite(RenderType::guiTextured, TANK_SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredTank.x - 4, this.hoveredTank.y - 4, 24, 24);
        }
    }

    private void renderTankSlotHighlightFront(GuiGraphics guiGraphics) {
        if (this.hoveredTank != null && this.hoveredTank.isHighlightable()) {
            guiGraphics.blitSprite(RenderType::guiTexturedOverlay, TANK_SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredTank.x - 4, this.hoveredTank.y - 4, 24, 24);
        }
    }

    protected void renderFluidTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.hoveredTank != null && this.hoveredTank.hasFluid()) {
            FluidStack fluidStack = this.hoveredTank.getFluid();

            if (this.menu.getCarried().isEmpty()) {
                guiGraphics.renderTooltip(
                        this.font,
                        fluidStack.getHoverName(),
                        x,
                        y
                );
            }
        }
    }

    protected void renderTank(GuiGraphics guiGraphics, FluidSlot fluidSlot) {
        int i = fluidSlot.x;
        int j = fluidSlot.y;

        boolean changed = false;
        boolean usingSlot = fluidSlot == this.clickedSlot && this.isDragging();

        FluidStack fluidStack = fluidSlot.getFluid();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

        if (fluidStack.isEmpty() && fluidSlot.isActive()) {
            ResourceLocation noFluidTexture = fluidSlot.getNoFluidIcon();

            if (noFluidTexture != null) {
                guiGraphics.blitSprite(RenderType::guiTextured, noFluidTexture, i, j, 16, 48);
                usingSlot = true;
            }
        }

        if (!usingSlot) {
            renderTankContents(guiGraphics, fluidStack, fluidSlot, this.menu.getTankSlotCapacity(fluidSlot.index));
        }

        guiGraphics.pose().popPose();
    }

    protected void renderTankContents(GuiGraphics guiGraphics, FluidStack fluidStack, FluidSlot fluidSlot, int capacity) {
        int i = fluidSlot.x;
        int j = fluidSlot.y;
        ResourceLocation fluidTextures = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture();

        int scale = Mth.ceil(getScale(fluidStack.getAmount(), capacity) * 24.0F) + 1;
        guiGraphics.blitSprite(RenderType::guiTextured, fluidTextures, 16, 48, 0, 48 - scale, i + fluidSlot.x, j + fluidSlot.y, 16, scale);
    }

    @Nullable
    private FluidSlot getHoveredTank(double mouseX, double mouseY) {
        for (FluidSlot slot : this.menu.fluidSlots) {
            if (slot.isActive() && isHoveringTankSlot(slot, mouseX, mouseY)) {
                return slot;
            }
        }

        return null;
    }

    private boolean isHoveringTankSlot(FluidSlot slot, double mouseX, double mouseY) {
        return this.isHovering(slot.x, slot.y, 16, 48, mouseX, mouseY);
    }

    private void onStopHoveringTankSlot(FluidSlot slot) {
        if (slot.hasFluid()) {
            for (FluidTankSlotMouseAction action : this.fluidTankSlotMouseActions) {
                if (action.matches(slot)) {
                    action.onStopHovering(slot);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            FluidSlot fluidSlot = getHoveredTank(mouseX, mouseY);

            int x = this.leftPos;
            int y = this.topPos;

            boolean clickedOutBounds = hasClickedOutside(mouseX, mouseY, x, y, button);

            if (fluidSlot != null) clickedOutBounds = false;

            int slotId = -1;

            if (fluidSlot != null) {
                slotId = fluidSlot.index;
            }

            if (clickedOutBounds) {
                slotId = -999;
            }

            if (slotId != -1) {
                if (!this.menu.getCarried().isEmpty()) {
                    if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                        tankSlotClicked(slotId, ClickType.CLONE, false);
                    } else {
                        boolean pickFluid = this.minecraft.options.keyRight.isActiveAndMatches(mouseKey);

                        if (slotId != -999 && (pickFluid || this.minecraft.options.keyLeft.isActiveAndMatches(mouseKey))) {
                            tankSlotClicked(slotId, ClickType.PICKUP, pickFluid);
                        }
                    }
                }
            }
        }

        return true;
    }

    protected void tankSlotClicked(int slotId, ClickType clickType, boolean drain) {
        if (clickType == ClickType.CLONE || !drain) {
            PacketDistributor.sendToServer(new ServerboundFillTankPacket(
                    this.menu.containerId,
                    (short) slotId,
                    clickType,
                    this.menu.getCarried()
            ));
        } else if (clickType == ClickType.PICKUP) {
            PacketDistributor.sendToServer(new ServerboundDrainTankPacket(
                    this.menu.containerId,
                    (short) slotId,
                    this.menu.getCarried()
            ));
        }
    }

    @Override
    public void onClose() {
        if (this.hoveredTank != null) {
            onStopHoveringTankSlot(this.hoveredTank);
        }

        super.onClose();
    }

    protected static float getScale(int amount, int capacity) {
        return Mth.clamp((float) amount / (float) capacity, 0.0F, 1.0F);
    }
}
