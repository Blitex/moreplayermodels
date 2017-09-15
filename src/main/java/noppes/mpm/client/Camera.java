package noppes.mpm.client;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Camera {
	public boolean enabled = false;

	public float cameraYaw = 0;
	public float cameraPitch = 0;

	public float playerYaw = 0;
	public float playerPitch = 0;
	
	public float cameraDistance = 4;
	

	public void update(boolean start){
		Minecraft mc = Minecraft.getMinecraft();
		Entity view = mc.getRenderViewEntity();
		if(mc.gameSettings.thirdPersonView != 1){
			if(enabled)
				reset();
			return;
		}
		if(!enabled || view == null)
			return;
		
		updateCamera();
		if(start){			
			view.rotationYaw = view.prevRotationYaw = cameraYaw;
			view.rotationPitch = view.prevRotationPitch = cameraPitch;
		}
		else{
			view.rotationYaw = mc.thePlayer.rotationYaw - cameraYaw + playerYaw;
			view.prevRotationYaw = mc.thePlayer.prevRotationYaw - cameraYaw + playerYaw;
			view.rotationPitch = -playerPitch;
			view.prevRotationPitch = -playerPitch;
		}
	}
	
	private void updateCamera(){
		Minecraft mc = Minecraft.getMinecraft();
		if(!mc.inGameHasFocus)
			return;
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        double dx = Mouse.getDX() * f1 * 0.15D;
        double dy = Mouse.getDY() * f1 * 0.15D;
        if(ClientProxy.Camera.isKeyDown()){
        	cameraYaw += dx;
        	cameraPitch += dy;
        	cameraPitch = MathHelper.clamp_float(cameraPitch, -90.0F, 90.0F);
        }
        else{
        	playerYaw += dx;
        	playerPitch += dy;
        	playerPitch = MathHelper.clamp_float(playerPitch, -90.0F, 90.0F);
        }
        
	}

	public void reset() {
		enabled = false;
		cameraYaw = 0;
		cameraPitch = 0;
		playerYaw = 0;
		playerPitch = 0;
		cameraDistance = 4;
	}

	public void enabled() {
		Minecraft mc = Minecraft.getMinecraft();
		if(!enabled){
			cameraYaw = playerYaw = mc.thePlayer.rotationYaw;
			cameraPitch = mc.thePlayer.rotationPitch;
			playerPitch = -cameraPitch;
		}
		enabled = true;
	}
}
