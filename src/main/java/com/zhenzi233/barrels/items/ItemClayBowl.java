package com.zhenzi233.barrels.items;

import com.zhenzi233.barrels.library.ClayBowlWrapper;
import com.zhenzi233.barrels.library.Util;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static knightminer.ceramics.items.ItemClayBucket.MILK_BUCKET;

public class ItemClayBowl extends Item {
    public ItemClayBowl() {
        this.hasSubtypes = true;
    }

    public boolean hasFluid(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (worldIn.getBlockState(pos).getBlock() == Blocks.CAULDRON && stack.getItem() instanceof ItemClayBowl)
        {
            ItemStack stack1 = interactWithCauldron(player, worldIn, pos, worldIn.getBlockState(pos), stack);
            player.setHeldItem(hand, stack1);
        }
        return EnumActionResult.PASS;
    }
    private ItemStack interactWithCauldron(EntityPlayer player, World world, BlockPos pos, IBlockState state, ItemStack stack) {
        int level = state.getValue(BlockCauldron.LEVEL);
        NBTTagCompound nbt = stack.getTagCompound();
        // if we have a fluid, try filling
        if (Util.nbtIsNotEmpty(nbt)) {
            NBTTagCompound nbtFluid = nbt.getCompoundTag("Fluid");
            if (isSpecialFluidFromBowl(nbt, "water") && Util.getTankAmount(nbtFluid) > 200 && level < 3)
            {
                if (player != null)
                {
                    player.addStat(StatList.CAULDRON_FILLED);
                }
                if (!world.isRemote)
                {
                    Blocks.CAULDRON.setWaterLevel(world, pos, state, ++level);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                return new ItemStack(this);
            }
        }
        else if ((nbt == null || nbt.hasNoTags()) && level > 0)
        {
            if (player != null)
            {
                player.addStat(StatList.CAULDRON_USED);
            }
            if (!world.isRemote)
            {
                Blocks.CAULDRON.setWaterLevel(world, pos, state, --level);
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (stack.getCount() > 1)
            {
                ItemStack stack1 = new ItemStack(this);
                fillBowl(new FluidStack(FluidRegistry.WATER, 250), stack1);
                ItemHandlerHelper.giveItemToPlayer(player, stack1);
                int amount = stack.getCount();
                --amount;
                stack.setCount(amount);
                return stack;
            }
            return fillBowl(new FluidStack(FluidRegistry.WATER, 250), stack);
        }
        return stack;
    }

    public ItemStack fillBowl(FluidStack fluidStack, ItemStack stack)
    {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler != null)
        {
            handler.fill(fluidStack, true);
            return stack;
        }
        return stack;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        // milk we set active and return success, drinking code is done elsewhere
        if (isSpecialFluidFromBowl(stack.getTagCompound(), "milk"))
            {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }


        return super.onItemRightClick(world, player, hand);
    }


    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        // milk has drinking animation
        return isSpecialFluidFromBowl(stack.getTagCompound(), "milk") ? EnumAction.DRINK : EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        // milk requires drinking time
        return isSpecialFluidFromBowl(stack.getTagCompound(), "milk") ? 32 : 0;
    }

    @Override
    @Nullable
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        // must be milk
        if(!Util.isSpecialFluid(stack.getTagCompound(), "milk")) {
            return stack;
        }

        if(entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
            stack = new ItemStack(this);
        }

        if(!worldIn.isRemote) {
            entityLiving.curePotionEffects(MILK_BUCKET);
        }

        if(entityLiving instanceof EntityPlayer) {
            ((EntityPlayer) entityLiving).addStat(StatList.getObjectUseStats(this));
        }

        return stack;
    }
//
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        // only work if the bucket is empty and right clicking a cow
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null && target instanceof EntityCow && !player.capabilities.isCreativeMode) {
            // sound
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);

            // modify items
            // because the action expects mutating the item stack
            if(stack.getCount() == 1 && cap != null) {
                cap.fill(new FluidStack(FluidRegistry.getFluid("milk"), 250), true);
            } else {
                stack.shrink(1);
                ItemStack stack1 = new ItemStack(this);
                IFluidHandlerItem cap1 = stack1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                cap1.fill(new FluidStack(FluidRegistry.getFluid("milk"), 250), true);
                ItemHandlerHelper.giveItemToPlayer(player, stack1);
            }

            return true;
        }
        return false;
    }
    /* itemStack properties */

    @Override
    public int getItemStackLimit(ItemStack stack) {

        // empty stacks to 16
        return hasFluid(stack) ? 1 : 64;
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(Util.nbtIsNotEmpty(nbt) && isSpecialFluidFromBowl(nbt, "lava")) {
            return 5000;
        }
        return 0;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (canBroken(stack)) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(this);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
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

    /* event */

    @SubscribeEvent
    public void onItemDestroyed(PlayerDestroyItemEvent event) {
        ItemStack original = event.getOriginal();
        if(original.getItem() == this) {
            event.getEntityPlayer().renderBrokenItemStack(new ItemStack(this));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ClayBowlWrapper(stack, 250);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (Util.nbtIsNotEmpty(nbt))
        {
            NBTTagCompound nbtFluid = nbt.getCompoundTag("Fluid");
            tooltip.add(Util.getTankInformation(nbtFluid, true, true));
            tooltip.add(Util.getTankInformation(nbtFluid, false, true));
        }
    }

    public boolean isSpecialFluidFromBowl(NBTTagCompound nbt, String name)
    {
        if (Util.nbtIsNotEmpty(nbt))
        {
            NBTTagCompound nbtFluid = nbt.getCompoundTag("Fluid");
            return Util.getTankInformation(nbtFluid, true, false).equals(name);
        }
        return false;
    }
}
