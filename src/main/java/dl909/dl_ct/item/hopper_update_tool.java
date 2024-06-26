package dl909.dl_ct.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.block.HopperBlock.ENABLED;

public class hopper_update_tool extends Item {
    public hopper_update_tool(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            Vec3d pos = user.getPos();
            for (int x = (int) pos.x - 5; x < (int) pos.x + 6; x++) {
                for (int y = (int) pos.y - 5; y < (int) pos.y + 6; y++) {
                    for (int z = (int) pos.z - 5; z < (int) pos.z + 6; z++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        BlockState target = world.getBlockState(blockPos);
                        if (target.getBlock().getClass() == HopperBlock.class) {
                            boolean bl = !world.isReceivingRedstonePower(blockPos);
                            if (bl != target.get(ENABLED)) {
                                world.setBlockState(blockPos, (BlockState)target.with(ENABLED, bl), 4);
                            }
                        }
                    }
                }
            }
        }
        user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1.0F,1.0F);
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
