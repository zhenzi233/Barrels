package com.zhenzi233.barrels.tileentity;

import knightminer.ceramics.library.tank.BarrelTank;
import knightminer.ceramics.tileentity.TileBarrel;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

import static net.minecraft.block.BlockFire.AGE;

public class TileBarrelLog extends TileBarrel implements ITickable {

    private static final int BASE_CAPACITY = Fluid.BUCKET_VOLUME * 4;

    public TileBarrelLog() {
        this(BASE_CAPACITY);
    }

    public TileBarrelLog(int baseCapacity) {
        super(baseCapacity);
    }

    @Override
    public void update() {
        if (!world.isRemote)
        {
            BarrelTank tank = this.getTank();
            FluidStack fluidStack = tank.getFluid();
            Random rand = world.rand;

            if (fluidStack != null)
            {
                Fluid fluid = fluidStack.getFluid();
                if (fluid == FluidRegistry.LAVA)
                {
                    boolean flag1 = this.tryCatchFire(world, pos.east(), 300, rand, 10, EnumFacing.WEST, false);
                    boolean flag2 = this.tryCatchFire(world, pos.west(), 300, rand, 10, EnumFacing.EAST, false);
                    boolean flag3 = this.tryCatchFire(world, pos.down(), 250, rand, 10, EnumFacing.UP, false);
                    boolean flag4 = this.tryCatchFire(world, pos.up(), 250, rand, 10, EnumFacing.DOWN, false);
                    boolean flag5 = this.tryCatchFire(world, pos.north(), 300, rand, 10, EnumFacing.SOUTH, false);
                    boolean flag6 = this.tryCatchFire(world, pos.south(), 300, rand, 10, EnumFacing.NORTH, false);

                    if (!flag1 && !flag2 && !flag3 && !flag4 && !flag5 && !flag6)
                    {
                        this.tryCatchFire(world, pos.up(), 300, rand, 10, EnumFacing.UP, true);
                        this.tryCatchFire(world, pos.west(), 300, rand, 10, EnumFacing.WEST, true);
                        this.tryCatchFire(world, pos.east(), 300, rand, 10, EnumFacing.EAST, true);
                        this.tryCatchFire(world, pos.north(), 300, rand, 10, EnumFacing.NORTH, true);
                        this.tryCatchFire(world, pos.south(), 300, rand, 10, EnumFacing.SOUTH, true);
                    }
                }
            }
        }
    }

    private boolean tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face, boolean canCreateOnAir)
    {
        int i = worldIn.getBlockState(pos).getBlock().getFlammability(worldIn, pos, face);

        if (random.nextInt(chance) < i)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == Blocks.TNT)
            {
                Blocks.TNT.onBlockDestroyedByPlayer(worldIn, pos, iblockstate.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));

                return true;
            }

            if (catchFire(worldIn, random, age, pos))
            {
                return true;
            }
        }

        if (random.nextInt(chance) < 5 && canCreateOnAir && worldIn.getBlockState(pos).getBlock().equals(Blocks.AIR))
        {
            return catchFire(worldIn, random, age, pos);
        }
        return false;
    }

    private boolean catchFire(World worldIn, Random random, int age, BlockPos pos)
    {
        if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos))
        {
            int j = age + random.nextInt(5) / 4;

            if (j > 15)
            {
                j = 15;
            }

            worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState().withProperty(AGE, Integer.valueOf(j)), 3);

            return true;
        }

        return false;
    }

}
