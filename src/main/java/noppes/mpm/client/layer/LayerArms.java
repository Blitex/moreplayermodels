package noppes.mpm.client.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.constants.EnumParts;

public class LayerArms extends LayerInterface{
	private Model2DRenderer lClaw;
	private Model2DRenderer rClaw;
	
	public LayerArms(RenderPlayer render) {
		super(render);
	}

	@Override
	protected void createParts(){
		lClaw = new Model2DRenderer(model, 0, 16, 4, 4);
		lClaw.setRotationPoint(3F, 14f, -2);
		lClaw.rotateAngleY = (float) (Math.PI / -2);
		lClaw.setScale(0.25f);

		rClaw = new Model2DRenderer(model, 0, 16, 4, 4);
		rClaw.setRotationPoint(-2F, 14f, -2);
		rClaw.rotateAngleY = (float) (Math.PI / -2);
		rClaw.setScale(0.25f);
	}
	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		ModelPartData data = playerdata.getPartData(EnumParts.CLAWS);
		if(data == null)
			return;
		preRender(data);
		if(data.pattern == 0 || data.pattern == 1){
			GlStateManager.pushMatrix();
			model.bipedLeftArm.postRender(0.0625F);
			lClaw.render(par7);
			GlStateManager.popMatrix();
		}
		if(data.pattern == 0 || data.pattern == 2){
			GlStateManager.pushMatrix();
			model.bipedRightArm.postRender(0.0625F);
			rClaw.render(par7);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
		
	}

}
