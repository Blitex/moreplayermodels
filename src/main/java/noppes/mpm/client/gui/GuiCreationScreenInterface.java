package noppes.mpm.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.util.MPMEntityUtil;

public abstract class GuiCreationScreenInterface extends GuiNPCInterface implements ISubGuiListener, ISliderListener{
	public static String Message = "";
	public EntityLivingBase entity;
	
	public int active = 0;

	private EntityPlayer player;
	public int xOffset = 0;
	public ModelData playerdata;
	
	public static GuiCreationScreenInterface Gui = new GuiCreationParts();
	
	private static float rotation = 0.5f;
	
	public GuiCreationScreenInterface(){
		playerdata = ModelData.get(Minecraft.getMinecraft().thePlayer);
		xSize = 400;
		ySize = 240;
		xOffset = 140;

		player = Minecraft.getMinecraft().thePlayer;
		this.closeOnEsc = true;
	}

    @Override
    public void initGui() {
    	super.initGui();
    	entity = playerdata.getEntity(mc.thePlayer);
    	Keyboard.enableRepeatEvents(true);

    	addButton(new GuiNpcButton(0, guiLeft, guiTop, 60, 20, "gui.options"));
    	addButton(new GuiNpcButton(1, guiLeft + 62, guiTop, 60, 20, "gui.entity"));
    	if(entity == null)
    		addButton(new GuiNpcButton(2, guiLeft, guiTop + 23, 60, 20, "gui.parts"));
    	else{
    		GuiCreationExtra gui = new GuiCreationExtra();
    		gui.playerdata = playerdata;
    		if(!gui.getData(entity).isEmpty())
    			addButton(new GuiNpcButton(2, guiLeft, guiTop + 23, 60, 20, "gui.extra"));
    		else if(active == 2){
        		openGui(new GuiCreationEntities());
    			return;
    		}
    	}
    	if(entity == null)
    		addButton(new GuiNpcButton(3, guiLeft + 62, guiTop + 23, 60, 20, "gui.scale"));
    	getButton(active).enabled = false;
    	addButton(new GuiNpcButton(66, guiLeft + xSize - 20, guiTop, 20, 20, "X"));
    	    	
    	addLabel(new GuiNpcLabel(0, Message, guiLeft + 120, guiTop + ySize - 10, 0xff0000));
    	getLabel(0).center(xSize - 120);
    	
    	addSlider(new GuiNpcSlider(this, 500, guiLeft + xOffset + 142, guiTop + 210, 120, 20, rotation));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
    	super.actionPerformed(btn);
    	if(btn.id == 0){
    		openGui(new GuiCreationOptions());
    	}
    	if(btn.id == 1){
    		openGui(new GuiCreationEntities());
    	}
    	if(btn.id == 2){
    		if(entity == null)
    			openGui(new GuiCreationParts());
    		else
    			openGui(new GuiCreationExtra());
    	}
    	if(btn.id == 3){
    		openGui(new GuiCreationScale());
    	}
//    	if(btn.id == 4){
//    		this.setSubGui(new GuiPresetSave(this, playerdata));
//    	}
//    	if(btn.id == 5){
//    		openGui(new GuiCreationLoad());
//    	}
    	if(btn.id == 66){
    		close();
    	}
    }
    
    @Override
    public void drawScreen(int x, int y, float f){
    	super.drawScreen(x, y, f);
    	entity = playerdata.getEntity(mc.thePlayer);
    	EntityLivingBase entity = this.entity;
    	if(entity == null)
    		entity = this.player;
    	else
    		MPMEntityUtil.Copy(mc.thePlayer, player);
    	
    	drawNpc(entity, xOffset + 200, 200, 1, (int)(rotation * 360 - 180));
    }
    
    @Override
    public void onGuiClosed(){
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void save(){
    	
    }
    
    @Override
    public boolean drawSubGuiBackground(){
    	return true;
    }
    
    public void openGui(GuiNPCInterface gui){
    	parent.setSubGui(gui);
    	if(gui instanceof GuiCreationScreenInterface)
    		Gui = (GuiCreationScreenInterface) gui;
    }
    
	public void subGuiClosed(GuiNPCInterface subgui){
		initGui();
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		if(slider.id == 500){
			rotation = slider.sliderValue;
			slider.setString("" + (int)(rotation * 360));
		}
	}

	@Override
	public void mousePressed(GuiNpcSlider slider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GuiNpcSlider slider) {
		// TODO Auto-generated method stub
		
	}
}
