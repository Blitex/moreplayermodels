package noppes.mpm.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiModelColor extends GuiNPCInterface implements ITextfieldListener{

	private GuiScreen parent;
	private final static ResourceLocation colorPicker = new ResourceLocation("moreplayermodels:textures/gui/color.png");
	private final static ResourceLocation colorgui = new ResourceLocation("moreplayermodels:textures/gui/color_gui.png");
	
	private int colorX, colorY;
	
	private GuiNpcTextField textfield;
	
	public int color;

	private ColorCallback callback;
	public GuiModelColor(GuiScreen parent, int color, ColorCallback callback){
		this.parent = parent;
		this.callback = callback;
		ySize = 230;
		closeOnEsc = false;
		background = colorgui;
		this.color = color;
	}

    @Override
    public void initGui() {
    	super.initGui();
    	colorX = guiLeft + 4;
    	colorY = guiTop + 50;
		this.addTextField(textfield = new GuiNpcTextField(0, this, guiLeft + 35, guiTop + 25, 60, 20, getColor()));
		addButton(new GuiNpcButton(66, guiLeft + 107, guiTop + 8, 20, 20, "X"));
		textfield.setTextColor(color);
    }

    @Override
	protected void actionPerformed(GuiButton guibutton) {
    	if(guibutton.id == 66){
    		close();
    	}
    }
    
    @Override
    public void keyTyped(char c, int i){
    	String prev = textfield.getText();
    	super.keyTyped(c, i);
    	String newText = textfield.getText();
    	if(newText.equals(prev))
    		return;
		try{
			color = Integer.parseInt(textfield.getText(),16);
			callback.color(color);
			textfield.setTextColor(color);
		}
		catch(NumberFormatException e){
			textfield.setText(prev);
		}
    }

    @Override
    public void drawScreen(int par1, int par2, float par3){
    	super.drawScreen(par1, par2, par3);

    	GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(colorPicker);
        this.drawTexturedModalRect(colorX, colorY, 0, 0, 120, 120);
    }
    
	@Override
    public void mouseClicked(int i, int j, int k) throws IOException{
		super.mouseClicked(i, j, k);
		if( i < colorX  || i > colorX + 120 || j < colorY || j > colorY + 120)
			return;
		InputStream stream = null;
		try {
			IResource resource = this.mc.getResourceManager().getResource(colorPicker);
            BufferedImage bufferedimage = ImageIO.read(stream = resource.getInputStream());
            int color = bufferedimage.getRGB((i - guiLeft - 4) * 4, (j - guiTop - 50) * 4)  & 16777215;
            if(color != 0){
            	this.color = color;
            	callback.color(color);
            	textfield.setTextColor(color);
            	textfield.setText(getColor());
            }
			
		} catch (IOException e) {
		} 
		finally{
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					
				}
			}
		}
    }

	@Override
	public void unFocused(GuiNpcTextField textfield) {
		try{
			color = Integer.parseInt(textfield.getText(),16);
		}
		catch(NumberFormatException e){
			color = 0;
		}
		callback.color(color);
		textfield.setTextColor(color);
	}
	
	public String getColor() {
		String str = Integer.toHexString(color);

    	while(str.length() < 6)
    		str = "0" + str;
    	
    	return str;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
	public static interface ColorCallback{
	    public void color(int color);
	}
}
