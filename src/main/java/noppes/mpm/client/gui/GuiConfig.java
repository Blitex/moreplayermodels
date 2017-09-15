package noppes.mpm.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;

public class GuiConfig extends GuiNPCInterface{
	
	public GuiConfig(){
    	xSize = 280;
        this.closeOnEsc = true;
	}

    @Override
    public void initGui() {
    	super.initGui();

		int y = guiTop + 20;
		
    	addButton(new GuiNpcButton(46, guiLeft, y += 32, 80, 20, "config.reloadskins"));
    	addButton(new GuiNpcButton(51, guiLeft + 90, y, 80, 20, "config.editbuttons"));

    	addButton(new GuiNpcButton(47, guiLeft + 90, y + 22, 50, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.EnablePOV?1:0));
		addLabel(new GuiNpcLabel(47, "config.pov", guiLeft, y + 27, 0xFFFFFF));

    	addButton(new GuiNpcButton(48, guiLeft + 90 + 144, y += 22, 60, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.EnableChatBubbles?1:0));
		addLabel(new GuiNpcLabel(48, "config.chatbubbles", guiLeft + 144, y + 5, 0xFFFFFF));

    	addButton(new GuiNpcButton(49, guiLeft + 90, y + 22, 50, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.EnableBackItem?1:0));
		addLabel(new GuiNpcLabel(49, "config.backitem", guiLeft, y + 27, 0xFFFFFF));

    	addButton(new GuiNpcButton(50, guiLeft + 90 + 144, y += 22, 50, 20, new String[]{"gui.no","1","2","3","4"}, MorePlayerModels.Tooltips));
		addLabel(new GuiNpcLabel(50, "config.tooltip", guiLeft + 144, y + 5, 0xFFFFFF));

    	addButton(new GuiNpcButton(57, guiLeft + 90 + 144, y + 22, 50, 20, new String[]{"gui.yes","gui.no"}, MorePlayerModels.HidePlayerNames?1:0));
		addLabel(new GuiNpcLabel(57, "config.names", guiLeft + 144, y + 27, 0xFFFFFF));

    	addButton(new GuiNpcButton(53, guiLeft + 90, y += 22, 50, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.EnableParticles?1:0));
		addLabel(new GuiNpcLabel(53, "config.particles", guiLeft, y + 5, 0xFFFFFF));

    	addButton(new GuiNpcButton(56, guiLeft + 90 + 144, y + 22, 50, 20, new String[]{"gui.yes","gui.no"}, MorePlayerModels.HideSelectionBox?1:0));
		addLabel(new GuiNpcLabel(56, "config.blockhighlight", guiLeft + 144, y + 27, 0xFFFFFF));

    	addButton(new GuiNpcButton(54, guiLeft + 90, y += 22, 50, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.HeadWearType));
		addLabel(new GuiNpcLabel(54, "config.solidheadlayer", guiLeft, y + 5, 0xFFFFFF));

    	addButton(new GuiNpcButton(55, guiLeft + 90, y += 22, 50, 20, new String[]{"gui.no","gui.yes"}, MorePlayerModels.Compatibility?1:0));
		addLabel(new GuiNpcLabel(55, "config.compatibility", guiLeft, y + 5, 0xFFFFFF));
    
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
    	super.actionPerformed(btn);
    	if(!(btn instanceof GuiNpcButton))
    		return;
    	GuiNpcButton button = (GuiNpcButton) btn;
    	
    	if(button.id == 46){
    		List<EntityPlayer> players = mc.theWorld.playerEntities;
    		for(EntityPlayer player : players){
                //SkinManager skinmanager = Minecraft.getMinecraft().getSkinManager();
                //skinmanager.func_152790_a(player.getGameProfile(), ((AbstractClientPlayer) player).func_175155_b(), true);
    			ModelData data = ModelData.get(player);
    			data.resourceLoaded = false;
    			data.resourceInit = false;
    		}
    		
    	}
    	if(button.id == 47){
    		MorePlayerModels.EnablePOV = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 48){
    		MorePlayerModels.EnableChatBubbles = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 49){
    		MorePlayerModels.EnableBackItem = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 50){
    		MorePlayerModels.Tooltips = button.getValue();
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 51){
            this.mc.displayGuiScreen(new GuiEditButtons());
    	}
    	if(button.id == 53){
    		MorePlayerModels.EnableParticles = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 54){
    		MorePlayerModels.HeadWearType = button.getValue();
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 55){
    		MorePlayerModels.Compatibility = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();

    		ClientProxy.FixModels(false);
    	}
    	if(button.id == 56){
    		MorePlayerModels.HideSelectionBox = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    	if(button.id == 57){
    		MorePlayerModels.HidePlayerNames = button.getValue() == 1;
    		MorePlayerModels.instance.configLoader.updateConfig();
    	}
    }

	@Override
	public void save() {
		
	}
}
