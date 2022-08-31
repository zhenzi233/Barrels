package com.zhenzi233.barrels.tileentity;

import com.zhenzi233.barrels.blocks.BlockBarrelModule;
import com.zhenzi233.barrels.client.ModelHelper;
import knightminer.ceramics.tileentity.TileBarrelExtension;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;

public class TileBarrelExtensionModule extends TileBarrelExtension {
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
}
