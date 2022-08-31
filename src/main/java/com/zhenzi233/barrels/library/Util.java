package com.zhenzi233.barrels.library;

import com.zhenzi233.barrels.Barrels;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Util {
    public static Map<String, String> FluidNamesToLocalName = new HashMap<>();

    public static void initFluidNamesToLocalName()
    {
        Map<String, Fluid> map = FluidRegistry.getRegisteredFluids();

        for (Map.Entry<String, Fluid> entry : map.entrySet())
        {
            Fluid fluid = entry.getValue();
            if (fluid != null)
            {
                FluidStack fluidStack = new FluidStack(fluid, 100);
                String[] unlocalizedName = fluid.getUnlocalizedName(fluidStack).split("\\.");
                FluidNamesToLocalName.put(unlocalizedName[2], fluid.getLocalizedName(fluidStack));
            }
        }
    }

    public static String getLocalizedNameFromUnlocalizedNameInFluid(String name)
    {
        for (Map.Entry<String, String> entry : FluidNamesToLocalName.entrySet())
        {
            if (name.equals(entry.getKey()))
            {
                return entry.getValue();
            }
        }
        return "fail! need to report author!";
    }

    public static void reloadFluidNamesToLocalName()
    {
        FluidNamesToLocalName.clear();
        initFluidNamesToLocalName();
    }

    @Deprecated
    public static void printLang()
    {

    }

    public static Block getModuleBlock(String resourceName)
    {
        return Block.REGISTRY.getObject(new ResourceLocation(resourceName));
    }

    public static ResourceLocation getResourceLocation(String location)
    {
        return new ResourceLocation(Barrels.MODID + ":" + location);
    }

    public static int getTankAmount(NBTTagCompound nbtTank)
    {
        NBTTagCompound tank = nbtTank;
        if (tank != null && !tank.hasNoTags())
        {
            return tank.getInteger("Amount");
        }
        return -1;
    }

    public static String getTankInformation(NBTTagCompound nbtTank, boolean isName, boolean isTranslated)
    {
        NBTTagCompound tank = nbtTank;
        if (tank != null && !tank.hasNoTags())
        {
            String name = tank.getString("FluidName");
            int amount = tank.getInteger("Amount");
            if (!name.isEmpty() && !(amount <= 0))
            {
                if (isName)
                {
                    if (isTranslated)
                    {
                        return Util.getLocalizedNameFromUnlocalizedNameInFluid(name);
                    }   else
                    {
                        return name;
                    }
                }   else
                {
                    String number = amount + "mb";
                    return number;
                }
            }
        }
        return "";
    }

    public static boolean isSpecialFluid(NBTTagCompound nbt, String name)
    {
        if (nbt != null && !nbt.hasNoTags())
        {
            return Util.getTankInformation(nbt, true, false).equals(name);
        }
        return false;
    }

    public static boolean nbtIsNotEmpty(NBTTagCompound nbt)
    {
        return nbt != null && !nbt.hasNoTags();
    }

    public static FluidStack getFluidStackFromBowl(NBTTagCompound nbt)
    {
        if (Util.nbtIsNotEmpty(nbt))
        {
            return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
        }
        return null;
    }

    public static Logger getLogger(String type) {
        String log = Barrels.MODID;

        return LogManager.getLogger(log + "-" + type);
    }

    public static String RESOURCE = "barrels".toLowerCase(Locale.US);
    public static String resource(String res) {
        return String.format("%s:%s", RESOURCE, res);
    }
}
