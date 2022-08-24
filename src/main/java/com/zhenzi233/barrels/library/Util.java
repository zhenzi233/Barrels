package com.zhenzi233.barrels.library;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
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

    public static void printLang()
    {
    }
}
