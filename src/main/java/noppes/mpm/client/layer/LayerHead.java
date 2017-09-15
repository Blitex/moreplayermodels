package noppes.mpm.client.layer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.client.model.part.head.ModelDuckBeak;
import noppes.mpm.client.model.part.horns.ModelAntennasBack;
import noppes.mpm.client.model.part.horns.ModelAntennasFront;
import noppes.mpm.client.model.part.horns.ModelAntlerHorns;
import noppes.mpm.client.model.part.horns.ModelBullHorns;
import noppes.mpm.constants.EnumParts;

public class LayerHead extends LayerInterface{

	private ModelRenderer small;
	private ModelRenderer medium;
	private ModelRenderer large;
	private ModelRenderer bunnySnout;
	private ModelRenderer beak;

	private Model2DRenderer beard;
	private Model2DRenderer hair;
	private Model2DRenderer mohawk;

	private ModelRenderer bull;
	private ModelRenderer antlers;
	private ModelRenderer antennasBack;
	private ModelRenderer antennasFront;

	private ModelRenderer ears;
	private ModelRenderer bunnyEars;
	
	public LayerHead(RenderPlayer render) {
		super(render);
	}


	@Override
	protected void createParts(){
		small = new ModelRenderer(model, 24, 0);
		small.addBox(0F, 0F, 0F, 4, 3, 1);
		small.setRotationPoint(-2F, -3F, -5F);

		medium = new ModelRenderer(model, 24, 0);
		medium.addBox(0F, 0F, 0F, 4, 3, 2);
		medium.setRotationPoint(-2F, -3F, -6F);

		large = new ModelRenderer(model, 24, 0);
		large.addBox(0F, 0F, 0F, 4, 3, 3);
		large.setRotationPoint(-2F, -3F, -7F);
		
		bunnySnout = new ModelRenderer(model, 24, 0);
		bunnySnout.addBox(1F, 1F, 0F, 4, 2, 1);
		bunnySnout.setRotationPoint(-3F, -4F, -5F);
		
		ModelRenderer tooth = new ModelRenderer(model, 24, 3);
		tooth.addBox(2F, 3f, 0F, 2, 1, 1);
		tooth.setRotationPoint(0F, 0F, 0F);
		bunnySnout.addChild(tooth);

		beak = new ModelDuckBeak(model);
		beak.setRotationPoint(0, 0, -4F);

		beard = new Model2DRenderer(model, 56, 20, 8, 12);
		beard.setRotationOffset(-3.99f, 11.8f, -4);
		beard.setScale(0.74f);

		hair = new Model2DRenderer(model, 56, 20, 8, 12);
		hair.setRotationOffset(-3.99f, 11.8f, 3);
		hair.setScale(0.75f);

		mohawk = new Model2DRenderer(model, 0, 0, 64 , 64);
		mohawk.setTextureSize(64, 64);
		mohawk.setRotationOffset(-9F, 0.1f, -0.5F);
        setRotation(mohawk, 0, (float)(Math.PI/2f), 0);
        mohawk.setScale(0.825f);

		bull = new ModelBullHorns(model);
		antlers = new ModelAntlerHorns(model);
		antennasBack = new ModelAntennasBack(model);
		antennasFront = new ModelAntennasFront(model);

		ears = new ModelRenderer(model);
		Model2DRenderer right = new Model2DRenderer(model, 56, 0, 8, 4);
		right.setRotationPoint(-7.44f, -7.3f, -0.0f);
		right.setScale(0.234f, 0.234f);
		right.setThickness(1.16f);
		ears.addChild(right);

		Model2DRenderer left = new Model2DRenderer(model, 56, 0, 8, 4);
		left.setRotationPoint(7.44f, -7.3f, 1.15f);
		left.setScale(0.234f, 0.234f);
        setRotation(left, 0, (float)(Math.PI), 0);
        left.setThickness(1.16f);
        ears.addChild(left);

		Model2DRenderer right2 = new Model2DRenderer(model, 56, 4, 8, 4);
		right2.setRotationPoint(-7.44f, -7.3f, 1.14f);
		right2.setScale(0.234f, 0.234f);
		right2.setThickness(1.16f);
		ears.addChild(right2);

		Model2DRenderer left2 = new Model2DRenderer(model, 56, 4, 8, 4);
		left2.setRotationPoint(7.44f, -7.3f, 2.31f);
		left2.setScale(0.234f, 0.234f);
        setRotation(left2, 0, (float)(Math.PI), 0);
        left2.setThickness(1.16f);
        ears.addChild(left2);

		
		bunnyEars = new ModelRenderer(model);		
		ModelRenderer earleft = new ModelRenderer(model, 56, 0);
		earleft.mirror = true;
		earleft.addBox(-1.466667F, -4F, 0F, 3, 7, 1);
		earleft.setRotationPoint(2.533333F, -11F, 0F);
		bunnyEars.addChild(earleft);

		ModelRenderer earright = new ModelRenderer(model, 56, 0);
		earright.addBox(-1.5F, -4F, 0F, 3, 7, 1);
		earright.setRotationPoint(-2.466667F, -11F, 0F);
		bunnyEars.addChild(earright);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		model.bipedHead.postRender(0.0625F);
		renderSnout(par7);
		renderBeard(par7);
		renderHair(par7);
		renderMohawk(par7);
		renderHorns(par7);
		renderEars(par7);
	}
	
	private void renderSnout(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.SNOUT);
		if(data == null)
			return;
		preRender(data);
		if(data.type == 0){
			small.render(par7);
		}
		else if(data.type == 1){
			medium.render(par7);
		}
		else if(data.type == 2){
			large.render(par7);
		}
		else if(data.type == 3){
			bunnySnout.render(par7);
		}
		else if(data.type == 4){
			beak.render(par7);
		}
	}

	private void renderBeard(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.BEARD);
		if(data == null)
			return;
		preRender(data);
		beard.render(par7);
	}

	private void renderHair(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.HAIR);
		if(data == null)
			return;
		preRender(data);
		hair.render(par7);
	}
	
	private void renderMohawk(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.MOHAWK);
		if(data == null)
			return;
		preRender(data);
		mohawk.render(par7);
	}
	private void renderHorns(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.HORNS);
		if(data == null)
			return;
		preRender(data);
		if(data.type == 0){
			bull.render(par7);
		}
		else if(data.type == 1){
			antlers.render(par7);
		}
		else if(data.type == 2 && data.pattern == 0){
			antennasBack.render(par7);
		}
		else if(data.type == 2 && data.pattern == 1){
			antennasFront.render(par7);
		}
	}
	
	private void renderEars(float par7){
		ModelPartData data = playerdata.getPartData(EnumParts.EARS);
		if(data == null)
			return;
		preRender(data);
		if(data.type == 0){
			ears.render(par7);
		}
		else if(data.type == 1){
			bunnyEars.render(par7);
		}
	}

	public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
		ModelRenderer head = model.bipedHead;
		if(head.rotateAngleX < 0){
			beard.rotateAngleX = 0;
			hair.rotateAngleX = -head.rotateAngleX * 1.2f;
			if(head.rotateAngleX > -1){
				hair.rotationPointY = -head.rotateAngleX * 1.5f;
				hair.rotationPointZ = -head.rotateAngleX * 1.5f;
			}
		}
		else{
			hair.rotateAngleX = 0;
			hair.rotationPointY = 0;
			hair.rotationPointZ = 0;
			beard.rotateAngleX = -head.rotateAngleX;
		}
		
	}
}
