package net.dl909.dl_ct.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
//This block remember the time it last to be powered and post how long it goes when it is used in millisecond
public class timer_block extends Block{
    public static final BooleanProperty POWERED;
    public long time=0;
    public long cooldown = 0;

    public timer_block(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(POWERED, false));
    }
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, bl), 3);
            if(bl){
                time = new Date().getTime();
            }
        }

    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient()){
            if(time == 0||new Date().getTime()-cooldown>=1000 ){
                cooldown = new Date().getTime();
                return ActionResult.PASS;
            }else {
                cooldown = new Date().getTime();
                player.sendMessage(Text.literal(String.valueOf(-this.time +new Date().getTime())));
            }
            return ActionResult.SUCCESS;}
        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
    }
    static {
        POWERED = Properties.POWERED;
    }
}
