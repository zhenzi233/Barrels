package com.zhenzi233.barrels.blocks;

import com.zhenzi233.barrels.Barrels;
import com.zhenzi233.barrels.client.PropertyString;
import com.zhenzi233.barrels.library.Util;
import com.zhenzi233.barrels.tileentity.TileBarrelExtensionModule;
import com.zhenzi233.barrels.tileentity.TileBarrelModule;
import knightminer.ceramics.Ceramics;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.library.Config;
import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class BlockBarrelModule extends BlockBarrel {
    public static final PropertyString TEXTURE = new PropertyString("texture");

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return (TileEntity)(this.isExtension(this.getStateFromMeta(meta)) ? new TileBarrelExtensionModule() : new TileBarrelModule());
    }

    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{EXTENSION}, new IUnlistedProperty[]{TEXTURE});
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState)state;
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileBarrelModule) {
            TileBarrelModule barrel = (TileBarrelModule)te;
            return barrel.writeExtendedBlockState(extendedState);
        } else if (te != null && te instanceof TileBarrelExtensionModule)
        {
            TileBarrelExtensionModule extension = (TileBarrelExtensionModule) te;
            return extension.writeExtendedBlockState(extendedState);
        }   else
        {
            return super.getExtendedState(state, world, pos);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        NBTTagCompound tag = stack.getTagCompound();
        if (Util.nbtIsNotEmpty(tag))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileBarrelModule) {
                TileBarrelModule barrel = (TileBarrelModule) te;
                NBTTagCompound feetTag = tag.getCompoundTag("textureBlock");
                if (feetTag == null) {
                    feetTag = new NBTTagCompound();
                }

                barrel.updateTextureBlock(feetTag);
            }
            if (te != null && te instanceof TileBarrelExtensionModule) {
                TileBarrelExtensionModule extension = (TileBarrelExtensionModule) te;
                NBTTagCompound feetTag = tag.getCompoundTag("textureBlock");
                if (feetTag == null) {
                    feetTag = new NBTTagCompound();
                }

                extension.updateTextureBlock(feetTag);
            }
        }
    }

    private ItemStack writeDataOntoItemStack(@Nonnull ItemStack item, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, TileEntity te) {
        if (te != null && te instanceof TileBarrelModule) {
            TileBarrelModule barrel = (TileBarrelModule) te;
            NBTTagCompound tag = item.getTagCompound();
            if (!Util.nbtIsNotEmpty(tag))
            {
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                NBTTagCompound data = barrel.getTextureBlock();
                if (!data.hasNoTags()) {
                    tag.setTag("textureBlock", data);
                }

                if (!tag.hasNoTags()) {
                    item.setTagCompound(tag);
                }
            }

            return item;
        }
        if (te != null && te instanceof TileBarrelExtensionModule) {
            TileBarrelExtensionModule extension = (TileBarrelExtensionModule) te;
            NBTTagCompound tag = item.getTagCompound();
            if (!Util.nbtIsNotEmpty(tag))
            {
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                NBTTagCompound data = extension.getTextureBlock();
                if (!data.hasNoTags()) {
                    tag.setTag("textureBlock", data);
                }

                if (!tag.hasNoTags()) {
                    item.setTagCompound(tag);
                }
            }

            return item;
        }
        return item;
    }

    /* ItemStack */

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        Block block = worldIn.getBlockState(pos).getBlock();

        if (tileentity instanceof TileBarrelModule && block.equals(Blocks.AIR))
        {
            TileBarrelModule tileBarrel = (TileBarrelModule) tileentity;

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
            ItemStack dropStack = this.writeDataOntoItemStack(itemstack, worldIn, pos, state, tileBarrel);

            spawnAsEntity(worldIn, pos, dropStack);

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }

        if (tileentity instanceof TileBarrelExtensionModule && block.equals(Blocks.AIR))
        {
            TileBarrelExtensionModule tileBarrel = (TileBarrelExtensionModule) tileentity;

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
            ItemStack dropStack = this.writeDataOntoItemStack(itemstack, worldIn, pos, state, tileBarrel);
            dropStack.setItemDamage(1);

            spawnAsEntity(worldIn, pos, dropStack);

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }

        super.breakBlock(worldIn, pos, state);

    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        Block block = world.getBlockState(pos).getBlock();

        if (tileentity instanceof TileBarrelModule)
        {
            TileBarrelModule tileBarrel = (TileBarrelModule) tileentity;

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));

            return this.writeDataOntoItemStack(itemstack, world, pos, state, tileBarrel);
        }

        if (tileentity instanceof TileBarrelExtensionModule)
        {
            TileBarrelExtensionModule tileBarrel = (TileBarrelExtensionModule) tileentity;

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));

            return this.writeDataOntoItemStack(itemstack, world, pos, state, tileBarrel);
        }

        return getItem(world, pos, state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        // toolforge has custom blocks
        list.add(createItemStack(this, 0, Blocks.COAL_BLOCK, 0));
        list.add(createItemStack(this, 0, Blocks.GLASS, 0));
        list.add(createItemStack(this, 1, Blocks.COAL_BLOCK, 1));
        list.add(createItemStack(this, 1, Blocks.GLASS, 1));
    }

    public static ItemStack createItemStack(BlockBarrelModule barrel, int barrelMeta, Block block, int blockMeta) {
        ItemStack stack = new ItemStack(barrel, 1, barrelMeta);
        if (block != null) {
            ItemStack blockStack = new ItemStack(block, 1, blockMeta);
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound subTag = new NBTTagCompound();
            blockStack.writeToNBT(subTag);
            tag.setTag("textureBlock", subTag);
            stack.setTagCompound(tag);
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (Util.nbtIsNotEmpty(nbt))
        {
            NBTTagCompound blockTag = nbt.getCompoundTag("textureBlock");
            if (Util.nbtIsNotEmpty(blockTag))
            {
                String name = blockTag.getString("id");
                Block block = Block.getBlockFromName(name);
                if (block != null)
                {
                    tooltip.add(block.getLocalizedName());
                }
            }
        }
    }

    /* barrel */
    @Override
    public boolean isValidExtension(IBlockState state) {
        return state.getBlock() == Barrels.BARREL_MODULE && (Boolean)state.getValue(EXTENSION);
    }
}
