package micdoodle8.mods.galacticraft.api.client.tabs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.gui.GuiMPM;

public class InventoryTabMPM extends AbstractTab {
	private static final ModelPlayer biped = new ModelPlayer(0, true);
	
	public InventoryTabMPM() {
		super(0, 0, 0, new ItemStack(Items.SKULL, 1, 3));
		displayString = I18n.translateToLocal("menu.mpm");
	}

	@Override
	public void onTabClicked() {
		Minecraft.getMinecraft().addScheduledTask(new Runnable(){
			@Override
			public void run() {
				Minecraft mc = Minecraft.getMinecraft();
				mc.displayGuiScreen(new GuiMPM());
			}
		});
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}
	
	@Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY){
        if (!visible){
            super.drawButton(minecraft, mouseX, mouseY);
            return;
        }
        this.renderStack = null;
        
        if(enabled){
	        Minecraft mc = Minecraft.getMinecraft();
	        boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	        if(hovered){
	        	int x = mouseX + mc.fontRendererObj.getStringWidth(displayString);
	        	GlStateManager.translate(x, yPosition + 2, 0);
	        	drawHoveringText(Arrays.asList(new String[] {displayString}), 0, 0, mc.fontRendererObj);
	        	GlStateManager.translate(-x, -(yPosition + 2), 0);
	        }
        }
        super.drawButton(minecraft, mouseX, mouseY);
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPosition + 14, (float)yPosition + 22, 150.0F);
        GlStateManager.scale(20, 20, 20);
		ClientProxy.bindTexture(minecraft.thePlayer.getLocationSkin());
        GlStateManager.enableColorMaterial();
        GlStateManager.rotate(135.0F, -1.0F, 1.0F, -1.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, -1.0F, 1.0F, -1.0F);
		biped.bipedHeadwear.rotateAngleX = biped.bipedHead.rotateAngleX = 0.7f;
		biped.bipedHeadwear.rotateAngleY = biped.bipedHead.rotateAngleY = (float) (-Math.PI/4);
		biped.bipedHeadwear.rotateAngleZ = biped.bipedHead.rotateAngleZ = -0.5f;
        biped.bipedHead.render(0.064f);
        biped.bipedHeadwear.render(0.0625F);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    protected void drawHoveringText(List list, int x, int y, FontRenderer font){
        if (list.isEmpty())
        	return;
        
    	GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int k = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()){
            String s = (String)iterator.next();
            int l = font.getStringWidth(s);

            if (l > k){
                k = l;
            }
        }

        int j2 = x + 12;
        int k2 = y - 12;
        int i1 = 8;

        if (list.size() > 1){
            i1 += 2 + (list.size() - 1) * 10;
        }

        if (j2 + k > this.width){
            j2 -= 28 + k;
        }

        if (k2 + i1 + 6 > this.height){
            k2 = this.height - i1 - 6;
        }

        this.zLevel = 300.0F;
        itemRender.zLevel = 300.0F;
        int j1 = -267386864;
        this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
        this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
        this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
        this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
        int k1 = 1347420415;
        int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
        this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
        this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
        this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

        for (int i2 = 0; i2 < list.size(); ++i2){
            String s1 = (String)list.get(i2);
            font.drawStringWithShadow(s1, j2, k2, -1);

            if (i2 == 0){
                k2 += 2;
            }

            k2 += 10;
        }

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }
}