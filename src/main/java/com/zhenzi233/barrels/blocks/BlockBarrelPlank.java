package com.zhenzi233.barrels.blocks;

import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockBarrelPlank extends BlockBarrelLog{
    public BlockBarrelPlank(boolean extension) {
        super(extension);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(isExtension(getStateFromMeta(meta))) {
            return new TileBarrelExtension();
        }
        return new TileBarrel(Fluid.BUCKET_VOLUME * 2);
    }
}
