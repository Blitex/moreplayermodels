package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import noppes.mpm.ModelData;
import noppes.mpm.constants.EnumParts;

public class LayerElytraAlt extends LayerElytra {

	public LayerElytraAlt(RenderPlayer renderPlayerIn) {
		super(renderPlayerIn);
	}

	@Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale){
    	ModelData data = ModelData.get(entitylivingbaseIn);
    	if(data.getPartData(EnumParts.WINGS) != null && data.wingMode == 1){
    		return;
    	}
		super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
