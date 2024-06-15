package dl909.dl_ct.block;

import dl909.dl_ct.block.entity.CrafterBlockEntity;
import dl909.dl_ct.dl909_creative_tool;
import dl909.dl_ct.recipe.RecipeCache;
import dl909.dl_ct.screen.VoidScreenHandler;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


//able to craft
//open ui will crash the game

public class CrafterBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty TRIGGERED;
    public static final BooleanProperty CRAFTING;
    private static final int field_46802 = 6;
    private static final int TRIGGER_DELAY = 4;
    private static final RecipeCache recipeCache = new RecipeCache(10);
    private static final int field_50015 = 17;
    private static final EnumProperty<JigsawOrientation> ORIENTATION;

    public CrafterBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ORIENTATION, JigsawOrientation.NORTH_UP)).with(TRIGGERED, false)).with(CRAFTING, false));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
            return crafterBlockEntity.getComparatorOutput();
        }
        return 0;
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        boolean bl2 = state.get(TRIGGERED);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), Block.NOTIFY_LISTENERS);
            this.setTriggered(blockEntity, true);
        } else if (!bl && bl2) {
            world.setBlockState(pos, (BlockState)((BlockState)state.with(TRIGGERED, false)).with(CRAFTING, false), Block.NOTIFY_LISTENERS);
            this.setTriggered(blockEntity, false);
        }
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.craft(state, world, pos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : CrafterBlock.checkType(type, dl909_creative_tool.CRAFTER_BLOCK_ENTITY, CrafterBlockEntity::tickCrafting);
    }

    private void setTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
        if (blockEntity instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
            crafterBlockEntity.setTriggered(triggered);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CrafterBlockEntity crafterBlockEntity = new CrafterBlockEntity(pos, state);
        crafterBlockEntity.setTriggered(state.contains(TRIGGERED) && state.get(TRIGGERED) != false);
        return new CrafterBlockEntity(pos,state);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection().getOpposite();
        Direction direction2 = switch (direction) {
            default -> throw new RuntimeException(null, null);
            case DOWN -> ctx.getHorizontalPlayerFacing().getOpposite();
            case UP -> ctx.getHorizontalPlayerFacing();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };
        return (BlockState)((BlockState)this.getDefaultState().with(ORIENTATION, JigsawOrientation.byDirections(direction, direction2))).with(TRIGGERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (state.get(TRIGGERED).booleanValue()) {
            world.scheduleBlockTick(pos, this, 4);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory)((Object)blockEntity);
            ItemScatterer.spawn(world, pos, inventory);
            world.updateComparators(pos, state.getBlock());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrafterBlockEntity) {
            player.openHandledScreen(screenHandlerFactory);
        }
        if (blockEntity != null) {
            blockEntity.markDirty();
        }
        return ActionResult.CONSUME;
    }
    protected void craft(BlockState state, ServerWorld world, BlockPos pos){
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CrafterBlockEntity)) {
            return;
        }
        CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
        CraftingInventory craftingInventory = new CraftingInventory(new VoidScreenHandler(),3,3);
        Optional<CraftingRecipe> optional = CrafterBlock.getCraftingRecipe(world, crafterBlockEntity);
        if (optional.isEmpty()) {
            world.syncWorldEvent(1050, pos, 0);
            return;
        }
        CraftingRecipe recipeEntry = optional.get();
        ItemStack itemStack = recipeEntry.craft(craftingInventory, world.getRegistryManager());
        if (itemStack.isEmpty()) {
            world.syncWorldEvent(1050, pos, 0);
            return;
        }
        crafterBlockEntity.setCraftingTicksRemaining(6);
        world.setBlockState(pos, (BlockState)state.with(CRAFTING, true), Block.NOTIFY_LISTENERS);
        itemStack.getItem().onCraft(itemStack,world,null);
        this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack, state);
        /*
        * Due to the change of code during minecraft 1.19->1.20:
        * onCraft(ItemStack,World,Player)->onCraft(ItemStack,World)
        * Some mod Overwrite this method and use Player param may crash when crafting item using crafter
         */
        for (ItemStack itemStack2 : recipeEntry.getRemainder(craftingInventory)) {
            if (itemStack2.isEmpty()) continue;
            this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack2, state);
        }
        crafterBlockEntity.getHeldStacks().forEach(stack -> {
            if (stack.isEmpty()) {
                return;
            }
            stack.decrement(1);
        });
        crafterBlockEntity.markDirty();
    }
    public static Optional<CraftingRecipe> getCraftingRecipe(World world, CrafterBlockEntity inputInventory) {
        return recipeCache.getRecipe(world, inputInventory);
    }

    private void transferOrSpawnStack(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state) {
        Direction direction = state.get(ORIENTATION).getFacing();
        Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
        ItemStack itemStack = stack.copy();
        if (inventory != null && (inventory instanceof CrafterBlockEntity || stack.getCount() > Math.min(inventory.getMaxCountPerStack(),stack.getMaxCount()))) {
            while (!itemStack.isEmpty() && HopperBlockEntity.transfer(blockEntity, inventory, itemStack.copyWithCount(1), direction.getOpposite()).isEmpty()) {
                itemStack.decrement(1);
            }
        } else if (inventory != null) {
            int i;
            while (!itemStack.isEmpty() && (i = itemStack.getCount()) != (itemStack = HopperBlockEntity.transfer(blockEntity, inventory, itemStack, direction.getOpposite())).getCount()) {
            }
        }
        if (!itemStack.isEmpty()) {
            Vec3d vec3d = Vec3d.ofCenter(pos);
            Vec3d vec3d2 = vec3d.offset(direction, 0.7);
            ItemDispenserBehavior.spawnItem(world, itemStack, 6, direction, vec3d2);
        }
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, TRIGGERED, CRAFTING);
    }
    static {
        ORIENTATION = Properties.ORIENTATION;
        TRIGGERED = Properties.TRIGGERED;
        CRAFTING = BooleanProperty.of("crafting");
    }

}
