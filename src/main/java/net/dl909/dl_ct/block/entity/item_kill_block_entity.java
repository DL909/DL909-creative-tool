package net.dl909.dl_ct.block.entity;

import net.dl909.dl_ct.dl909_creative_tool;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class item_kill_block_entity extends BlockEntity {

    public item_kill_block_entity(BlockPos pos, BlockState state) {
        super(dl909_creative_tool.ITEM_KILL_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {

        for (Entity target : world.getEntitiesByType(
                EntityType.ITEM,
                new Box(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                EntityPredicates.VALID_ENTITY)) {

            target.kill();
        }
    }
}
