package dl909.dl_ct.screen;

import dl909.dl_ct.block.CrafterBlock;
import dl909.dl_ct.block.entity.CrafterBlockEntity;
import dl909.dl_ct.dl909_creative_tool;
import dl909.dl_ct.screen.slot.CrafterInputSlot;
import dl909.dl_ct.screen.slot.CrafterOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterScreenHandler
        extends ScreenHandler
        implements ScreenHandlerListener {
    private final CraftingResultInventory resultInventory = new CraftingResultInventory();
    private final Inventory inputInventory;
    private final PlayerEntity player;
    public BlockPos pos;
    PropertyDelegate propertyDelegate;
    public CrafterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(9), new ArrayPropertyDelegate(10));
        this.pos = buf.readBlockPos();
    }

    public CrafterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inputInventory, PropertyDelegate propertyDelegate){
        super(dl909_creative_tool.CRAFTER_3X3,syncId);
        checkSize(inputInventory,9);
        this.inputInventory = inputInventory;
        this.player = playerInventory.player;
        this.propertyDelegate = propertyDelegate;
        this.addSlots(playerInventory);
        inputInventory.onOpen(playerInventory.player);
        this.addListener(this);
        this.pos = BlockPos.ORIGIN;
    }

    private void addSlots(PlayerInventory playerInventory) {
        int j;
        int i;
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                int k = j + i * 3;
                this.addSlot(new CrafterInputSlot(this.inputInventory, k, 26 + j * 18, 17 + i * 18, this));
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addSlot(new CrafterOutputSlot(this.resultInventory, 0, 134, 35));
        this.addProperties(this.propertyDelegate);
        this.updateResult();
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        this.updateResult();
    }
    private void updateResult() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity) {
            World world = serverPlayerEntity.getWorld();
            if(CrafterBlock.getCraftingRecipe(world, (CrafterBlockEntity) this.inputInventory).isPresent()) {
                CraftingInventory craftingInventory = new CraftingInventory(new VoidScreenHandler(),3,3);
                craftingInventory.setStack(0, inputInventory.getStack(0));
                craftingInventory.setStack(1, inputInventory.getStack(1));
                craftingInventory.setStack(2, inputInventory.getStack(2));
                craftingInventory.setStack(3, inputInventory.getStack(3));
                craftingInventory.setStack(4, inputInventory.getStack(4));
                craftingInventory.setStack(5, inputInventory.getStack(5));
                craftingInventory.setStack(6, inputInventory.getStack(6));
                craftingInventory.setStack(7, inputInventory.getStack(7));
                craftingInventory.setStack(8, inputInventory.getStack(8));

                ItemStack itemStack = CrafterBlock.getCraftingRecipe(world, (CrafterBlockEntity) this.inputInventory).get().craft(craftingInventory, world.getRegistryManager());
                this.resultInventory.setStack(0, itemStack);
            }else {
                this.resultInventory.setStack(0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inputInventory.canPlayerUse(player);
    }

    public void setSlotEnabled(int slot, boolean enabled) {
        CrafterInputSlot crafterInputSlot = (CrafterInputSlot)this.getSlot(slot);
        this.propertyDelegate.set(crafterInputSlot.id, enabled ? 0 : 1);
        this.sendContentUpdates();
    }

    public boolean isSlotDisabled(int slot) {
        if (slot > -1 && slot < 9) {
            return this.propertyDelegate.get(slot) == 1;
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inputInventory.size()) {
                if (!this.insertItem(originalStack, this.inputInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inputInventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
    public Inventory getInputInventory() {
        return this.inputInventory;
    }
    public boolean isTriggered() {
        return this.propertyDelegate.get(9) == 1;
    }

}

