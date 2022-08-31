package com.zhenzi233.barrels.blocks;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.library.Util;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBarrelCover extends BlockBarrel {
    protected static final AxisAlignedBB AABB_GLASS = new AxisAlignedBB(0.0625, 1, 0.0625, 1 - 0.0625, 1 + 0.0625, 1 - 0.0625);
    protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.4375, 1+ 0.0625, 0.4375, 1 - 0.4375,  1 + 0.125, 1 - 0.4375);
    public static final PropertyEnum<BlockBarrelMetal.EnumType> VARIANT = PropertyEnum.create("variant", BlockBarrelMetal.EnumType.class);

    public BlockBarrelCover() {
        super(Material.IRON);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(VARIANT, BlockBarrelMetal.EnumType.IRON));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(isExtension(getStateFromMeta(meta))) {
            return new TileBarrelExtension();
        }
        switch (meta)
        {
            case 1: return new TileBarrel(Fluid.BUCKET_VOLUME * 12);
            case 2: return new TileBarrel(Fluid.BUCKET_VOLUME * 16);
            case 3: return new TileBarrel(Fluid.BUCKET_VOLUME * 18);
            case 4: return new TileBarrel(Fluid.BUCKET_VOLUME * 20);
            default: return new TileBarrel(Fluid.BUCKET_VOLUME * 8);
        }
    }

    @Override
    public boolean isValidExtension(IBlockState state) {
        return false;
    }

    @Override
    public boolean isExtension(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack handItem = player.getHeldItem(hand);
        if (player.isSneaking() && handItem.isEmpty())
        {
            TileEntity oldTe = world.getTileEntity(pos);
            NBTTagCompound nbt = oldTe.getUpdateTag();
            world.setBlockState(pos, Barrels.BARREL_METAL.getDefaultState().withProperty(VARIANT, state.getValue(VARIANT)));
            TileEntity newTe = world.getTileEntity(pos);
            if (newTe != null)
            {
                newTe.readFromNBT(nbt);
            }
            player.inventory.addItemStackToInventory(new ItemStack(Barrels.COVER));
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    /* Special Drops System */

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        Block block = worldIn.getBlockState(pos).getBlock();

        if (tileentity instanceof TileBarrel && block.equals(Blocks.AIR))
        {
            TileBarrel tileBarrel = (TileBarrel)tileentity;

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("container", tileBarrel.getUpdateTag());
            itemstack.setTagCompound(nbttagcompound);
            itemstack.setItemDamage(this.getMetaFromState(state));

            spawnAsEntity(worldIn, pos, itemstack);

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }

        super.breakBlock(worldIn, pos, state);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt != null && !nbt.hasNoTags())
        {
            NBTTagCompound coNbt = nbt.getCompoundTag("container");
            NBTTagCompound tank = coNbt.getCompoundTag("tank");
            tooltip.add(Util.getTankInformation(tank, true, true));
            tooltip.add(Util.getTankInformation(tank, false, true));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te;
        te = world.getTileEntity(pos);
        if (te instanceof TileBarrel)
        {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null && !nbt.hasNoTags()) {
                NBTTagCompound coNbt = nbt.getCompoundTag("container");
                coNbt.setInteger("x", pos.getX());
                coNbt.setInteger("y", pos.getY());
                coNbt.setInteger("z", pos.getZ());
                te.readFromNBT(coNbt);
            }
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        ItemStack itemstack = super.getItem(worldIn, pos, state);
        TileBarrel tileBarrel = (TileBarrel) worldIn.getTileEntity(pos);
        NBTTagCompound nbttagcompound = tileBarrel.getUpdateTag();

        if (!nbttagcompound.hasNoTags())
        {
            itemstack.setTagInfo("container", nbttagcompound);
        }

        return itemstack;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockBarrelMetal.EnumType type : BlockBarrelMetal.EnumType.values())
        {
            list.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, BlockBarrelMetal.EnumType.byMetadata((meta)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {VARIANT});
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
        super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, p_185477_7_);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_GLASS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_TOP);
    }
}
