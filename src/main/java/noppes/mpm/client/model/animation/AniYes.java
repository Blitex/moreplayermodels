package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.mpm.ModelData;

public class AniYes {
	public static void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, ModelBiped model, ModelData data){
		float ticks = (entity.ticksExisted - data.animationStart) / 10f;
		
		ticks = ticks % 2;
		float ani = ticks - 0.5f;
		if(ticks > 1)
			ani = 1.5f - ticks;
		model.bipedHead.rotateAngleX = ani;
	}
}
