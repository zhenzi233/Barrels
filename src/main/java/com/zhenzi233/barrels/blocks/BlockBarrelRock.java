package com.zhenzi233.barrels.blocks;

import com.zhenzi233.barrels.Barrels;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.BlockStone;
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

public class BlockBarrelRock extends BlockBarrel {
    private final boolean extension;

    public BlockBarrelRock(boolean extension) {
        super(Material.ROCK);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE));
        this.extension = extension;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(isExtension(getStateFromMeta(meta))) {
            return new TileBarrelExtension();
        }
        return new TileBarrel(Fluid.BUCKET_VOLUME * 6);
    }

    @Override
    public boolean isValidExtension(IBlockState state) {
        return state.getBlock() == Barrels.BARREL_ROCK_EXTENSION;
    }

    @Override
    public boolean isExtension(IBlockState state) {
        return extension;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockStone.EnumType type : BlockStone.EnumType.values())
        {
            list.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.byMetadata((meta)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockStone.VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {BlockStone.VARIANT});
    }
}
