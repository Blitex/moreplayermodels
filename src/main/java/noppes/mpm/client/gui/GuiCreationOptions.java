package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationOptions extends GuiCreationScreenInterface implements ITextfieldListener{
	
	public GuiCreationOptions(){
		xOffset = 150;
	}

    @Override
    public void initGui() {
    	super.initGui();

		int y = guiTop + 50;

	    addButton(new GuiNpcButton(9, guiLeft + 58, y, 80, 20, new String[]{"gui.default", "config.humanfemale", "config.humanmale", "config.goblinmale"}, playerdata.soundType));
		addLabel(new GuiNpcLabel(5, "config.sounds", guiLeft, y + 5, 0xFFFFFF));

		addTextField(new GuiNpcTextField(52, this, guiLeft + 60, y += 23, 200, 20, playerdata.url));
		addLabel(new GuiNpcLabel(52, "config.skinurl", guiLeft, y + 5, 0xFFFFFF));    
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
    	super.actionPerformed(btn);
    	if(!(btn instanceof GuiNpcButton))
    		return;
    	GuiNpcButton button = (GuiNpcButton) btn;

    	if(button.id == 9){
    		playerdata.soundType = (short) button.getValue();
    	}
    }
    

	@Override
	public void unFocused(GuiNpcTextField guiNpcTextField) {
		playerdata.url = guiNpcTextField.getText();
		playerdata.resourceInit = false;
	}
}
