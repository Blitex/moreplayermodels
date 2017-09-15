package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.model.part.head.ModelHeadwear;

public class LayerHeadwear extends LayerInterface implements LayerPreRender{
	private ModelHeadwear headwear;
	
	public LayerHeadwear(RenderPlayer render) {
		super(render);
	}

	@Override
	protected void createParts(){
		headwear = new ModelHeadwear(model);
	}
	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {	
		if(MorePlayerModels.HeadWearType != 1)
			return;
		GlStateManager.color(1, 1, 1);
		ClientProxy.bindTexture(player.getLocationSkin());
    	if(player.hurtTime > 0 || player.deathTime > 0){
        	GlStateManager.color(1, 0, 0, 0.3f);
    	}
		model.bipedHead.postRender(par7);
		headwear.render(par7);
	}

	@Override
	public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
		
	}

	@Override
	public void preRender(AbstractClientPlayer player) {
		model.bipedHeadwear.isHidden = MorePlayerModels.HeadWearType == 1;
		headwear.config =  null;
	}

}
