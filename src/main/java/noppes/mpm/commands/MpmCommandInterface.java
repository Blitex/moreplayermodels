package noppes.mpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class MpmCommandInterface extends CommandBase {

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender par1ICommandSender) {
		return true;
	}
	
	@Override
    public int getRequiredPermissionLevel(){
        return 0;
    }

	
	public boolean isPlayerOp(ICommandSender player){
		return player.canCommandSenderUseCommand(2, "mpm");
	}
}
