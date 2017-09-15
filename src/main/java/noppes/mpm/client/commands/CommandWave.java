package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandWave extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "wave";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] var2) {
		ClientEventHandler.processAnimation(EnumAnimation.WAVING.ordinal());
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/wave to wave";
	}

}
