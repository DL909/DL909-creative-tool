package net.dl909.dl_ct.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class debug_key extends Item {
    public debug_key(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (itemStack.getNbt() != null) {
            if (itemStack.getNbt().contains("target_x")
                    && itemStack.getNbt().contains("target_y")
                    && itemStack.getNbt().contains("target_z")) {
                tooltip.add(Text.literal("target:" +
                        itemStack.getNbt().getFloat("target_x") + "," +
                        itemStack.getNbt().getFloat("target_y") + "," +
                        itemStack.getNbt().getFloat("target_z")
                ));
                return;
            }
        }
        tooltip.add(Text.literal("no target set"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Vec3d pos = context.getHitPos();
        ItemStack itemStack = context.getStack();
        NbtCompound nbt = new NbtCompound();
        if(itemStack.getNbt()!=null){
            nbt.copyFrom(itemStack.getNbt());
        }
        nbt.putFloat("pos_x",(float) pos.x);
        nbt.putFloat("pos_y",(float) pos.y);
        nbt.putFloat("pos_z",(float) pos.z);
        itemStack.setNbt(nbt);
        appendTooltip(itemStack,context.getWorld(),itemStack.getTooltip(context.getPlayer(), new TooltipContext() {
            @Override
            public boolean isAdvanced() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        }),null);
        return super.useOnBlock(context);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        ItemStack itemStack = user.getStackInHand(hand);
        Vec3d pos = user.getPos();
        NbtCompound nbt = new NbtCompound();
        if(itemStack.getNbt()!=null){
            nbt.copyFrom(itemStack.getNbt());
        }
        nbt.putFloat("target_x",(float) pos.x);
        nbt.putFloat("target_y",(float) pos.y);
        nbt.putFloat("target_z",(float) pos.z);
        itemStack.setNbt(nbt);
        appendTooltip(itemStack,world,itemStack.getTooltip(user, new TooltipContext() {
            @Override
            public boolean isAdvanced() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        }),null);
        return TypedActionResult.success(itemStack,world.isClient());
    }
}
