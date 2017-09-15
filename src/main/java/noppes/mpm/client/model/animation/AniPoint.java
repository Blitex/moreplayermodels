package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class AniPoint {

	public static void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, ModelBiped base){
        base.bipedRightArm.rotateAngleX = -1.570796F;
		base.bipedRightArm.rotateAngleY = par4 / (180F / (float)Math.PI);
		base.bipedRightArm.rotateAngleZ = 0;
	}
}
