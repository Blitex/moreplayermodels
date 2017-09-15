package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
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

public class ModelPlayerAlt extends ModelPlayer{
    private ModelRenderer field_178729_w;
    private ModelRenderer field_178736_x;
    private ModelData playerdata;

    private Map<EnumParts, List<ModelScaleRenderer>> map = new HashMap<EnumParts, List<ModelScaleRenderer>>();


	public ModelPlayerAlt(float scale, boolean bo) {
		super(scale, bo);
        this.field_178736_x = new ModelScaleRenderer(this, 24, 0, EnumParts.HEAD);
        this.field_178736_x.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
        this.field_178729_w = new ModelScaleRenderer(this, 0, 0, EnumParts.BODY);
        this.field_178729_w.setTextureSize(64, 32);
        this.field_178729_w.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);	

        ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, field_178736_x, 6);
        ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, field_178729_w, 5);

        this.bipedLeftArm = createScale(bipedLeftArm, EnumParts.ARM_LEFT);
        this.bipedRightArm = createScale(bipedRightArm, EnumParts.ARM_RIGHT);
        this.bipedLeftArmwear = createScale(bipedLeftArmwear, EnumParts.ARM_LEFT);
        this.bipedRightArmwear = createScale(bipedRightArmwear, EnumParts.ARM_RIGHT);

        this.bipedLeftLeg = createScale(bipedLeftLeg, EnumParts.LEG_LEFT);
        this.bipedRightLeg = createScale(bipedRightLeg, EnumParts.LEG_RIGHT);
        this.bipedLeftLegwear = createScale(bipedLeftLegwear, EnumParts.LEG_LEFT);
        this.bipedRightLegwear = createScale(bipedRightLegwear, EnumParts.LEG_RIGHT);


        this.bipedHead = createScale(bipedHead, EnumParts.HEAD);
        this.bipedHeadwear = createScale(bipedHeadwear, EnumParts.HEAD);
        this.bipedBody = createScale(bipedBody, EnumParts.BODY);
        this.bipedBodyWear = createScale(bipedBodyWear, EnumParts.BODY);
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
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity){
		EntityPlayer player = (EntityPlayer) entity;
		playerdata = ModelData.get(player);
		
		if(playerdata.isSleeping()){
			GlStateManager.translate(0, 1.14f, 0);
			GlStateManager.rotate(45, -1, 0, 0);
			GlStateManager.translate(0, -0.8f, 0);
		}
		else if(playerdata.animation == EnumAnimation.CRAWLING){
			GlStateManager.translate(0, (12f - playerdata.getBodyY() * 4) * par6, 0);
			GlStateManager.translate(0, 0f, ((isSneak?-6:-3f) - playerdata.getBodyY() * 10) * par6);
			GlStateManager.rotate(45, 1.0F, 0F, 0.0F);			
		}
		
		for(EnumParts part : map.keySet()){
			ModelPartConfig config = playerdata.getPartConfig(part);
			for(ModelScaleRenderer model : map.get(part)){
				model.config = config;
			}
		}
		
    	if(!isRiding)
    		isRiding = playerdata.animation == EnumAnimation.SITTING;
    	
    	if(isSneak && (playerdata.animation == EnumAnimation.CRAWLING || playerdata.isSleeping()))
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
		this.bipedLeftArm.rotationPointX = 0;
		this.bipedLeftArm.rotationPointY = 2;
		this.bipedLeftArm.rotationPointZ = 0;
		this.bipedRightArm.rotationPointX = 0;
		this.bipedRightArm.rotationPointY = 2;
		this.bipedRightArm.rotationPointZ = 0;
		
    	super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
    	
    	if(playerdata.isSleeping() || player.isPlayerSleeping()){
     		if(bipedHead.rotateAngleX < 0){
     			bipedHead.rotateAngleX = 0;
     			bipedHeadwear.rotateAngleX = 0;
     		}
     	}
    	else if(playerdata.animation == EnumAnimation.CRY)
    		bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = 0.7f;
    	else if(playerdata.animation == EnumAnimation.HUG)
    		AniHug.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	else if(playerdata.animation == EnumAnimation.CRAWLING)
    		AniCrawling.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	else if(playerdata.animation == EnumAnimation.WAVING){
    		AniWaving.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(playerdata.animation == EnumAnimation.DANCING){
    		AniDancing.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(playerdata.animation == EnumAnimation.BOW){
    		AniBow.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, playerdata);
    	}
    	else if(playerdata.animation == EnumAnimation.YES){
    		AniYes.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, playerdata);
    	}
    	else if(playerdata.animation == EnumAnimation.NO){
    		AniNo.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this, playerdata);
    	}
    	else if(playerdata.animation == EnumAnimation.POINT){
    		AniPoint.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, this);
    	}
    	else if(isSneak)
            this.bipedBody.rotateAngleX = 0.5F / playerdata.getPartConfig(EnumParts.BODY).scaleY;
    	    	
        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedBody, this.bipedBodyWear);
        copyModelAngles(this.bipedHead, this.bipedHeadwear);
    }
	
	@Override
    public ModelRenderer getRandomModelBox(Random random){
		switch(random.nextInt(5)){
		case 0:
			return bipedHead;
		case 1:
			return bipedBody;
		case 2:
			return bipedLeftArm;
		case 3:
			return bipedRightArm;
		case 4:
			return bipedLeftLeg;
		case 5:
			return bipedRightLeg;
		}
		return bipedHead;
    }

//	@Override
//    public void renderRightArm(){
//		GlStateManager.translate(0, -playerdata.getBodyY(), 0);
//
//		ModelPartConfig config = ((ModelScaleRenderer)bipedRightArm).config;
//		GlStateManager.translate(0, -(config.scaleY - 1) / 2, 0);
//		
//        this.bipedRightArm.render(0.0625F);
//        this.bipedRightArmwear.render(0.0625F);
//    }
//
//	@Override
//    public void renderLeftArm(){
//		GlStateManager.translate(0, -playerdata.getBodyY(), 0);
//
//		ModelPartConfig config = ((ModelScaleRenderer)bipedLeftArm).config;
//		GlStateManager.translate(0, -(config.scaleY - 1) / 2, 0);
//		
//        this.bipedLeftArm.render(0.0625F);
//        this.bipedLeftArmwear.render(0.0625F);
//    }
}
