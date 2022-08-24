package com.zhenzi233.barrels.proxy;

import com.zhenzi233.barrels.Barrels;
import knightminer.ceramics.Ceramics;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ProxyBase
{
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values())
        {
            registerBlockModel(Barrels.BARREL_LOG, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_LOG_EXTENSION, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_PLANK, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_PLANK_EXTENSION, type.getMetadata(), "variant=" + type.getName());
        }
    }

    private void registerItemModel(Item item, int meta, String name) {
        if(item != null && item != Items.AIR) {
            // tell Minecraft which textures it has to load. This is resource-domain sensitive
            final ResourceLocation location = item.getRegistryName();

            // tell the game which model to use for this item-meta combination
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, name));
        }
    }

    private void registerBlockModel(Block block, int meta, String name) {
        registerItemModel(Item.getItemFromBlock(block), meta, name);
    }
}
