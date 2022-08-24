package com.zhenzi233.barrels.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;

public class ItemLog extends ItemCloth {
    public ItemLog(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
    }
}
