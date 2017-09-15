package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandDance extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "dance";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] var2) {
		ClientEventHandler.processAnimation(EnumAnimation.DANCING.ordinal());
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/dance to dance";
	}
}
