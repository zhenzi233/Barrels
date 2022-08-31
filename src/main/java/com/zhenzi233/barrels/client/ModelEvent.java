package com.zhenzi233.barrels.client;

import com.zhenzi233.barrels.library.Util;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEvent {
    private static final String LOCATION_Barrel = Util.resource("barrel_module");

    public static final ModelResourceLocation normal = getBarrelleLoc(false);
    public static final ModelResourceLocation extension = getBarrelleLoc(true);
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {

        // replace the baked table models with smart variants

        // tool tables
        replaceBarrelModel(normal, event);
        replaceBarrelExtensionModel(extension, event);

        // silence the missing-model message for the default itemblock
//        event.getModelRegistry().putObject(new ModelResourceLocation(LOCATION_Barrel, "inventory"), event.getModelRegistry().getObject(locToolStation));
    }

    private static ModelResourceLocation getBarrelleLoc(boolean extension) {
        return new ModelResourceLocation(LOCATION_Barrel, String.format("%s=%s",
                "extension",
                extension));
    }

    public static void replaceBarrelModel(ModelResourceLocation location, ModelBakeEvent event) {
        try {
            IModel model = ModelLoaderRegistry.getModel(location);
            IBakedModel standard = event.getModelRegistry().getObject(location);
            IBakedModel finalModel = new BakedBarrelModel(standard, model, DefaultVertexFormats.BLOCK);
            event.getModelRegistry().putObject(location, finalModel);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void replaceBarrelExtensionModel(ModelResourceLocation location, ModelBakeEvent event) {
        try {
            IModel model = ModelLoaderRegistry.getModel(location);
            IBakedModel standard = event.getModelRegistry().getObject(location);
            IBakedModel finalModel = new BakedBarrelExtensionModel(standard, model, DefaultVertexFormats.BLOCK);
            event.getModelRegistry().putObject(location, finalModel);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
