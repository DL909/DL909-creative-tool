package net.dl909.dl_ct.block;

import net.dl909.dl_ct.dl909_creative_tool;
import net.dl909.dl_ct.block.entity.item_kill_block_entity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class item_kill_block extends BlockWithEntity implements BlockEntityProvider {
    public item_kill_block(Settings settings) {
        super(settings);
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new item_kill_block_entity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, dl909_creative_tool.ITEM_KILL_BLOCK_ENTITY, (world1, pos, state1, be) -> item_kill_block_entity.tick(world1, pos));
    }
}
