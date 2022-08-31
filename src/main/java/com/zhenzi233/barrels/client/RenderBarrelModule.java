package com.zhenzi233.barrels.client;

import com.zhenzi233.barrels.tileentity.TileBarrelModule;
import knightminer.ceramics.client.RenderUtils;
import knightminer.ceramics.library.tank.BarrelTank;
import knightminer.ceramics.tileentity.TileBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class RenderBarrelModule extends FastTESR<TileBarrelModule> {
    public static Minecraft mc = Minecraft.getMinecraft();

    public RenderBarrelModule() {
    }

    public void renderTileEntityFast(@Nonnull TileBarrelModule barrel, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
        BarrelTank tank = barrel.getTank();
        FluidStack fluid = tank.getFluid();
        if (fluid != null) {
            BlockPos pos = barrel.getPos();
            int blockHeight = 1 + barrel.height;
            float height = ((float)fluid.amount - (float)tank.renderOffset) / (float)tank.getCapacity() * (float)blockHeight - 0.0625F;
            height = Math.max(0.0675F, Math.min(height, (float)blockHeight - 0.0625F));
            if (!((float)tank.renderOffset > 1.2F) && !((float)tank.renderOffset < -1.2F)) {
                tank.renderOffset = 0;
            } else {
                tank.renderOffset = (int)((float)tank.renderOffset - ((float)tank.renderOffset / 12.0F + 0.1F) * partialTicks);
            }

            renderer.setTranslation(x, y, z);
            TextureAtlasSprite stillSpr = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            TextureAtlasSprite flowSpr = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
            int color = fluid.getFluid().getColor(fluid);
            int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());
            RenderUtils.putTexturedQuad(renderer, stillSpr, 0.125, (double)height, 0.125, 0.75, 0, 0.75, EnumFacing.UP, color, brightness, false);
            RenderUtils.putTexturedQuad(renderer, flowSpr, 0.125, 0.125, 0.125, 0.75, (double)height - 0.125, 0.75, EnumFacing.EAST, color, brightness, true);
            RenderUtils.putTexturedQuad(renderer, flowSpr, 0.125, 0.125, 0.125, 0.75, (double)height - 0.125, 0.75, EnumFacing.WEST, color, brightness, true);
            RenderUtils.putTexturedQuad(renderer, flowSpr, 0.125, 0.125, 0.125, 0.75, (double)height - 0.125, 0.75, EnumFacing.NORTH, color, brightness, true);
            RenderUtils.putTexturedQuad(renderer, flowSpr, 0.125, 0.125, 0.125, 0.75, (double)height - 0.125, 0.75, EnumFacing.SOUTH, color, brightness, true);
        }
    }
}
