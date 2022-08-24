package com.zhenzi233.barrels.blocks;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.tileentity.TileBarrelLog;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockBarrelLog extends BlockBarrel {

    private boolean extension;

    public BlockBarrelLog(boolean extension) {
        super(Material.WOOD);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
        this.extension = extension;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(isExtension(getStateFromMeta(meta))) {
            return new TileBarrelExtension();
        }
        return new TileBarrelLog(Fluid.BUCKET_VOLUME * 3);
    }

    @Override
    public boolean isValidExtension(IBlockState state) {
        return state.getBlock() == Barrels.BARREL_LOG_EXTENSION;
    }

    @Override
    public boolean isExtension(IBlockState state) {
        return extension;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values())
        {
            list.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata((meta)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockPlanks.VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {BlockPlanks.VARIANT});
    }

}
