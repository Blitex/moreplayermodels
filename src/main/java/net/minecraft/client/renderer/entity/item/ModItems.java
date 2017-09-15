package net.minecraft.client.renderer.entity.item;

import assets.moreplayermodels.models.item.ItemTutorialItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noppes.mpm.MorePlayerModels;

/**
 * Created by Alex Nava on 7/10/2017.
 */
public class ModItems {

    public static Item CerealModel;

    public static void preInit() {

        CerealModel = new ItemTutorialItem();
        CerealModel.setUnlocalizedName("CerealModel");
        Item item = CerealModel.setCreativeTab(MorePlayerModels.modelsTab);

        registerItems();

    }

    public static void registerItems() {
        Item cerealModel = GameRegistry.register(CerealModel, new ResourceLocation(MorePlayerModels.MODID, "CerealModel"));
    }

    public static void registerRenders(Item cerealModel) {
       registerRenders(ModItems.CerealModel);
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft(). getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(MorePlayerModels.MODID + ";" + item.getUnlocalizedName().substring(5), "inventory"));
    }

    public static void registerRenders() {
    }
}
