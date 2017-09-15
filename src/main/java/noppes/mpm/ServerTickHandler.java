package noppes.mpm;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.client.AnalyticsTracking;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class ServerTickHandler {
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.side == Side.CLIENT || event.phase == Phase.START)
			return;
    	EntityPlayerMP player = (EntityPlayerMP) event.player;
    	ModelData data = ModelData.get(player);
    	ItemStack item = player.inventory.mainInventory[0];
    	if(data.backItem != item){
	    	if(item == null){
	    		Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_REMOVE, player.getUniqueID());
	    	}
	    	else{
	    		NBTTagCompound tag = item.writeToNBT(new NBTTagCompound());
	    		Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_UPDATE, player.getUniqueID(), tag);
	    	}
	    	data.backItem = item;
    	}
    	data.eyes.update(player);
        if(data.animation != EnumAnimation.NONE)
        	checkAnimation(player, data);
    	data.prevPosX = player.posX;
    	data.prevPosY = player.posY;
    	data.prevPosZ = player.posZ;
	}
	
	public static void checkAnimation(EntityPlayer player, ModelData data){
		if(data.prevPosY <= 0 || player.ticksExisted < 40)
			return;
    	double motionX = data.prevPosX - player.posX;
    	double motionY = data.prevPosY - player.posY;
    	double motionZ = data.prevPosZ - player.posZ;

    	double speed = motionX * motionX +  motionZ * motionZ;
    	boolean isJumping = motionY * motionY > 0.08;

    	if(data.animationTime > 0)
    		data.animationTime--;

    	if(player.isPlayerSleeping() || player.isRiding() || data.animationTime == 0  || data.animation == EnumAnimation.BOW && player.isSneaking())
    		data.setAnimation(EnumAnimation.NONE);
    	    	
    	if(!isJumping && player.isSneaking() && (data.animation == EnumAnimation.HUG || data.animation == EnumAnimation.CRAWLING || 
    			data.animation == EnumAnimation.SITTING || data.animation == EnumAnimation.DANCING))
    		return;
    	if(speed > 0.01 || isJumping || player.isPlayerSleeping() || player.isRiding() || data.isSleeping() && speed > 0.001)
    		data.setAnimation(EnumAnimation.NONE);
	}

	private String serverName = null;
	@SubscribeEvent
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
		if(serverName == null){
			String e = "local";
			MinecraftServer server = event.player.getServer();
			if(server.isDedicatedServer()){
				try {
					e = InetAddress.getByName(server.getServerHostname()).getCanonicalHostName();
				} catch (UnknownHostException e1) {
					e = server.getServerHostname();
				}
				if(server.getServerPort() != 25565)
					e += ":" + server.getServerPort();
			}
			if(e == null || e.startsWith("192.168") || e.contains("127.0.0.1") || e.startsWith("localhost"))
				e = "local";
			serverName = e;
		}
		AnalyticsTracking.sendData(event.player, "join", serverName);
	}
}
