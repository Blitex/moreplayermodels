package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.ClientProxy;

public abstract class LayerInterface implements LayerRenderer{
	protected RenderPlayer render;
	protected AbstractClientPlayer player;
	protected ModelData playerdata;
	protected ModelBiped model;
	
	public LayerInterface(RenderPlayer render){
		this.render = render;
		setModel((ModelBiped) render.getMainModel());
	}
	
	public void setModel(ModelBiped model){
		this.model = model;
		createParts();
	}
	
	public void setColor(ModelPartData data, EntityLivingBase entity){
	}
	
	protected void createParts(){
		
	}
	
	public void preRender(ModelPartData data){
		if(data.playerTexture)
			ClientProxy.bindTexture(player.getLocationSkin());
		else
			ClientProxy.bindTexture(data.getResource());
    	if(player.hurtTime > 0 || player.deathTime > 0){
        	GlStateManager.color(1, 0, 0, 0.3f);
    		return;
    	}
		int color = data.color;
    	float red = (color >> 16 & 255) / 255f;
    	float green = (color >> 8  & 255) / 255f;
    	float blue = (color & 255) / 255f;
    	GlStateManager.color(red, green, blue, 0.99f);
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float par2, float par3, float par8, float par4, float par5, float par6, float par7) {
		if(entity.isInvisible())
			return;
		
		player = (AbstractClientPlayer) entity;
		playerdata = ModelData.get(player);

		ModelBiped model = render.getMainModel();
		rotate(par2, par3, par4, par5, par6, par7);

		GlStateManager.pushMatrix();
        if (player.isSneaking()){
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GlStateManager.enableRescaleNormal();
		render(par2, par3, par4, par5, par6, par7);
        GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	public abstract void render(float par2, float par3, float par4, float par5, float par6, float par7);
	public abstract void rotate(float par1, float par2, float par3, float par4, float par5, float par6);
	
}
