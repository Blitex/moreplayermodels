package noppes.mpm.client.gui.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class GuiNPCInterface extends GuiScreen
{
	public EntityPlayerSP player;
	public boolean drawDefaultBackground = true;
	private HashMap<Integer,GuiNpcButton> buttons = new HashMap<Integer,GuiNpcButton>();
	private HashMap<Integer,GuiNpcTextField> textfields = new HashMap<Integer,GuiNpcTextField>();
	private HashMap<Integer,GuiNpcLabel> labels = new HashMap<Integer,GuiNpcLabel>();
	private HashMap<Integer,GuiCustomScroll> scrolls = new HashMap<Integer,GuiCustomScroll>();
	private HashMap<Integer,GuiNpcSlider> sliders = new HashMap<Integer,GuiNpcSlider>();
	private HashMap<Integer,GuiScreen> extra = new HashMap<Integer,GuiScreen>();
	protected ResourceLocation background = null;
	public boolean closeOnEsc = false;
	public int guiLeft,guiTop,xSize,ySize;
	private GuiNPCInterface subgui;
	public GuiNPCInterface parent;
	public int mouseX, mouseY;
	
    public GuiNPCInterface(){
    	this.player = Minecraft.getMinecraft().thePlayer;
    	xSize = 200;
    	ySize = 222;
    }
    public void setBackground(String texture){
    	background = new ResourceLocation("customnpcs","textures/gui/" + texture);
    }
    public ResourceLocation getResource(String texture){
    	return new ResourceLocation("customnpcs","textures/gui/" + texture);
    }
    @Override
    public void initGui(){
    	super.initGui();
    	GuiNpcTextField.unfocus();
    	if(subgui != null){
    		subgui.setWorldAndResolution(mc, width, height);
    		subgui.initGui();
    	}
    	guiLeft = (width- xSize)/2;
        guiTop = (height - ySize) / 2;
        buttonList.clear();
        labels.clear();
        textfields.clear();
        buttons.clear();
        scrolls.clear();
        sliders.clear();
        Keyboard.enableRepeatEvents(true);
    }
    @Override
    public void updateScreen(){
    	if(subgui != null)
    		subgui.updateScreen();
    	else{
	    	for(GuiNpcTextField tf : textfields.values()){
	    		if(tf.enabled)
	    			tf.updateCursorCounter();
	    	}
	        super.updateScreen();
    	}
    }
	
    @Override
    public void mouseClicked(int i, int j, int k) throws IOException
    {
    	if(subgui != null)
    		subgui.mouseClicked(i,j,k);
    	else{
	    	for(GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(textfields.values()))
	    		if(tf.enabled)
	    			tf.mouseClicked(i, j, k);
	        if (k == 0){
		        for(GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(scrolls.values())){
		        	scroll.mouseClicked(i, j, k);
		        }
	        }
	    	mouseEvent(i,j,k);
	    	super.mouseClicked(i, j, k);
    	}
    }

    public void mouseEvent(int i, int j, int k){};

    @Override
	protected void actionPerformed(GuiButton guibutton) {
		if(subgui != null)
			subgui.buttonEvent(guibutton);
		else{
			buttonEvent(guibutton);
		}
	}
    public void buttonEvent(GuiButton guibutton){};

    @Override
	public void keyTyped(char c, int i){
    	if(subgui != null)
    		subgui.keyTyped(c,i);
    	else{
	    	for(GuiNpcTextField tf : textfields.values())
	    			tf.textboxKeyTyped(c, i);
	        if (closeOnEsc && (i == 1 || !GuiNpcTextField.isActive() && isInventoryKey(i))){
	        	close();
	        }
    	}
    }
    
    public void onGuiClosed(){
    	GuiNpcTextField.unfocus();
    }
    
    public final void close(){
    	if(parent != null){
    		parent.closeSubGui(this);
    	}
    	else{
            displayGuiScreen(null);
            mc.setIngameFocus();
    	}
        save();
    }
    public void addButton(GuiNpcButton button){
    	buttons.put(button.id,button);
    	buttonList.add(button);
    }
	public GuiNpcButton getButton(int i) {
		return buttons.get(i);
	}
    public void addTextField(GuiNpcTextField tf){
    	textfields.put(tf.id,tf);
    }
    public GuiNpcTextField getTextField(int i){
    	return textfields.get(i);
    }
    public void addLabel(GuiNpcLabel label) {
		labels.put(label.id, label);
	}
    public GuiNpcLabel getLabel(int i){
    	return labels.get(i);
    }

    public void addSlider(GuiNpcSlider slider){
		sliders.put(slider.id,slider);
    	buttonList.add(slider);
    }
	public GuiNpcSlider getSlider(int i) {
		return sliders.get(i);
	}
	public void addScroll(GuiCustomScroll scroll) {
        scroll.setWorldAndResolution(mc, 350, 250);
        scrolls.put(scroll.id, scroll);
	}
	public GuiCustomScroll getScroll(int id){
		return scrolls.get(id); 
	}
	
    public abstract void save();
    
    @Override
    public void drawScreen(int i, int j, float f){
    	mouseX = i;
    	mouseY = j;
    	if(subgui == null || subgui.drawSubGuiBackground()){
	    	if(drawDefaultBackground)
	    		drawDefaultBackground();
	    	
	    	if(background != null && mc.renderEngine != null){
	    		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	    		mc.renderEngine.bindTexture(background);
	    		if(xSize > 256){
	    			drawTexturedModalRect(guiLeft, guiTop, 0, 0, 250, ySize);
	    			drawTexturedModalRect(guiLeft + 250, guiTop, 256 - (xSize - 250), 0, xSize - 250, ySize);
	    		}
	    		else
	        		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	    	}
	        for(GuiNpcLabel label : labels.values())
	        	label.drawLabel(this,fontRendererObj);
	    	for(GuiNpcTextField tf : textfields.values()){
	    		tf.drawTextBox();
	    	}
	        for(GuiCustomScroll scroll : scrolls.values())
	            scroll.drawScreen(i, j, f, hasSubGui()?0:Mouse.getDWheel());
	        for(GuiScreen gui : extra.values())
	        	gui.drawScreen(i, j, f);
	        super.drawScreen(i, j, f);
    	}
        if(subgui != null){
        	GlStateManager.translate(0, 0, 260F);
    		subgui.drawScreen(i,j,f);
    		GlStateManager.translate(0, 0, -260F);
        }
    }
    
    public boolean drawSubGuiBackground(){
    	return true;
    }
    
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}
	
	public void elementClicked() {
		if(subgui != null)
			subgui.elementClicked();
	}
	@Override
    public boolean doesGuiPauseGame(){
        return false;
    }
	
	public void doubleClicked() {
	}
	
	public boolean isInventoryKey(int i){
        return i == mc.gameSettings.keyBindInventory.getKeyCode(); //inventory key
	}
	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}

	public void displayGuiScreen(GuiScreen gui) {
		mc.displayGuiScreen(gui);
	}

    public void setSubGui(GuiNPCInterface gui){
    	subgui = gui;
		subgui.parent = this;
		subgui.setWorldAndResolution(mc, width, height);
    	initGui();
    }
    
	public void closeSubGui(GuiNPCInterface gui) {
		subgui = null;
		if(this instanceof ISubGuiListener){
			((ISubGuiListener)this).subGuiClosed(gui);
		}
		initGui();
	}
	
	public boolean hasSubGui() {
		return subgui != null;
	}
	
	public GuiNPCInterface getSubGui() {
		if(hasSubGui() && subgui.hasSubGui())
			return subgui.getSubGui();
		return subgui;
	}
	
	public void drawNpc(EntityLivingBase npc, int x, int y, float zoomed, int rotation){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft + x, guiTop + y, 50F);
        float scale = 1;
        if(npc.height > 2.4)
        	scale = 2 / npc.height;
        if(npc instanceof EntityPlayer){
        	//GlStateManager.translate(0, -data * scale * zoomed * 60, 0);
        }
        GlStateManager.scale(-60 * scale * zoomed, 60 * scale * zoomed, 60 * scale * zoomed);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		//GlStateManager.disableLighting();
		
		float f2 = npc.renderYawOffset;
		float f3 = npc.rotationYaw;
		float f4 = npc.rotationPitch;
		float f7 = npc.rotationYawHead;
		float f5 = (float) (guiLeft + x) - mouseX;
		float f6 = (float) ((guiTop + y) - 100 * scale * zoomed) - mouseY;
		GlStateManager.rotate(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-(float) Math.atan(f6 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
		npc.renderYawOffset = rotation;
		npc.rotationYaw = (float)Math.atan(f5 / 80F) * 40F + rotation;
		npc.rotationPitch = -(float) Math.atan(f6 / 40F) * 20F;
		npc.rotationYawHead = npc.rotationYaw;
		mc.getRenderManager().playerViewY = 180F;
		mc.getRenderManager().doRenderEntity(npc, 0, 0, 0,	0, 1, false);
		npc.prevRenderYawOffset = npc.renderYawOffset = f2;
		npc.prevRotationYaw = npc.rotationYaw = f3;
		npc.prevRotationPitch = npc.rotationPitch = f4;
		npc.prevRotationYawHead = npc.rotationYawHead = f7;
		GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
    public void openLink(String link){
        try{
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI(link)});
        }
        catch (Throwable throwable)
        {
        }
    }
}
