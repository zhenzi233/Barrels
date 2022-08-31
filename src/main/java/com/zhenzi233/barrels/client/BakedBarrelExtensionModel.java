package com.zhenzi233.barrels.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zhenzi233.barrels.blocks.BlockBarrelModule;
import com.zhenzi233.barrels.library.Util;
import com.zhenzi233.barrels.tileentity.TileBarrelExtensionModule;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;

public class BakedBarrelExtensionModel extends BakedModelWrapper<IBakedModel> {

    private static final Logger log = Util.getLogger("Barrel Model");

    private final IModel barrelModel;

    private final Map<String, IBakedModel> cache = Maps.newHashMap();

    private static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> {
        assert location != null;
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    };

    private final VertexFormat format;

    public BakedBarrelExtensionModel(IBakedModel originalModel, IModel barrelModel, VertexFormat format) {
        super(originalModel);
        this.barrelModel = barrelModel;
        this.format = format;
    }

    protected IBakedModel getActualModel(String texture) {
        IBakedModel bakedModel = originalModel;

        if (texture != null) {
            if (cache.containsKey(texture)) {
                bakedModel = cache.get(texture);
            } else if (barrelModel != null) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                builder.put("texture", texture);
                IModel retexturedModel = barrelModel.retexture(builder.build());
                IModelState modelState = retexturedModel.getDefaultState();

                bakedModel = retexturedModel.bake(modelState, format, textureGetter);
                cache.put(texture, bakedModel);
            }
        }

        return bakedModel;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return originalModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        // get texture from state
        String texture = null;

        if(state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            if(extendedState.getUnlistedNames().contains(BlockBarrelModule.TEXTURE)) {
                texture = extendedState.getValue(BlockBarrelModule.TEXTURE);
            }

            // remove all world specific data
            // This is so that the next call to getQuads from the transformed TRSRModel doesn't do this again
            // otherwise table models inside table model items recursively calls this with the state of the original table
            state = extendedState;
        }

        // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
        if(texture == null) {
            return originalModel.getQuads(state, side, rand);
        }

        // the model returned by getActualModel should be a simple model with no special handling
        return getActualModel(texture).getQuads(state, side, rand);
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return BakedBarrelExtensionModel.BarrelItemOverrideList.INSTANCE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> pair = originalModel.handlePerspective(cameraTransformType);
        return Pair.of(this, pair.getRight());
    }

    private static class BarrelItemOverrideList extends ItemOverrideList {

        static BarrelItemOverrideList INSTANCE = new BarrelItemOverrideList();

        private BarrelItemOverrideList() {
            super(ImmutableList.of());
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if(originalModel instanceof BakedBarrelExtensionModel) {
                // read out the data on the itemstack
                NBTTagCompound tag = stack.getTagCompound();
                if (Util.nbtIsNotEmpty(tag))
                {
                    ItemStack blockStack = new ItemStack(tag.getCompoundTag(TileBarrelExtensionModule.BLOCK_TAG));
                    if(!blockStack.isEmpty()) {
                        // get model from data
                        Block block = Block.getBlockFromItem(blockStack.getItem());
                        String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();
                        return ((BakedBarrelExtensionModel) originalModel)
                                .getActualModel(texture);
                    }
                }
            }
            return originalModel;
        }
    }
}
