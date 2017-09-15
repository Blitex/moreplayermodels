package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.model.animation.AniBow;
import noppes.mpm.client.model.animation.AniCrawling;
import noppes.mpm.client.model.animation.AniDancing;
import noppes.mpm.client.model.animation.AniHug;
import noppes.mpm.client.model.animation.AniNo;
import noppes.mpm.client.model.animation.AniPoint;
import noppes.mpm.client.model.animation.AniWaving;
import noppes.mpm.client.model.animation.AniYes;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class ModelBipedAlt extends ModelBiped{

    private Map<EnumParts, List<ModelScaleRenderer>> map = new HashMap<EnumParts, List<ModelScaleRenderer>>();


	public ModelBipedAlt(float scale) {
		super(scale);	

        this.bipedLeftArm = createScale(bipedLeftArm, EnumParts.ARM_LEFT);
        this.bipedRightArm = createScale(bipedRightArm, EnumParts.ARM_RIGHT);

        this.bipedLeftLeg = createScale(bipedLeftLeg, EnumParts.LEG_LEFT);
        this.bipedRightLeg = createScale(bipedRightLeg, EnumParts.LEG_RIGHT);


        this.bipedHead = createScale(bipedHead, EnumParts.HEAD);
        this.bipedHeadwear = createScale(bipedHeadwear, EnumParts.HEAD);
        this.bipedBody = createScale(bipedBody, EnumParts.BODY);
	}
	
	private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part){
		int textureX = ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2);
		int textureY = ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3);
		ModelScaleRenderer model = new ModelScaleRenderer(this, textureX, textureY, part);
		model.textureHeight = renderer.textureHeight;
		model.textureWidth = renderer.textureWidth;
		model.childModels = renderer.childModels;
		model.cubeList = renderer.cubeList;
		copyModelAngles(renderer, model);

		List<ModelScaleRenderer> list = map.get(part);
		if(list == null)
			map.put(part, list = new ArrayList<ModelScaleRenderer>());
		list.add(model);
		return model;
	}
	
	@Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity)
    {
		//super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entity);
		EntityPlayer player = (EntityPlayer) entity;
		ModelData data = ModelData.get(player);
		
		for(EnumParts part : map.keySet()){
			ModelPartConfig config = data.getPartConfig(part);
			for(ModelScaleRenderer model : map.get(part)){
				model.config = config;
			}
		}
		
    	if(!isRiding)
    		isRiding = data.animation == EnumAnimation.SITTING;
    	
    	if(isSneak && (data.animation == EnumAnimation.CRAWLING || data.isSleeping()))
    		isSneak = false;
    	
    	this.bipedBody.rotationPointX = this.bipedBody.rotationPointY = this.bipedBody.rotationPointZ = 0;
    	this.bipedBody.rotateAngleX = this.bipedBody.rotateAngleY = this.bipedBody.rotateAngleZ = 0;

    	this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX = 0;
    	this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ = 0;

    	this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX = 0;
    	this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY = 0;
    	this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ = 0;
		
		this.bipedLeftLeg.rotateAngleX = 0;
		this.bipedLeftLeg.rotateAngleY = 0;
		this.bipedLeftLeg.rotateAngleZ = 0;
		this.bipedRightLeg.rotateAngleX = 0;
		this.bipedRightLeg.rotateAngleY = 0;
		this.bipedRightLeg.rotateAngleZ = 0;
		this.bipedLeftArm.rotationPointX= 0;
		this.bipedLeftArm.rotationPointY = 2;
		this.bipedLeftArm.rotationPointZ = 0;
		this.bipedRightArm.rotationPointX= 0;
		this.bipedRightArm.rotationPointY = 2;
		this.bipedRightArm.rotationPointZ = 0;
		
    	super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
    	
    	if(data.isSleeping() || player.isPlayerSleeping()){
     		if(bipedHead.rotateAngleX < 0){
     			bipedHead.rotateAngleX = 0;
     			bipedHeadwear.rotateAngleX = 0;
     		}
     	}
    	else if(data.animation == EnumAnimation.CRY)
    		bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = 0.7f;
    	else if(data.animation == EnumAnimation.HUG)
    		AniHug.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	else if(data.animation == EnumAnimation.CRAWLING)
    		AniCrawling.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	else if(data.animation == EnumAnimation.WAVING){
    		AniWaving.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(data.animation == EnumAnimation.DANCING){
    		AniDancing.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(data.animation == EnumAnimation.BOW){
    		AniBow.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, data);
    	}
    	else if(data.animation == EnumAnimation.YES){
    		AniYes.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, data);
    	}
    	else if(data.animation == EnumAnimation.NO){
    		AniNo.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, data);
    	}
    	else if(data.animation == EnumAnimation.POINT){
    		AniPoint.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(isSneak)
            this.bipedBody.rotateAngleX = 0.5F / data.getPartConfig(EnumParts.BODY).scaleY;
    	
    }
}
