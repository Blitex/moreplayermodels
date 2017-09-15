package noppes.mpm.client;

import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.ServerTickHandler;
import noppes.mpm.client.fx.EntityEnderFX;
import noppes.mpm.client.fx.EntityRainbowFX;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.GuiMPM;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.constants.EnumParts;
import noppes.mpm.util.MPMEntityUtil;

public class ClientEventHandler {
	private long lastAltClick = 0;
	private boolean altIsPressed = false;
	
	private World prevWorld;
	private List<EntityPlayer> playerlist;

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event){
		Minecraft mc = Minecraft.getMinecraft();
		if(mc == null || mc.thePlayer == null)
			return;
		if(ClientProxy.Screen.isPressed()){
			ModelData data = ModelData.get(mc.thePlayer);
			data.setAnimation(EnumAnimation.NONE);
			if(mc.currentScreen == null){
				mc.displayGuiScreen(new GuiMPM());
			}
		}
		if(mc.currentScreen == null){
			slashPressed = Keyboard.getEventCharacter() == '/';
		}
		if(!mc.inGameHasFocus)
			return;
		if(ClientProxy.Sleep.isPressed()){
			processAnimation(MorePlayerModels.button1);
		}
		if(ClientProxy.Sit.isPressed()){
			processAnimation(MorePlayerModels.button2);
		}
		if(ClientProxy.Dance.isPressed()){
			processAnimation(MorePlayerModels.button3);
		}
		if(ClientProxy.Hug.isPressed()){
			processAnimation(MorePlayerModels.button4);
		}
		if(ClientProxy.Crawl.isPressed()){
			processAnimation(MorePlayerModels.button5);
		}

		if(ClientProxy.Camera.isKeyDown() && mc.gameSettings.thirdPersonView == 1){
			long time = System.currentTimeMillis();
			if(!altIsPressed){
				if(time - lastAltClick < 400){
					camera.reset();
				}
				else{
					camera.enabled();
					lastAltClick = time;					
				}
			}
			altIsPressed = true;
		}
		else{
			altIsPressed = false;
		}
	}

	@SubscribeEvent
	public void keyEvent(GuiScreenEvent.KeyboardInputEvent.Pre event){
		if(event.getGui() instanceof GuiChat){
			if(!slashPressed && Keyboard.getEventCharacter() == '/'){
				slashPressed = true;
			}
//			if(Keyboard.getEventKey() == Keyboard.KEY_NUMPADENTER || Keyboard.getEventKey() == Keyboard.KEY_RETURN){
//				slashPressed = false;
//			}
		}
		else
			slashPressed = false;
	}

	private boolean slashPressed = false;
	@SubscribeEvent
	public void onCommand(CommandEvent event){//dirty fix for client commands not checking for /
		if(!(event.getCommand() instanceof MpmCommandInterface) || event.getSender().getServer() != null || slashPressed)
			return;
		event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onMouse(MouseEvent event){
		Minecraft mc = Minecraft.getMinecraft();
		if(event.getDwheel() == 0 || !mc.inGameHasFocus || mc.gameSettings.thirdPersonView != 1 || !camera.enabled || !altIsPressed)
			return;
		camera.cameraDistance -= event.getDwheel() / 100f;
		if(camera.cameraDistance > 14)
			camera.cameraDistance = 14;
		else if(camera.cameraDistance < 1)
			camera.cameraDistance = 1;
		event.setCanceled(true);
	}

	public static void processAnimation(int type) {
		if(type < 0)
			return;
		if(MorePlayerModels.HasServerSide)
			Client.sendData(EnumPackets.ANIMATION, type);
		else{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			EnumAnimation animation = EnumAnimation.values()[type];
			if(animation == EnumAnimation.SLEEPING_SOUTH){
				float rotation = player.rotationYaw;
				while(rotation < 0)
					rotation += 360;
				while(rotation > 360)
					rotation -= 360;
				int rotate = (int) ((rotation + 45) / 90);
				if(rotate == 1)
					animation = EnumAnimation.SLEEPING_WEST;
				if(rotate == 2)
					animation = EnumAnimation.SLEEPING_NORTH;
				if(rotate == 3)
					animation = EnumAnimation.SLEEPING_EAST;
			}
			ModelData data = ModelData.get(player);
			if(data.animationEquals(animation))
				animation = EnumAnimation.NONE;
			data.setAnimation(animation.ordinal());
		}
	}
	
	public static Camera camera = new Camera();
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event){
        camera.update(event.phase == Phase.START);	
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event){
		if(event.side == Side.SERVER || event.phase == Phase.START)
			return;
    	Minecraft mc = Minecraft.getMinecraft();
    	if(mc.theWorld != null && prevWorld != mc.theWorld){
			MorePlayerModels.HasServerSide = false;
			GuiCreationScreenInterface.Message = "message.noserver";
			ModelData data = ModelData.get(mc.thePlayer);
			Client.sendData(EnumPackets.PING, MorePlayerModels.Version, data.writeToNBT());
			prevWorld = mc.theWorld;
    		ClientProxy.FixModels(false);
    	}
    	RenderEvent.lastSkinTick++;
	}

	@SubscribeEvent
	public void onCamera(EntityViewRenderEvent.CameraSetup event){
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping() || mc.gameSettings.thirdPersonView != 1){
        	return;
        }
        float f = entity.getEyeHeight();
        double partialTicks = event.getRenderPartialTicks();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        double d3 = camera.cameraDistance - 4;
        
        float f1 = entity.rotationYaw;
        float f2 = entity.rotationPitch;

        double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
        double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
        double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

        for (int i = 0; i < 8; ++i)
        {
            float f3 = (float)((i & 1) * 2 - 1);
            float f4 = (float)((i >> 1 & 1) * 2 - 1);
            float f5 = (float)((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            RayTraceResult raytraceresult = mc.theWorld.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

            if (raytraceresult != null)
            {
                double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

                if (d7 < d3)
                {
                    d3 = d7;
                }
            }
        }
        GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
        GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.side == Side.SERVER || event.phase == Phase.START)
			return;
    	EntityPlayer player = event.player;
		ModelData data = ModelData.get(player);
    	EntityLivingBase entity = data.getEntity(player);
    	Minecraft mc = Minecraft.getMinecraft();
    	if(entity != null){
    		//entity.posY -= player.yOffset;
    		entity.onUpdate();
			MPMEntityUtil.Copy(player, entity);
			return;
    	}
    	
    	if(!MorePlayerModels.HasServerSide)
    		data.eyes.update(player);
    	
        if (data.inLove > 0){
            --data.inLove;
            if(player.getRNG().nextBoolean()){
                double d0 = player.getRNG().nextGaussian() * 0.02D;
                double d1 = player.getRNG().nextGaussian() * 0.02D;
                double d2 = player.getRNG().nextGaussian() * 0.02D;
                player.worldObj.spawnParticle(EnumParticleTypes.HEART, player.posX + player.getRNG().nextFloat() * player.width * 2.0F - player.width, player.posY + 0.5D + player.getRNG().nextFloat() * player.height, player.posZ + player.getRNG().nextFloat() * player.width * 2.0F - player.width, d0, d1, d2);
            
            }
        }
        if(data.animation == EnumAnimation.CRY){
            float f1 = player.rotationYaw * (float)Math.PI / 180.0F;
            float dx = -MathHelper.sin(f1);
            float dz = MathHelper.cos(f1);
            for (int i = 0; (float)i < 10; ++i){
                float f2 = (player.getRNG().nextFloat() - 0.5f) * player.width * 0.5f + dx * 0.15f;
                float f3 = (player.getRNG().nextFloat() - 0.5f) * player.width * 0.5f + dz * 0.15f;
                player.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, player.posX + (double)f2, player.posY - data.getBodyY() + 1.1f - player.getYOffset(), player.posZ + (double)f3, 0.0000000000000000000000001f, 0, 0.0000000000000000000000001f);
            }
        }
        if(data.animation != EnumAnimation.NONE)
        	ServerTickHandler.checkAnimation(player, data);
        
    	data.prevPosX = player.posX;
    	data.prevPosY = player.posY;
    	data.prevPosZ = player.posZ;
        
        ModelPartData particles = data.getPartData(EnumParts.PARTICLES);
        if(particles != null)
        	spawnParticles(player, data, particles);
	}
	
	private void spawnParticles(EntityPlayer player, ModelData data, ModelPartData particles) {
		if(!MorePlayerModels.EnableParticles)
			return;
		Minecraft minecraft =  Minecraft.getMinecraft();
		double height = player.getYOffset() + data.getBodyY();
		Random rand = player.getRNG();
		if(particles.type == 0){
			for(int i = 0; i< 2; i++){
				EntityEnderFX fx = new EntityEnderFX((AbstractClientPlayer) player, (rand.nextDouble() - 0.5D) * (double)player.width, (rand.nextDouble() * (double)player.height) - height - 0.25D, (rand.nextDouble() - 0.5D) * (double)player.width, (rand.nextDouble() - 0.5D) * 2D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2D, particles);
				minecraft.effectRenderer.addEffect(fx);
			}
    		
		}
		else if(particles.type == 1){
        	for(int i = 0; i < 2; i++){
	            double x = player.posX + (rand.nextDouble() - 0.5D) * 0.9;
	            double y = (player.posY + rand.nextDouble() * 1.9) - 0.25D - height;
	            double z = player.posZ + (rand.nextDouble() - 0.5D) * 0.9;
	
	            
	            double f = (rand.nextDouble() - 0.5D) * 2D;
	            double f1 =  -rand.nextDouble();
	            double f2 = (rand.nextDouble() - 0.5D) * 2D;
	            
	            minecraft.effectRenderer.addEffect(new EntityRainbowFX(player.worldObj, x, y, z, f, f1, f2));
        	}
		}
	}
}
