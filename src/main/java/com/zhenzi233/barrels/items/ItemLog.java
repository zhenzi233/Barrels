package com.zhenzi233.barrels.items;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.blocks.BlockBarrelMetal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
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
            return super.getUnlocalizedName() + "." + BlockStone.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase();
        }   else if (this.block == Barrels.BARREL_METAL || this.block == Barrels.BARREL_METAL_EXTENSION || this.block == Barrels.BARREL_METAL_COVER)
        {
            return super.getUnlocalizedName() + "." + BlockBarrelMetal.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase();
        }   else
        {
            return super.getUnlocalizedName() + "." + BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName().toLowerCase();
        }
    }
}
