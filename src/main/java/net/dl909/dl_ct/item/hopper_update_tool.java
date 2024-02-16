package net.dl909.dl_ct.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class hopper_update_tool extends Item {
    public hopper_update_tool(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient()) {
            Vec3d pos = user.getPos();
            for (int x = (int) pos.x - 5; x < (int) pos.x + 6; x++) {
                for (int y = (int) pos.y - 5; y < (int) pos.y + 6; y++) {
                    for (int z = (int) pos.z - 5; z < (int) pos.z + 6; z++) {
                        BlockState target = world.getBlockState(new BlockPos(x, y, z));
                        if (target.getBlock().getClass() == HopperBlock.class) {
                            target.getBlock().neighborUpdate(target, world, new BlockPos(x, y, z), null, null, false);
                        }
                    }
                }
            }
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }
}
