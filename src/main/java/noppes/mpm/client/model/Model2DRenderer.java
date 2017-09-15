package noppes.mpm.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Model2DRenderer extends ModelRenderer {

    private boolean compiled;

    private int displayList;
    
	private float x1, x2, y1, y2;
	private int width, height;
	private float rotationOffsetX, rotationOffsetY, rotationOffsetZ;
	
	private float scaleX = 1, scaleY = 1, thickness = 1;

	public Model2DRenderer(ModelBase par1ModelBase, float x, float y, int width, int height, int textureWidth, int textureHeight) {
		super(par1ModelBase);
		this.width = width;
		this.height = height;
		
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		
		x1 = x / textureWidth;
		y1 = y / textureHeight;

		x2 = (x + width) / textureWidth;
		y2 = (y + height) / textureHeight;
	}
	public Model2DRenderer(ModelBase par1ModelBase, float x, float y, int width, int height) {
		this(par1ModelBase, x, y, width, height, par1ModelBase.textureWidth, par1ModelBase.textureHeight);
	}
    
	public void render(float par1) {
		if(!showModel || isHidden)
			return;
		if(!compiled)
			compileDisplayList(par1);
		
		GlStateManager.pushMatrix();
		this.postRender(par1);
		GlStateManager.callList(this.displayList);
		GlStateManager.popMatrix();
	}
	
	public void setRotationOffset(float x, float y, float z){
		rotationOffsetX = x;
		rotationOffsetY = y;
		rotationOffsetZ = z;
	}

	public void setScale(float scale){
		this.scaleX = scale;
		this.scaleY = scale;
	}
	public void setScale(float x, float y){
		this.scaleX = x;
		this.scaleY = y;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
	}
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float par1)
    {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        GlStateManager.translate(rotationOffsetX * par1, rotationOffsetY * par1, rotationOffsetZ * par1);
		GlStateManager.scale(scaleX * width / height, scaleY, thickness);
    	GlStateManager.rotate(180, 1F, 0F, 0F);
        if(mirror){
    		GlStateManager.translate(0, 0, -1f * par1);
            GlStateManager.rotate(180, 0, 1F, 0F);
        }

		renderItemIn2D(Tessellator.getInstance().getBuffer(), x1, y1, x2, y2, width, height, par1);
        GL11.glEndList();
        this.compiled = true;
    }
    
    public static void renderItemIn2D(VertexBuffer worldrenderer, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_){
    	Tessellator tessellator = Tessellator.getInstance();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(p_78439_1_, p_78439_4_).normal(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(1.0D, 0.0D, 0.0D).tex(p_78439_3_, p_78439_4_).normal(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(1.0D, 1.0D, 0.0D).tex(p_78439_3_, p_78439_2_).normal(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 1.0D, 0.0D).tex(p_78439_1_, p_78439_2_).normal(0.0F, 0.0F, 1.0F).endVertex();
        tessellator.draw();
        
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(0.0D, 1.0D, 0.0F - p_78439_7_).tex(p_78439_1_, p_78439_2_).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(1.0D, 1.0D, 0.0F - p_78439_7_).tex(p_78439_3_, p_78439_2_).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(1.0D, 0.0D, 0.0F - p_78439_7_).tex(p_78439_3_, p_78439_4_).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0F - p_78439_7_).tex(p_78439_1_, p_78439_4_).normal(0.0F, 0.0F, -1.0F).endVertex();
        tessellator.draw();
        float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float)p_78439_5_;
        float f6 = 0.5F * (p_78439_4_ - p_78439_2_) / (float)p_78439_6_;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        int k;
        float f7;
        float f8;

        for (k = 0; k < p_78439_5_; ++k)
        {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            worldrenderer.pos(f7, 0.0D, 0.0F - p_78439_7_).tex(f8, p_78439_4_).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f7, 0.0D, 0.0D).tex(f8, p_78439_4_).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f7, 1.0D, 0.0D).tex(f8, p_78439_2_).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f7, 1.0D, 0.0F - p_78439_7_).tex(f8, p_78439_2_).normal(-1.0F, 0.0F, 0.0F).endVertex();
        }

        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float f9;

        for (k = 0; k < p_78439_5_; ++k)
        {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            f9 = f7 + 1.0F / (float)p_78439_5_;
            worldrenderer.pos(f9, 1.0D, 0.0F - p_78439_7_).tex(f8, p_78439_2_).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f9, 1.0D, 0.0D).tex(f8, p_78439_2_).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f9, 0.0D, 0.0D).tex(f8, p_78439_4_).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldrenderer.pos(f9, 0.0D, 0.0F - p_78439_7_).tex(f8, p_78439_4_).normal(1.0F, 0.0F, 0.0F).endVertex();
        }

        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < p_78439_6_; ++k)
        {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            f9 = f7 + 1.0F / (float)p_78439_6_;
            worldrenderer.pos(0.0D, f9, 0.0D).tex(p_78439_1_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(1.0D, f9, 0.0D).tex(p_78439_3_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(1.0D, f9, 0.0F - p_78439_7_).tex(p_78439_3_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.0D, f9, 0.0F - p_78439_7_).tex(p_78439_1_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
        }

        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < p_78439_6_; ++k)
        {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            worldrenderer.pos(1.0D, f7, 0.0D).tex(p_78439_3_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.0D, f7, 0.0D).tex(p_78439_1_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.0D, f7, 0.0F - p_78439_7_).tex(p_78439_1_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos(1.0D, f7, 0.0F - p_78439_7_).tex(p_78439_3_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
        }

        tessellator.draw();
    }
}
