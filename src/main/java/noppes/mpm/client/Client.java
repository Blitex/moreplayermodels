package noppes.mpm.client;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class Client {

	public static void sendData(EnumPackets enu, Object... obs) {
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		try {
			if(!Server.fillBuffer(buffer, enu, obs))
				return;
			MorePlayerModels.Channel.sendToServer(new FMLProxyPacket(buffer, "MorePlayerModels"));
		} catch (IOException e) {
			LogWriter.except(e);
		}
	}
}
