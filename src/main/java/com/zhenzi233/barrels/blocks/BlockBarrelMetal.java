package com.zhenzi233.barrels.blocks;

import com.zhenzi233.barrels.Barrels;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelBase;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBarrelMetal extends BlockBarrel {
    public static final PropertyEnum<BlockBarrelMetal.EnumType> VARIANT = PropertyEnum.create("variant", BlockBarrelMetal.EnumType.class);
    private final boolean extension;

    public BlockBarrelMetal(boolean extension) {
        super(Material.IRON);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(VARIANT, EnumType.IRON));
        this.extension = extension;
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
        return state.getBlock() == Barrels.BARREL_METAL_EXTENSION;
    }

    @Override
    public boolean isExtension(IBlockState state) {
        return extension;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack handItem = player.getHeldItem(hand);
        if (handItem.getItem().equals(Item.getItemFromBlock(Barrels.COVER)) && state.getBlock() instanceof BlockBarrelMetal)
        {
            TileEntity oldTe = world.getTileEntity(pos);
            NBTTagCompound nbt = oldTe.getUpdateTag();
            world.setBlockState(pos, Barrels.BARREL_METAL_COVER.getDefaultState().withProperty(VARIANT, state.getValue(VARIANT)));
            TileEntity newTe = world.getTileEntity(pos);
            if (newTe != null)
            {
                newTe.readFromNBT(nbt);
            }
            handItem.shrink(1);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    /* Special Drops System */

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te;
        if (!this.isExtension(state)) {
            te = world.getTileEntity(pos);
            if (te instanceof TileBarrel) {
                ((TileBarrel)te).checkBarrelStructure();
            }
        } else {
            te = world.getTileEntity(pos.down());
            if (te instanceof TileBarrelBase) {
                ((TileBarrelBase)te).checkBarrelStructure();
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
    }

    /* type */
    public static enum EnumType implements IStringSerializable
    {
        IRON(0, MapColor.IRON, "iron", true),
        GOLD(1, MapColor.GOLD, "gold", true),
        DIAMOND(2, MapColor.DIAMOND, "diamond",  true),
        OBSIDIAN(3, MapColor.OBSIDIAN, "obsidian", true),
        EMERALD(4, MapColor.EMERALD, "emerald",  true);

        /** Array of the Block's BlockStates */
        private static final BlockBarrelMetal.EnumType[] META_LOOKUP = new BlockBarrelMetal.EnumType[values().length];
        /** The BlockState's metadata. */
        private final int meta;
        /** The EnumType's name. */
        private final String name;
        private final String unlocalizedName;
        private final MapColor mapColor;
        private final boolean isNatural;

        private EnumType(int index, MapColor mapColor, String name, boolean isNatural)
        {
            this(index, mapColor, name, name, isNatural);
        }

        private EnumType(int index, MapColor mapColor, String name, String unlocalizedName, boolean isNatural)
        {
            this.meta = index;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
            this.mapColor = mapColor;
            this.isNatural = isNatural;
        }

        /**
         * Returns the EnumType's metadata value.
         */
        public int getMetadata()
        {
            return this.meta;
        }

        public MapColor getMapColor()
        {
            return this.mapColor;
        }

        public String toString()
        {
            return this.name;
        }

        /**
         * Returns an EnumType for the BlockState from a metadata value.
         */
        public static BlockBarrelMetal.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        public boolean isNatural()
        {
            return this.isNatural;
        }

        static
        {
            for (BlockBarrelMetal.EnumType blockmetal$enumtype : values())
            {
                META_LOOKUP[blockmetal$enumtype.getMetadata()] = blockmetal$enumtype;
            }
        }
    }
}
