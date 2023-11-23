package de.rjst.nextgeneconomy.config.bean;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PaperPluginCommand extends Command {

    private final CommandExecutor commandExecutor;

    public PaperPluginCommand(@NotNull final String name, String[] alias, final CommandExecutor commandExecutor) {
        super(name);
        setAliases(Arrays.stream(alias).toList());
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
        return commandExecutor.onCommand(sender,this,commandLabel,args);
    }
}
