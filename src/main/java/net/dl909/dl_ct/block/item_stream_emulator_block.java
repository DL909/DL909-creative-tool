package net.dl909.dl_ct.block;

import net.dl909.dl_ct.block.entity.item_stream_emulator_block_entity;
import net.dl909.dl_ct.dl909_creative_tool;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class item_stream_emulator_block extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty POWERED;


    public item_stream_emulator_block(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(POWERED, false));
    }
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (itemStack.getItem() == dl909_creative_tool.DEBUG_KEY) {
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("tick",0);
                nbt.putBoolean("saving",true);
                nbt.putBoolean("saved",true);
                Objects.requireNonNull(world.getBlockEntity(pos)).readNbt(nbt);
                Objects.requireNonNull(world.getBlockEntity(pos)).markDirty();
                player.sendMessage(Text.literal("item entity saving"));
            }else{
                player.sendMessage(Text.literal("saving:"+Objects.requireNonNull(world.getBlockEntity(pos)).createNbt().getBoolean("saving")+",saved:"+Objects.requireNonNull(world.getBlockEntity(pos)).createNbt().getBoolean("saved")));
            }
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new item_stream_emulator_block_entity(pos, state);
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, bl), 3);
        }

    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, dl909_creative_tool.ITEM_STREAM_EMULATOR_BLOCK_ENTITY, item_stream_emulator_block_entity::tick);
    }
    static {
        POWERED = Properties.POWERED;
    }
}
