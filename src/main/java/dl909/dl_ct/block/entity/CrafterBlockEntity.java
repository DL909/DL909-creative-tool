package dl909.dl_ct.block.entity;

import com.google.common.annotations.VisibleForTesting;
import dl909.dl_ct.block.CrafterBlock;
import dl909.dl_ct.dl909_creative_tool;
import dl909.dl_ct.screen.CrafterScreenHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public class CrafterBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory, RecipeInputProvider,Inventory {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;
    public static final int GRID_SIZE = 9;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int TRIGGERED_PROPERTY = 9;
    public static final int PROPERTIES_COUNT = 10;
    private DefaultedList<ItemStack> inputStacks;
    private int craftingTicksRemaining;
    public final PropertyDelegate propertyDelegate;


    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(dl909_creative_tool.CRAFTER_BLOCK_ENTITY, pos, state);
        this.inputStacks = DefaultedList.ofSize(9, Items.AIR.getDefaultStack());
        this.craftingTicksRemaining = 0;
        this.propertyDelegate = new PropertyDelegate() {
            private final int[] disabledSlots = new int[18];
            private int triggered = 0;

            public int get(int index) {
                return index == 9 ? this.triggered : ((index<9&&index>-1)?this.disabledSlots[index]:1);
            }

            public void set(int index, int value) {
                if (index == 9) {
                    this.triggered = value;
                } else {
                    this.disabledSlots[index] = value;
                }

            }

            public int size() {
                return 10;
            }
        };
    }
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.crafter");
    }
    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrafterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
        //return null;
    }
    public ScreenHandler createMenu(int syncId,PlayerInventory playerInventory,PlayerEntity player){
        return new CrafterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
        // 对于1.19之前的版本，请使用：
        // return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    public void setSlotEnabled(int slot, boolean enabled) {
        if (!this.canToggleSlot(slot)) {
            return;
        }
        this.propertyDelegate.set(slot, enabled ? 0 : 1);
        this.markDirty();
    }

    public boolean isSlotDisabled(int slot) {
        if (slot >= 0 && slot < 9) {
            return this.propertyDelegate.get(slot) == 1;
        }
        return false;
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (this.propertyDelegate.get(slot) == 1) {
            return false;
        } else {
            ItemStack itemStack = (ItemStack)this.inputStacks.get(slot);
            int i = itemStack.getCount();
            if (i >= itemStack.getMaxCount()) {
                return false;
            } else if (itemStack.isEmpty()) {
                return true;
            } else {
                return !this.betterSlotExists(i, itemStack, slot);
            }
        }
    }

    private boolean betterSlotExists(int count, ItemStack stack, int slot) {
        for(int i = slot + 1; i < 9; ++i) {
            if (!this.isSlotDisabled(i)) {
                ItemStack itemStack = this.getStack(i);
                if (itemStack.isEmpty() || itemStack.getCount() < count && areItemsAndComponentsEqual(itemStack, stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean areItemsAndComponentsEqual(ItemStack stack, ItemStack otherStack) {
        if (!stack.isOf(otherStack.getItem())) {
            return false;
        }
        if (stack.isEmpty() && otherStack.isEmpty()) {
            return true;
        }
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.craftingTicksRemaining = nbt.getInt("crafting_ticks_remaining");
        this.inputStacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inputStacks);
        }

        int[] is = nbt.getIntArray("disabled_slots");

        for(int i = 0; i < 9; ++i) {
            this.propertyDelegate.set(i, 0);
        }

        int[] var8 = is;
        int var5 = is.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int j = var8[var6];
            if (this.canToggleSlot(j)) {
                this.propertyDelegate.set(j, 1);
            }
        }

        this.propertyDelegate.set(9, nbt.getInt("triggered"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inputStacks);
        }

        this.putDisabledSlots(nbt);
        this.putTriggered(nbt);
    }

    @Override
    public int size() {
        return 9;
    }

    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.inputStacks.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }
    @Override
    public ItemStack getStack(int slot) {
        ItemStack itemStack;
        try {
            itemStack = this.inputStacks.get(slot);
        }catch (java.lang.ArrayIndexOutOfBoundsException t){
            itemStack = Items.AIR.getDefaultStack();
        }
        return itemStack;
    }
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (this.isSlotDisabled(slot)) {
            this.setSlotEnabled(slot, true);
        }

        super.setStack(slot, stack);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inputStacks;
    }
    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        System.out.println("Stack set");
        this.inputStacks = list;
    }
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.inputStacks;
    }

    public void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inputStacks = inventory;
    }

    public void provideRecipeInputs(RecipeMatcher finder) {
        Iterator<ItemStack> var2 = this.inputStacks.iterator();

        while(var2.hasNext()) {
            ItemStack itemStack = var2.next();
            finder.addUnenchantedInput(itemStack);
        }

    }

    private void putDisabledSlots(NbtCompound nbt) {
        IntList intList = new IntArrayList();

        for(int i = 0; i < 9; ++i) {
            if (this.isSlotDisabled(i)) {
                intList.add(i);
            }
        }

        nbt.putIntArray("disabled_slots", intList);
    }

    private void putTriggered(NbtCompound nbt) {
        nbt.putInt("triggered", this.propertyDelegate.get(9));
    }

    public void setTriggered(boolean triggered) {
        this.propertyDelegate.set(9, triggered ? 1 : 0);
    }

    @VisibleForTesting
    public boolean isTriggered() {
        return this.propertyDelegate.get(9) == 1;
    }

    public static void tickCrafting(World world, BlockPos pos, BlockState state, CrafterBlockEntity blockEntity) {
        int i = blockEntity.craftingTicksRemaining - 1;
        if (i >= 0) {
            blockEntity.craftingTicksRemaining = i;
            if (i == 0) {
                world.setBlockState(pos, (BlockState)state.with(CrafterBlock.CRAFTING, false), 3);
            }

        }

    }

    public void setCraftingTicksRemaining(int craftingTicksRemaining) {
        this.craftingTicksRemaining = craftingTicksRemaining;
    }

    public int getComparatorOutput() {
        int i = 0;
        for (int j = 0; j < this.size(); ++j) {
            ItemStack itemStack = this.getStack(j);
            if (itemStack.isEmpty() && !this.isSlotDisabled(j)) continue;
            ++i;
        }
        return i;
    }

    private boolean canToggleSlot(int slot) {
        return slot > -1 && slot < 9 && this.inputStacks.get(slot).isEmpty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
