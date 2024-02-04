package net.dl909.dl_ct.block.entity;

import net.dl909.dl_ct.dl909_creative_tool;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class item_stream_emulator_block_entity extends BlockEntity {
    public item_stream_emulator_block_entity(BlockPos pos, BlockState state) {
        super(dl909_creative_tool.ITEM_TP_BLOCK_ENTITY, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, item_stream_emulator_block_entity be) {
    }
}
