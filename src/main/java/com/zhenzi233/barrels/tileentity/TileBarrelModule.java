package com.zhenzi233.barrels.tileentity;

import com.zhenzi233.barrels.blocks.BlockBarrelModule;
import com.zhenzi233.barrels.client.ModelHelper;
import knightminer.ceramics.blocks.BlockBarrel;
import knightminer.ceramics.library.tank.BarrelTank;
import knightminer.ceramics.network.BarrelSizeChangedPacket;
import knightminer.ceramics.network.CeramicsNetwork;
import knightminer.ceramics.tileentity.TileBarrel;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;

public class TileBarrelModule extends TileBarrel {

    public static final String BLOCK_TAG = "textureBlock";

    public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state) {
        String texture = this.getTileData().getString("texture");
        if (texture.isEmpty()) {
            ItemStack stack = new ItemStack(this.getTileData().getCompoundTag("textureBlock"));
            if (!stack.isEmpty()) {
                Block block = Block.getBlockFromItem(stack.getItem());
                texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
                this.getTileData().setString("texture", texture);
            }
        }

        if (!texture.isEmpty()) {
            state = state.withProperty(BlockBarrelModule.TEXTURE, texture);
        }

        return state;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags = super.writeToNBT(tags);
        return tags;
    }

    public void updateTextureBlock(NBTTagCompound tag) {
        this.getTileData().setTag("textureBlock", tag);
    }

    public NBTTagCompound getTextureBlock() {
        return this.getTileData().getCompoundTag("textureBlock");
    }

    @Override
    public void checkBarrelStructure() {
        if (!this.world.isRemote) {
            Block teBlock = this.getBlockType();
            if (teBlock instanceof BlockBarrelModule) {
                BlockBarrelModule barrel = (BlockBarrelModule) teBlock;
                TileBarrelModule te = (TileBarrelModule) this.world.getTileEntity(pos);

                BlockPos topPos;
                for(topPos = this.pos.up(); barrel.isValidExtension(world.getBlockState(topPos)); topPos = topPos.up()) {
                    TileEntity teEx = this.world.getTileEntity(topPos);
                    if (te != null && teEx instanceof TileBarrelExtensionModule && checkTag(te.getTileData(), teEx.getTileData())) {
                        ((TileBarrelExtension)teEx).setMaster(this.pos);
                    }
                }

                this.height = topPos.down().getY() - this.pos.getY();
//                int newCapacity = this.baseCapacity * (this.height + 1);
//                if (newCapacity != this.capacity) {
//                    this.capacity = newCapacity;
//                    this.tank.setCapacity(newCapacity);
//                    this.onTankContentsChanged();
//                    CeramicsNetwork.sendToAllAround(this.world, this.pos, new BarrelSizeChangedPacket(this.pos, this.capacity, this.height));
//                }

            }
        }
    }

    public boolean checkTag(NBTTagCompound tag, NBTTagCompound topTag)
    {
        NBTTagCompound tagTexture = tag.getCompoundTag("textureBlock");
        NBTTagCompound topTagTexture = topTag.getCompoundTag("textureBlock");
        String id = tagTexture.getString("id");
        String topId = topTagTexture.getString("id");
        if (tag.hasNoTags() && topTag.hasNoTags())
        {
            return false;
        }
        return id.equals(topId);
    }



}
