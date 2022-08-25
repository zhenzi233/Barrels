package com.zhenzi233.barrels.proxy;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.blocks.BlockBarrelMetal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class ClientProxy extends ProxyBase
{
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init() {
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

        for (BlockStone.EnumType type : BlockStone.EnumType.values())
        {
            registerBlockModel(Barrels.BARREL_ROCK, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_ROCK_EXTENSION, type.getMetadata(), "variant=" + type.getName());
        }

        for (BlockBarrelMetal.EnumType type : BlockBarrelMetal.EnumType.values())
        {
            registerBlockModel(Barrels.BARREL_METAL, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_METAL_EXTENSION, type.getMetadata(), "variant=" + type.getName());
            registerBlockModel(Barrels.BARREL_METAL_COVER, type.getMetadata(), "variant=" + type.getName());
        }
        registerBlockModel(Barrels.COVER, 0, "");

        registerItemMeshModel(Barrels.CLAY_BOWL);
        registerItemModel(Barrels.UNFIRED_CLAY_BOWL, 0, "");
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

    private void registerItemMeshModel(Item item) {
        if (item != null && item != Items.AIR) {
            registerItemMeshModel(item, item.getRegistryName());
        }
    }

    private void registerItemMeshModel(Item item, final ResourceLocation location) {
        if(item != null && item != Items.AIR) {
            // so all meta get the item model
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
                @Nonnull
                @Override
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
                    return new ModelResourceLocation(location, "inventory");
                }
            });
            ModelLoader.registerItemVariants(item, location);
        }
    }
}
