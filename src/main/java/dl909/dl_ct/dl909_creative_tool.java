package dl909.dl_ct;

import dl909.dl_ct.block.*;
import dl909.dl_ct.block.entity.*;
import dl909.dl_ct.item.debug_key;
import dl909.dl_ct.item.hopper_update_tool;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class dl909_creative_tool implements ModInitializer {

    @Override
    public void onInitialize() {
        //block,block entity and block item
        Registry.register(Registries.BLOCK, new Identifier("dl_ct", "item_tp_block"), ITEM_TP_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "item_tp_block"), ITEM_TP_BLOCK_ITEM);
        Registry.register(Registries.BLOCK, new Identifier("dl_ct", "item_kill_block"), ITEM_KILL_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "item_kill_block"), ITEM_KILL_BLOCK_ITEM);
        Registry.register(Registries.BLOCK, new Identifier("dl_ct", "item_stream_emulator_block"), ITEM_STREAM_EMULATOR_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "item_stream_emulator_block"), ITEM_STREAM_EMULATOR_BLOCK_ITEM);

        //Registry.register(Registries.BLOCK, new Identifier("dl_ct","test_light_block"),DL909_REDSTONE_LAMP);
        //Registry.register(Registries.ITEM, new Identifier("dl_ct","test_light_block"),new BlockItem(DL909_REDSTONE_LAMP,new Item.Settings()));
        //Registry.register(Registries.BLOCK,new Identifier("dl_ct","timer_block"),TIMER_BLOCK);
        //Registry.register(Registries.ITEM,new Identifier("dl_ct","timer_block"),new BlockItem(TIMER_BLOCK,new Item.Settings()));
        //item
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "debug_key"), DEBUG_KEY);
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "hopper_update_tool"), HOPPER_DEBUG_KEY);

        //tooled_item
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "tooled_stick"), TOOLED_STICK);
        Registry.register(Registries.ITEM, new Identifier("dl_ct", "icon"), ICON);

        //group
        ItemGroupEvents.modifyEntriesEvent(ITEM).register(content -> {
            content.add(ITEM_TP_BLOCK_ITEM);
            content.add(ITEM_KILL_BLOCK_ITEM);
            content.add(ITEM_STREAM_EMULATOR_BLOCK_ITEM);
            content.add(DEBUG_KEY);
            content.add(TOOLED_STICK);
            content.add(ICON);
            content.add(HOPPER_DEBUG_KEY);
        });

    }

    public static final Block ITEM_TP_BLOCK = new item_tp_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
    public static final Item ITEM_TP_BLOCK_ITEM = new BlockItem(ITEM_TP_BLOCK, new Item.Settings());
    public static final BlockEntityType<item_tp_block_entity> ITEM_TP_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("dl_ct", "item_tp_block_entity"),
            FabricBlockEntityTypeBuilder.create(item_tp_block_entity::new, ITEM_TP_BLOCK).build()
    );


    public static final Block ITEM_KILL_BLOCK = new item_kill_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
    public static final Item ITEM_KILL_BLOCK_ITEM = new BlockItem(ITEM_KILL_BLOCK, new Item.Settings());
    public static final BlockEntityType<item_kill_block_entity> ITEM_KILL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("dl_ct", "item_kill_block_entity"),
            FabricBlockEntityTypeBuilder.create(item_kill_block_entity::new, ITEM_KILL_BLOCK).build()
    );

    public static final Block ITEM_STREAM_EMULATOR_BLOCK = new item_stream_emulator_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
    public static final Item ITEM_STREAM_EMULATOR_BLOCK_ITEM = new BlockItem(ITEM_STREAM_EMULATOR_BLOCK, new Item.Settings());
    public static final BlockEntityType<item_stream_emulator_block_entity> ITEM_STREAM_EMULATOR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("dl_ct", "item_stream_emulator_block_entity"),
            FabricBlockEntityTypeBuilder.create(item_stream_emulator_block_entity::new, ITEM_STREAM_EMULATOR_BLOCK).build()
    );


    //public static final Block DL909_REDSTONE_LAMP = new RedstoneLampBlock(AbstractBlock.Settings.of(Material.REDSTONE_LAMP).luminance(createLightLevelFromLitBlockState(15)).strength(0.3F).sounds(BlockSoundGroup.GLASS));
    //public static final Block TIMER_BLOCK = new timer_block(AbstractBlock.Settings.of(Material.STONE));
    //item
    public static final Item DEBUG_KEY = new debug_key(new Item.Settings().maxCount(1));
    public static final Item HOPPER_DEBUG_KEY = new hopper_update_tool(new Item.Settings().maxCount(1));
    public static final Item TOOLED_STICK = new Item(new Item.Settings());
    public static final Item ICON = new Item(new Item.Settings());
    /*
    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return (state) -> (Boolean)state.get(Properties.LIT) ? litLevel : 0;
    }
    */

    public static final ItemGroup ITEM = FabricItemGroup.builder(
                    new Identifier("dl_ct", "general")).
            icon(() -> new ItemStack(ICON)).build();

    public static final Identifier SLOT_CHANGE_STATE = new Identifier("dl_ct","slot_change_state");
}