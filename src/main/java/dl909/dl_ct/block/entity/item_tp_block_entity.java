package dl909.dl_ct.block.entity;

import dl909.dl_ct.dl909_creative_tool;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class item_tp_block_entity extends BlockEntity {
    // 储存数字的当前值
    private float target_x = 0;
    private float target_y = 0;
    private float target_z = 0;

    public item_tp_block_entity(BlockPos pos, BlockState state) {
        super(dl909_creative_tool.ITEM_TP_BLOCK_ENTITY, pos, state);
        readNbt(this.createNbt());
        target_x = 0.0f;
        target_y = 0.0f;
        target_z = 0.0f;
    }

    // 序列化方块实体
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the tag
        nbt.putFloat("target_x", target_x);
        nbt.putFloat("target_y", target_y);
        nbt.putFloat("target_z", target_z);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        target_x = nbt.getFloat("target_x");
        target_y = nbt.getFloat("target_y");
        target_z = nbt.getFloat("target_z");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static void tick(World world, BlockPos pos, BlockState state, item_tp_block_entity be) {
        for (Entity target : world.getEntitiesByType(
                EntityType.ITEM,
                new Box(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                EntityPredicates.VALID_ENTITY)) {
            target.teleport(be.target_x, be.target_y, be.target_z);
        }
    }
}
