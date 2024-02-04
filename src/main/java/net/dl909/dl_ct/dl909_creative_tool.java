package net.dl909.dl_ct;

import net.dl909.dl_ct.block.entity.item_kill_block_entity;
import net.dl909.dl_ct.block.entity.item_stream_emulator_block_entity;
import net.dl909.dl_ct.block.entity.item_tp_block_entity;
import net.dl909.dl_ct.block.item_kill_block;
import net.dl909.dl_ct.block.item_stream_emulator_block;
import net.dl909.dl_ct.block.item_tp_block;
import net.dl909.dl_ct.item.item_tp_block_key;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class dl909_creative_tool implements ModInitializer {

	@Override
	public void onInitialize() {
		//block,block entity and block item
		Registry.register(Registries.BLOCK, new Identifier("dl_ct", "item_tp_block"), ITEM_TP_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("dl_ct", "item_tp_block"), new BlockItem(ITEM_KILL_BLOCK,new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("dl_ct","item_kill_block"),ITEM_KILL_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("dl_ct","item_kill_block"),new BlockItem(ITEM_KILL_BLOCK,new Item.Settings()));
		//item
		Registry.register(Registries.ITEM, new Identifier("dl_ct", "item_tp_block_key"), ITEM_TP_BLOCK_KEY);

		//tooled_item
		Registry.register(Registries.ITEM, new Identifier("dl_ct", "tooled_stick"), new Item(new Item.Settings()));
	}

	public static final Block ITEM_TP_BLOCK = new item_tp_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
	public static final BlockEntityType<item_tp_block_entity> ITEM_TP_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier("dl_ct", "item_tp_block_entity"),
			FabricBlockEntityTypeBuilder.create(item_tp_block_entity::new, ITEM_TP_BLOCK).build()
	);


	public static final Block ITEM_KILL_BLOCK = new item_kill_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
	public static final BlockEntityType<item_kill_block_entity> ITEM_KILL_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier("dl_ct", "item_kill_block_entity"),
			FabricBlockEntityTypeBuilder.create(item_kill_block_entity::new, ITEM_KILL_BLOCK).build()
	);

	public static final Block ITEM_STREAM_EMULATOR_BLOCK = new item_stream_emulator_block(AbstractBlock.Settings.of(Material.STONE).noCollision());
	public static  final BlockEntityType<item_stream_emulator_block_entity> ITEM_STREAM_EMULATOR_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier("dl_ct","item_stream_emulator_block"),
			FabricBlockEntityTypeBuilder.create(item_stream_emulator_block_entity::new,ITEM_STREAM_EMULATOR_BLOCK).build()
	);

	//item
	public static final Item ITEM_TP_BLOCK_KEY = new item_tp_block_key(new Item.Settings().maxCount(1));
}