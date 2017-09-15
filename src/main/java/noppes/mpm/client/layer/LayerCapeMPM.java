package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class LayerCapeMPM extends LayerCape{
	private RenderPlayer render;
	public LayerCapeMPM(RenderPlayer render) {
		super(render);
		this.render = render;
	}

//	@Override
//	public void preRender(EntityPlayer player) {
//		if(!player.func_175148_a(EnumPlayerModelParts.CAPE))
//			return;
//		player.getDataWatcher().updateObject(10, player.getDataWatcher().getWatchableObjectByte(10) - 1);
//	}
	
    public void doRenderLayer(AbstractClientPlayer player, float p_177166_2_, float p_177166_3_, float p_177166_4_, float p_177166_5_, float p_177166_6_, float p_177166_7_, float p_177166_8_){
    	ModelBiped model = render.getMainModel();
		ModelData data = ModelData.get(player);
    	ModelPartConfig config = data.getPartConfig(EnumParts.BODY);
    	GlStateManager.pushMatrix();
    	if(player.isSneaking())
    		GlStateManager.translate(0, 0, -2 * p_177166_8_);
    	GlStateManager.translate(config.transX, config.transY, config.transZ);
    	GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
    	if(data.animationEquals(EnumAnimation.CRAWLING)){
        	int rotation = 78;
        	if(player.isSneaking()){
        		rotation -= 25;
        	}
        	GlStateManager.translate(0, 22 * p_177166_8_, 0);
    		GlStateManager.rotate(rotation, 1, 0, 0);
    	}
    	if(player.hurtTime > 0 || player.deathTime > 0){
        	GlStateManager.color(1, 0, 0, 0.3f);
    	}
    	super.doRenderLayer(player, p_177166_2_, p_177166_3_, p_177166_4_, p_177166_5_, p_177166_6_, p_177166_7_, p_177166_8_);
    	GlStateManager.popMatrix();
    }
}
