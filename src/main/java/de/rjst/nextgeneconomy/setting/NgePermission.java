package de.rjst.nextgeneconomy.setting;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

@Getter
public enum NgePermission {

    CMD_PAY("nge.cmd.pay"),
    CMD_BALANCE("nge.cmd.balance"),
    CMD_BALANCE_OTHER("nge.cmd.balance.other"),
    CMD_BALANCE_TOP("nge.cmd.balancetop"),
    CMD_ECO("nge.cmd.eco");


    private final Permission permission;

    NgePermission(final String perm) {
        permission = new Permission(perm);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.addPermission(permission);
    }
}
