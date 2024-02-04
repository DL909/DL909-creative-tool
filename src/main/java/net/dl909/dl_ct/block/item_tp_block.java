package net.dl909.dl_ct.block;

import net.dl909.dl_ct.block.entity.item_tp_block_entity;
import net.dl909.dl_ct.dl909_creative_tool;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class item_tp_block extends BlockWithEntity implements BlockEntityProvider {
    public item_tp_block(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (itemStack.getNbt() != null && itemStack.getItem() == dl909_creative_tool.ITEM_TP_BLOCK_KEY
                    && itemStack.getNbt().contains("pos_x")
                    && itemStack.getNbt().contains("pos_y")
                    && itemStack.getNbt().contains("pos_z")) {
                NbtCompound nbt = new NbtCompound();
                nbt.putFloat("target_x", itemStack.getNbt().getFloat("target_x"));
                nbt.putFloat("target_y",itemStack.getNbt().getFloat("target_y"));
                nbt.putFloat("target_z",itemStack.getNbt().getFloat("target_z"));
                Objects.requireNonNull(world.getBlockEntity(pos)).readNbt(nbt);
                Objects.requireNonNull(world.getBlockEntity(pos)).markDirty();
                player.sendMessage(Text.literal("target set to "+itemStack.getNbt().getFloat("target_x")+","+itemStack.getNbt().getFloat("target_y")+","+itemStack.getNbt().getFloat("target_z")));
            }else{
                player.sendMessage(Text.literal("target:"+
                        Objects.requireNonNull(world.getBlockEntity(pos)).createNbt().getFloat("target_x")+","+
                        Objects.requireNonNull(world.getBlockEntity(pos)).createNbt().getFloat("target_y")+","+
                        Objects.requireNonNull(world.getBlockEntity(pos)).createNbt().getFloat("target_z")));
            }
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new item_tp_block_entity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, dl909_creative_tool.ITEM_TP_BLOCK_ENTITY, (world1, pos, state1, be) -> item_tp_block_entity.tick(world1, pos));
    }
}
