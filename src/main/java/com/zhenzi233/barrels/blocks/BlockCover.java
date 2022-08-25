package com.zhenzi233.barrels.blocks;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCover extends BlockGlass {

    protected static final AxisAlignedBB AABB_GLASS = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1 - 0.0625, 0.0625, 1 - 0.0625);
    protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.4375, 0.0625, 0.4375, 1 - 0.4375, 0.125, 1 - 0.4375);


    public BlockCover() {
        super(Material.GLASS, true);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_GLASS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_TOP);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return AABB_GLASS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_GLASS;
    }
}
