package com.zhenzi233.barrels;

import com.zhenzi233.barrels.blocks.BlockBarrelLog;
import com.zhenzi233.barrels.blocks.BlockBarrelPlank;
import com.zhenzi233.barrels.blocks.BlockBarrelRock;
import com.zhenzi233.barrels.items.ItemClayBowl;
import com.zhenzi233.barrels.items.ItemLog;
import com.zhenzi233.barrels.library.Util;
import com.zhenzi233.barrels.proxy.ProxyBase;
import com.zhenzi233.barrels.tileentity.TileBarrelLog;
import knightminer.ceramics.Ceramics;
import knightminer.ceramics.library.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Barrels.MODID, name = Barrels.NAME, version = Barrels.VERSION, dependencies = "required-after:ceramics")
public class Barrels
{
    public static final String MODID = "barrels";
    public static final String NAME = "More Barrels For ASDUST";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @SidedProxy(clientSide = "com.zhenzi233.barrels.proxy.ClientProxy", serverSide = "com.zhenzi233.barrels.proxy.ServerProxy")
    public static ProxyBase proxy;

    public static CreativeTab tab = new CreativeTab(MODID, new ItemStack(Items.BRICK));

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        Blocks.FIRE.setFireInfo(BARREL_LOG, 5, 20);
        Blocks.FIRE.setFireInfo(BARREL_LOG_EXTENSION, 5, 20);
        Blocks.FIRE.setFireInfo(BARREL_PLANK, 20, 40);
        Blocks.FIRE.setFireInfo(BARREL_PLANK_EXTENSION, 20, 40);

        Util.initFluidNamesToLocalName();
    }

    public static Block BARREL_LOG = null;
    public static Block BARREL_LOG_EXTENSION = null;
    public static Block BARREL_PLANK = null;
    public static Block BARREL_PLANK_EXTENSION = null;
    public static Block BARREL_ROCK = null;
    public static Block BARREL_ROCK_EXTENSION = null;
    public static Item CLAY_BOWL = null;

    @Mod.EventBusSubscriber(modid=MODID)
    public static class Registration {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            IForgeRegistry<Block> r = event.getRegistry();

            BARREL_LOG = registerBlock(r, new BlockBarrelLog(false), "barrel_log");
            BARREL_LOG_EXTENSION = registerBlock(r, new BlockBarrelLog(true), "barrel_log_extension");
            BARREL_PLANK = registerBlock(r, new BlockBarrelPlank(false), "barrel_plank");
            BARREL_PLANK_EXTENSION = registerBlock(r, new BlockBarrelPlank(true), "barrel_plank_extension");
            BARREL_ROCK = registerBlock(r, new BlockBarrelRock(false), "barrel_rock");
            BARREL_ROCK_EXTENSION = registerBlock(r, new BlockBarrelRock(true), "barrel_rock_extension");

            registerTE(TileBarrelLog.class, "barrel_log");

        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            IForgeRegistry<Item> r = event.getRegistry();

            Ceramics.registerItemBlock(r, new ItemLog(BARREL_LOG));
            Ceramics.registerItemBlock(r, new ItemLog(BARREL_LOG_EXTENSION));
            Ceramics.registerItemBlock(r, new ItemLog(BARREL_PLANK));
            Ceramics.registerItemBlock(r, new ItemLog(BARREL_PLANK_EXTENSION));
            Ceramics.registerItemBlock(r, new ItemLog(BARREL_ROCK));
            Ceramics.registerItemBlock(r, new ItemLog(BARREL_ROCK_EXTENSION));

            CLAY_BOWL = registerItem(r, new ItemClayBowl(), "clay_bowl");
        }
    }

    public static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
        block.setUnlocalizedName(MODID + "." + name);
        block.setRegistryName(new ResourceLocation(MODID, name));
        block.setCreativeTab(tab);

        registry.register(block);
        return block;
    }

    public static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
        item.setUnlocalizedName(MODID + "." + name);
        item.setRegistryName(new ResourceLocation(MODID, name));
        item.setCreativeTab(tab);

        registry.register(item);

        return item;
    }

    protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
        GameRegistry.registerTileEntity(teClazz, MODID + "." + name);
    }

}
