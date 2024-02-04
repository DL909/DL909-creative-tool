package net.dl909.dl_ct.block;

import net.dl909.dl_ct.DL909_creative_tool;
import net.dl909.dl_ct.block.entity.item_stream_emulator_block_entity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class item_stream_emulator_block extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty POWERED;

    public item_stream_emulator_block(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
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
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, DL909_creative_tool.ITEM_STREAM_EMULATOR_BLOCK_ENTITY, (world1, pos, state1, be) -> item_stream_emulator_block_entity.tick(world1, pos, state1, be));
    }
    static {
        POWERED = Properties.POWERED;
    }
}
