package net.dl909.dl_ct.block.entity;

import net.dl909.dl_ct.dl909_creative_tool;
import net.dl909.dl_ct.util.ItemEntityHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class item_stream_emulator_block_entity extends BlockEntity{
    public String[][] list1 = new String[100][100];
    public int tick;
    public boolean saving;
    public boolean saved;
    public item_stream_emulator_block_entity(BlockPos pos, BlockState state) {
        super(dl909_creative_tool.ITEM_STREAM_EMULATOR_BLOCK_ENTITY, pos, state);
        readNbt(this.createNbt());
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the tag
        String str = "";
        for(int x=0;x<100;x++){
            for(int y=0;y<100;y++){
                if(list1[x][y]!=null){
                    str += list1[x][y]+";";
                }
            }
            nbt.putString(String.valueOf(x),str);
            str = "";
        }

        nbt.putInt("tick",tick);
        nbt.putBoolean("saving",saving);
        nbt.putBoolean("saved",saved);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        for(int x=0;x<100;x++){
            String data = nbt.getString(String.valueOf(x));
            if(!Objects.equals(data, "")){
                String[] line = data.split(";");
                System.arraycopy(line,0, list1[x], 0, 100);
            }
        }
        tick = nbt.getInt("tick");
        saving = nbt.getBoolean("saving");
        saved = nbt.getBoolean("saved");
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
    public static void tick(World world, BlockPos pos, BlockState state, item_stream_emulator_block_entity be) {
        be.tick+=1;
        if(be.tick==100){
            be.tick = 0;
            if(be.saving){
                be.saving = false;
            }
        }
        if(be.saving){
            int i = 0;
            for(ItemEntity target : world.getEntitiesByType(
                    EntityType.ITEM,
                    new Box(pos.getX(),pos.getY(),pos.getZ(),
                            pos.getX()+1,pos.getY()+1,pos.getZ()+1),
                    EntityPredicates.VALID_ENTITY))
            {
                if(i<100){
                    i++;
                    String[] str1 = target.getStack().getItem().getTranslationKey().split("\\.");
                    be.list1[be.tick][i]= str1[str1.length - 1] + ","
                            + target.getStack().getCount() + ","
                            + target.getPos().x + ","
                            + target.getPos().y + ","
                            + target.getPos().z + ","
                            + target.getVelocity().x + ","
                            + target.getVelocity().y + ","
                            + target.getVelocity().z;
                    target.kill();
                }
            }
            Objects.requireNonNull(world.getBlockEntity(pos)).markDirty();
        }else if(be.saved){
            for(int i=0;i<100;i++){
                if(be.list1[be.tick][i]!=null){
                    ItemEntity target = ItemEntityHelper.createItemEntityFromParameterList(world, be.list1[be.tick][i].split(","));
                    world.spawnEntity(target);
                }
            }
        }
    }
}
