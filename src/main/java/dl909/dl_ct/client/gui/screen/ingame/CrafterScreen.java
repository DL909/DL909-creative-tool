package dl909.dl_ct.client.gui.screen.ingame;


import com.mojang.blaze3d.systems.RenderSystem;
import dl909.dl_ct.dl909_creative_tool;
import dl909.dl_ct.screen.CrafterScreenHandler;
import dl909.dl_ct.screen.slot.CrafterInputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CrafterScreen extends HandledScreen<CrafterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafter.png");
    private static final Text TOGGLEABLE_SLOT_TEXT = Text.translatable("gui.toggleable_slot");
    private final PlayerEntity player;


    public CrafterScreen(CrafterScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.player = playerInventory.player;
    }

    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }



    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot instanceof CrafterInputSlot && !slot.hasStack() && !this.player.isSpectator()) {
            switch (actionType) {
                case PICKUP:
                    if (((CrafterScreenHandler)this.handler).isSlotDisabled(slotId)) {
                        this.enableSlot(slotId);

                    } else if (((CrafterScreenHandler)this.handler).getCursorStack().isEmpty()) {
                        this.disableSlot(slotId);
                    }
                    break;
                case SWAP:
                    ItemStack itemStack = this.player.getInventory().getStack(button);
                    if (((CrafterScreenHandler)this.handler).isSlotDisabled(slotId) && !itemStack.isEmpty()) {
                        this.enableSlot(slotId);
                    }
            }
        }
        super.onMouseClick(slot, slotId, button, actionType);
    }

    private void enableSlot(int slotId) {
        this.setSlotEnabled(slotId, true);
    }

    private void disableSlot(int slotId) {
        this.setSlotEnabled(slotId, false);
    }

    private void setSlotEnabled(int slotId, boolean enabled) {
        ((CrafterScreenHandler)this.handler).setSlotEnabled(slotId, enabled);
        float f = enabled ? 1.0F : 0.75F;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.handler.pos);
        buf.writeInt(slotId);
        buf.writeInt(this.handler.syncId);
        buf.writeBoolean(enabled);
        ClientPlayNetworking.send(dl909_creative_tool.SLOT_CHANGE_STATE, buf);
        this.player.playSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, f);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        for(int i=0;i<this.getScreenHandler().slots.size();i++){
            if(this.getScreenHandler().isSlotDisabled(i)){
                Slot slot = this.getScreenHandler().slots.get(i);
                drawDisabledSlot(matrices, (CrafterInputSlot) slot);
            }
        }
        drawArrowTexture(matrices);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
        if (this.focusedSlot instanceof CrafterInputSlot && !((CrafterScreenHandler)this.handler).isSlotDisabled(this.focusedSlot.id) && ((CrafterScreenHandler)this.handler).getCursorStack().isEmpty() && !this.focusedSlot.hasStack() && !this.player.isSpectator()) {
            this.renderTooltip(matrices, TOGGLEABLE_SLOT_TEXT, mouseX, mouseY);
        }
    }
    private void drawArrowTexture(MatrixStack matrices) {
        int i = this.width / 2 + 9;
        int j = this.height / 2 - 45;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (!this.handler.isTriggered()) {
            drawTexture(matrices, i, j,backgroundWidth,0, 16, 11);
        } else {
            drawTexture(matrices, i, j,backgroundWidth,11, 16, 11);
        }
    }
    private void drawDisabledSlot(MatrixStack matrices, CrafterInputSlot slot) {
        int x = slot.x-1+(width - backgroundWidth) / 2;
        int y = slot.y-1+(height - backgroundHeight) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices,x,y,backgroundWidth, 22, 18, 18);
    }
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

}