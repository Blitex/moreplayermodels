package noppes.mpm;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class PacketHandlerServer{

	@SubscribeEvent
	public void onPacketData(ServerCustomPacketEvent event) {
		EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).playerEntity;
		ByteBuf buf = event.getPacket().payload();
		try {
			handlePacket(buf, (EntityPlayerMP) player, EnumPackets.values()[buf.readInt()]);
		
		} catch (Exception e) {
			LogWriter.except(e);
		}
	}

	private void handlePacket(ByteBuf buffer, EntityPlayerMP player, EnumPackets type) throws Exception {
		if(type == EnumPackets.PING){
			int version = buffer.readInt();			
			if(version == MorePlayerModels.Version){
				ModelData data = ModelData.get(player);
				data.readFromNBT(Server.readNBT(buffer));
				
				if(!player.worldObj.getGameRules().getBoolean("mpmAllowEntityModels"))
					data.entityClass = null;
				
				data.save();
				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}
			ItemStack back = player.inventory.mainInventory[0];
			if(back != null)
				Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_UPDATE, player.getUniqueID(), back.writeToNBT(new NBTTagCompound()));
			
			Server.sendData(player, EnumPackets.PING, MorePlayerModels.Version);
		}
//		else if(type == EnumPackets.REQUEST_PLAYER_DATA){
//			EntityPlayer pl = player.worldObj.getPlayerEntityByName(Server.readString(buffer));
//			if(pl == null)
//				return;
//			String hash = Server.readString(buffer);
//			ModelData data = PlayerDataController.instance.getPlayerData(pl);
//			if(!hash.equals(data.getHash()))
//				Server.sendData(player, EnumPackets.SEND_PLAYER_DATA, pl.getName(), data.writeToNBT());
//
//			ItemStack back = pl.inventory.mainInventory[0];
//			if(back != null)
//				Server.sendData(player, EnumPackets.BACK_ITEM_UPDATE, pl.getName(), back.writeToNBT(new NBTTagCompound()));
//			else
//				Server.sendData(player, EnumPackets.BACK_ITEM_REMOVE, pl.getName());
//		}
		else if(type == EnumPackets.UPDATE_PLAYER_DATA){
			ModelData data = ModelData.get(player);
			data.readFromNBT(Server.readNBT(buffer));
			
			if(!player.worldObj.getGameRules().getBoolean("mpmAllowEntityModels"))
				data.entityClass = null;
			data.save();
			Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
		}
		else if(type == EnumPackets.ANIMATION){
			EnumAnimation animation = EnumAnimation.values()[buffer.readInt()];
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

			Server.sendAssociatedData(player, EnumPackets.ANIMATION, player.getUniqueID(), animation);
			data.setAnimation(animation);
		}
	}
}
