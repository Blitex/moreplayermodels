package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.List;

import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabMPM;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelData;
import noppes.mpm.client.Client;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.constants.EnumPackets;

public class GuiMPM extends GuiNPCInterface implements ICustomScrollListener, ISubGuiListener{
	public static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/smallbg.png");
	
	public ModelData playerdata;	
	protected NBTTagCompound original = new NBTTagCompound();
	
	private GuiCustomScroll scroll = null;
	
	public GuiMPM() {
		playerdata = ModelData.get(Minecraft.getMinecraft().thePlayer);
		original = playerdata.writeToNBT();
        xSize = 182;
        ySize = 185;
        this.drawDefaultBackground = false;
        this.closeOnEsc = true;
        if(PresetController.instance.presets.isEmpty())
        	PresetController.instance.load();
	}

	@Override
    public void initGui(){
        super.initGui();

		TabRegistry.updateTabValues(guiLeft + 2, guiTop + 8, InventoryTabMPM.class);
		TabRegistry.addTabsToList(buttonList);
        if(scroll == null){
    		scroll = new GuiCustomScroll(this, 0);
    		scroll.setSize(80, 160);
        }		
		List<String> list = new ArrayList<String>();
		for(Preset preset : PresetController.instance.presets.values()){
			if(preset.menu)
				list.add(preset.name);
		}
		scroll.setList(list);
		scroll.setSelected(PresetController.instance.selected);
		if(!scroll.hasSelected()){
			scroll.selected = 0;
		}
		scroll.guiLeft = guiLeft + 4;
		scroll.guiTop = guiTop + 14;
		addScroll(scroll);

		addButton(new GuiNpcButton(0, guiLeft + 4, guiTop + 176, 20, 20, "+"));
		addButton(new GuiNpcButton(1, guiLeft + 26, guiTop + 176, 20, 20, "-"));
		getButton(1).enabled = scroll.getList().size() > 1;

		addButton(new GuiNpcButton(2, guiLeft + 48, guiTop + 176, 60, 20, "selectServer.edit"));
		addButton(new GuiNpcButton(3, guiLeft + 110, guiTop + 176, 68, 20, "gui.config"));
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
    	
    	GuiInventory.drawEntityOnScreen(guiLeft + 130, guiTop + 130, 40, guiLeft + 130 - i, guiTop + 60 - j, player);
    }

	@Override
	public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    	Preset preset = PresetController.instance.getPreset(scroll.getSelected());
    	if(preset != null){
    	   playerdata.readFromNBT(preset.data.writeToNBT());
    	   PresetController.instance.selected = preset.name;
    	}
	}
    
    @Override
	protected void actionPerformed(GuiButton button){
    	if(!(button instanceof GuiNpcButton))
    		return;
    	if(button.id == 0){
    		setSubGui(new GuiCreationLoad());
    	}
    	if(button.id == 1){
    		GuiYesNo gui = new GuiYesNo(new GuiYesNoCallback(){

				@Override
				public void confirmClicked(boolean result, int id) {
					if(result){
						PresetController.instance.removePreset(scroll.getSelected());
						scroll.getList().remove(scroll.getSelected());
						Preset preset = PresetController.instance.getPreset(scroll.getList().get(0));
				    	playerdata.readFromNBT(preset.data.writeToNBT());
						PresetController.instance.selected = preset.name;
					}
		    		Minecraft.getMinecraft().displayGuiScreen(GuiMPM.this);
				}
    			
    		}, "", I18n.translateToLocal("message.delete"), 0);
    		this.mc.displayGuiScreen(gui);
    	}
    	if(button.id == 2){
			try {
				setSubGui(GuiCreationScreenInterface.Gui.getClass().newInstance());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
    	}
    	if(button.id == 3){
    		setSubGui(new GuiConfig());
    	}
    }
    
	@Override
	public void save() {
    	NBTTagCompound newCompound = playerdata.writeToNBT();
    	if(!original.equals(newCompound)){
			playerdata.save();
			Client.sendData(EnumPackets.UPDATE_PLAYER_DATA, newCompound);
    		original = newCompound;
    	}
	}

	@Override
	public void subGuiClosed(GuiNPCInterface subgui) {
		if(subgui instanceof GuiCreationScreenInterface){
			Preset p = PresetController.instance.getPreset(getScroll(0).getSelected());
			if(p != null){
				p.data = playerdata.copy();
				PresetController.instance.save();
			}
		}
	}

}