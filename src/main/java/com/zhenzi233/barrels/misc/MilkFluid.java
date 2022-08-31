package com.zhenzi233.barrels.misc;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.library.Util;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;

public class MilkFluid extends Fluid {
    public static String name = "milk";
    public static ResourceLocation still = Util.getResourceLocation("blocks/milk");
    public static ResourceLocation flowing = Util.getResourceLocation("blocks/milk_flow");
    public MilkFluid(String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(Barrels.MODID + "." + fluidName, still, flowing);
    }
}
