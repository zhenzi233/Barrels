package com.zhenzi233.barrels.library;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;

public class ClayBowlWrapper extends FluidHandlerItemStack {
    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public ClayBowlWrapper(@Nonnull ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.getCount() != 1 || maxDrain <= 0)
        {
            return null;
        }

        FluidStack contained = getFluid();
        if (contained == null || contained.amount <= 0 || !canDrainFluidType(contained))
        {
            return null;
        }

        final int drainAmount = Math.min(contained.amount, maxDrain);

        FluidStack drained = contained.copy();
        drained.amount = drainAmount;

        if (doDrain)
        {
            contained.amount -= drainAmount;
            if (contained.amount == 0)
            {
                if (canBroken(container))
                {
                    container.shrink(1);
                }   else
                {
                    setContainerToEmpty();
                }
            }
            else
            {
                setFluid(contained);
            }
        }

        return drained;
    }

    public boolean canBroken(ItemStack stack)
    {
        FluidStack fluidStack = Util.getFluidStackFromBowl(stack.getTagCompound());
        if (fluidStack != null)
        {
            return fluidStack.getFluid().getTemperature() > 450;
        }
        return false;
    }

    private void spawnParticle(EntityPlayer player)
    {
        player.renderBrokenItemStack(this.container);
    }

}
