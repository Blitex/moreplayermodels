package noppes.mpm.client.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import noppes.mpm.client.ChatMessages;

public class LayerEyes extends LayerInterface{

	public LayerEyes(RenderPlayer render) {
		super(render);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		if(!playerdata.eyes.isEnabled())
			return;
		GlStateManager.pushMatrix();
		model.bipedHead.postRender(0.0625F);
		GlStateManager.scale(par7, par7, -par7);
		GlStateManager.translate(0, (playerdata.eyes.type == 1?1:2) - playerdata.eyes.eyePos, 0);
		
        GlStateManager.disableTexture2D();
        int i = (int) (player.getBrightnessForRender(par7));
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        
        drawBrows();
        drawLeft();
        drawRight();

		GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
	}
	
	private void drawLeft(){
	    if(playerdata.eyes.pattern == 2)
		       return;
		drawRect(3, -5, 1, -4, 0xF6F6F6, 4.01, false);				
		drawRect(2, -5, 1, -4, playerdata.eyes.color, 4.011, playerdata.eyes.type == 1);
		if(playerdata.eyes.glint && player.isEntityAlive()){
			drawRect(1.5, -4.9, 1.9, -4.5, 0xFFFFFFFF, 4.012, false);
		}
		if(playerdata.eyes.type == 1){
			drawRect(3, -4, 1, -3, 0xFFFFFF, 4.01, true);
			drawRect(2, -4, 1, -3, playerdata.eyes.color, 4.011, false);
		}	    
	}
	
	private void drawRight(){
	    if(playerdata.eyes.pattern == 1)
		       return;
			drawRect(-3, -5, -1, -4, 0xF6F6F6, 4.01, false);
			drawRect(-2, -5, -1, -4, playerdata.eyes.color, 4.011, playerdata.eyes.type == 1);
		    if(playerdata.eyes.glint && player.isEntityAlive()){
				drawRect(-1.5, -4.9, -1.1, -4.5, 0xFFFFFFFF, 4.012, false);
			}
			if(playerdata.eyes.type == 1){
				drawRect(-3, -4, -1, -3, 0xFFFFFF, 4.01, true);
				drawRect(-2, -4, -1, -3, playerdata.eyes.color, 4.011, false);
			}
	}
	
	private void drawBrows(){
		float offsetY = 0;
		if(playerdata.eyes.blinkStart > 0 && player.isEntityAlive()){
			float f = (System.currentTimeMillis() - playerdata.eyes.blinkStart) / 150f;
			if(f > 1)
				f = 2 - f;
			if(f < 0){
				playerdata.eyes.blinkStart = 0;
				f = 0;
			}
			offsetY = (playerdata.eyes.type == 1?2:1) * f;
			drawRect(-3, -5, -1, -5 + offsetY, playerdata.eyes.skinColor, 4.013, false);
			drawRect(3, -5, 1, -5 + offsetY, playerdata.eyes.skinColor, 4.013, false);
		}
		if(playerdata.eyes.browThickness > 0){
		    float thickness = playerdata.eyes.browThickness / 10f;
            drawRect(-3, -5 + offsetY, -1, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014, false);
            drawRect(1, -5 + offsetY, 3, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014, false);
       }
	}
	
	public void drawRect(double x, double y, double x2, double y2, int color, double z, boolean darken)
    {
		double j1;

        if (x < x2)
        {
            j1 = x;
            x = x2;
            x2 = j1;
        }

        if (y < y2)
        {
            j1 = y;
            y = y2;
            y2 = j1;
        }

        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        if(darken){
        	f1 *= 0.96f;
        	f2 *= 0.96f;
        	f3 *= 0.96f;
        }
        VertexBuffer tessellator = Tessellator.getInstance().getBuffer();
        GlStateManager.color(1, 1, 1, 1);
        tessellator.begin(7, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos((double)x, (double)y, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos((double)x, (double)y2, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos((double)x2, (double)y2, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos((double)x2, (double)y, z).color(f1, f2, f3, 1).endVertex();
        Tessellator.getInstance().draw();
    }

	@Override
	public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
		
	}

}
