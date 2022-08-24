package com.zhenzi233.barrels.items;

import com.zhenzi233.barrels.Barrels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;

public class ItemLog extends ItemCloth {
    private final Block block;
    public ItemLog(Block block) {
        super(block);
        this.block = block;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (this.block == Barrels.BARREL_ROCK || this.block == Barrels.BARREL_ROCK_EXTENSION)
        {
            System.out.println(super.getUnlocalizedName() + "." + BlockStone.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase());
            return super.getUnlocalizedName() + "." + BlockStone.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase();
        }   else {
            System.out.println(super.getUnlocalizedName() + "." + BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase());
            return super.getUnlocalizedName() + "." + BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase();
        }
    }
}
