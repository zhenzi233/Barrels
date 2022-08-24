package com.zhenzi233.barrels.misc;

import com.zhenzi233.barrels.Barrels;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class CreativeTab extends CreativeTabs {
    private ItemStack icon;

    public CreativeTab(String label, ItemStack icon) {
        super(label);
        this.icon = icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getIconItemStack() {
        return this.icon;
    }

    @Override
    public ItemStack getTabIconItem() {
        return this.icon;
    }
}
