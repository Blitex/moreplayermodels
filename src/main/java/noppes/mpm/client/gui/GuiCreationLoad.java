package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.ModelData;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;

public class GuiCreationLoad extends GuiNPCInterface implements ICustomScrollListener{

	private List<String> list = new ArrayList<String>();
	private GuiCustomScroll scroll;
	private static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/smallbg.png");

	private ModelData playerdata;	
	private NBTTagCompound original = new NBTTagCompound();
	
	private String selected = "Normal";
	
	private HashMap<String,Preset> presets = Preset.GetDefault();
	
	public GuiCreationLoad(){
		playerdata = ModelData.get(Minecraft.getMinecraft().thePlayer);
		original = playerdata.writeToNBT();
		drawDefaultBackground = false;
		closeOnEsc = true;
		presets.putAll(PresetController.instance.presets);
	}

    @Override
    public void initGui() {
    	super.initGui();
    	if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
            for(Preset preset : presets.values())
            	list.add(preset.name);
            
    		scroll.setList(list);
        	scroll.setSelected(selected);
        	scroll.scrollTo(selected);
    	}
    	scroll.guiLeft = guiLeft + 4;
    	scroll.guiTop = guiTop + 33;
    	scroll.setSize(100, 144);
    	
    	addScroll(scroll);
    	addTextField(new GuiNpcTextField(0, this, guiLeft + 4, guiTop + 12, 172, 20, "New"));
    	addButton(new GuiNpcButton(10, guiLeft + 4, guiTop + ySize - 46, 86, 20, "gui.done"));
    	addButton(new GuiNpcButton(11, guiLeft + 92, guiTop + ySize - 46, 86, 20, "gui.cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
    	super.actionPerformed(btn);
    	if(btn.id == 10){
    		original = playerdata.writeToNBT();
    		Preset p = new Preset();
    		p.menu = true;
    		String name = getTextField(0).getText(); 
    		if(name.trim().isEmpty())
    			name = "New";
    		p.name = name;
    		p.data = playerdata.copy();
    		PresetController.instance.selected = name;
    		PresetController.instance.addPreset(p);
    		close();
    	}
    	if(btn.id == 11){
    		close();
    	}
    }

	@Override
    public void drawScreen(int i, int j, float f){
    	drawDefaultBackground();
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop + 8, 0, 0, xSize, 192);
        //drawTexturedModalRect(guiLeft + 4, guiTop + 8, 56, 0, 200, ySize);
                
        super.drawScreen(i, j, f);
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  
    	
    	GuiInventory.drawEntityOnScreen(guiLeft + 144, guiTop + 140, 40, guiLeft + 144 - i, guiTop + 80 - j, player);
    }

	@Override
	public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		selected = scroll.getSelected();
    	Preset preset = presets.get(selected.toLowerCase());
    	if(preset != null){
    		playerdata.readFromNBT(preset.data.writeToNBT());
    		initGui();
    	}
	}

	@Override
	public void save() {
		playerdata.readFromNBT(original);
	}
}
