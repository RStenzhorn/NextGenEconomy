package de.rjst.nextgeneconomy.logic;


import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class VaultConnector implements Economy {

    private static final String ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS = "Economy does not support bank accounts!";
    private static final String CANNOT_NEGATIVE_FUNDS = "Cannot negative funds";
    private static final String CANNOT_NULL = "Cannot null funds";
    private static final String TRANSACTION_WAS_NOT_ALLOWED = "Transaction was not allowed!";

    private final NextGenEconomyApi nextGenEconomyApi;


    @Override
    public final EconomyResponse bankBalance(final String name) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse bankDeposit(final String name, final double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse bankHas(final String name, final double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse bankWithdraw(final String name, final double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse createBank(final String name, final String player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse createBank(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final boolean createPlayerAccount(final String playerName) {
        final Server server = Bukkit.getServer();
        final OfflinePlayer offlinePlayer = server.getOfflinePlayer(playerName);
        return createPlayerAccount(offlinePlayer);
    }

    public final boolean createPlayerAccount(final OfflinePlayer player) {
        return nextGenEconomyApi.createAccount(player.getUniqueId());
    }

    public final boolean createPlayerAccount(final String playerName, final String worldName) {
        return createPlayerAccount(playerName);
    }

    public final boolean createPlayerAccount(final OfflinePlayer player, final String worldName) {
        return createPlayerAccount(player);
    }

    public final String currencyNamePlural() {
        return nextGenEconomyApi.getCurrencyPlural();
    }

    public final String currencyNameSingular() {
        return nextGenEconomyApi.getCurrencySingular();
    }

    public final EconomyResponse deleteBank(final String name) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse depositPlayer(final String playerName, final double amount) {
        final Server server = Bukkit.getServer();
        final OfflinePlayer offlinePlayer = server.getOfflinePlayer(playerName);
        return depositPlayer(offlinePlayer, amount);
    }

    public final EconomyResponse depositPlayer(final OfflinePlayer player, final double amount) {
        if (0.0D > amount)
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, CANNOT_NEGATIVE_FUNDS);
        if (0.0D == amount)
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, CANNOT_NULL);

        final UUID uuid = player.getUniqueId();

        final Boolean success = nextGenEconomyApi.addBalance(uuid, new BigDecimal(amount));
        if (success) {
            return new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.FAILURE, TRANSACTION_WAS_NOT_ALLOWED);
        }
    }

    public final EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
        return depositPlayer(playerName, amount);
    }

    public final EconomyResponse depositPlayer(final OfflinePlayer player, final String worldName, final double amount) {
        return depositPlayer(player, amount);
    }

    public final String format(final double amount) {
        final BigDecimal bigDecimalAmount = new BigDecimal(amount);
        return nextGenEconomyApi.format(bigDecimalAmount);
    }

    public final int fractionalDigits() {
        return nextGenEconomyApi.getFractionalDigits();
    }

    public final double getBalance(final String playerName) {
        final Server server = Bukkit.getServer();
        final OfflinePlayer offlinePlayer = server.getOfflinePlayer(playerName);
        return getBalance(offlinePlayer);
    }

    public final double getBalance(final OfflinePlayer player) {
        final UUID uuid = player.getUniqueId();
        final Optional<BigDecimal> balance = nextGenEconomyApi.getBalance(uuid);
        return balance.map(BigDecimal::doubleValue).orElse(0.0D);

    }

    public final double getBalance(final String playerName, final String world) {
        return getBalance(playerName);
    }

    public final double getBalance(final OfflinePlayer player, final String world) {
        return getBalance(player);
    }

    public final List<String> getBanks() {
        return new ArrayList<>();
    }

    public final String getName() {
        return "NextGenEconomy";
    }

    public final boolean has(final String playerName, final double amount) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        return has(offlinePlayer, amount);
    }

    public final boolean has(final OfflinePlayer player, final double amount) {
        boolean result = true;

        final double balance = getBalance(player);
        if (amount > balance) {
            result = false;
        }

        return result;
    }

    public final boolean has(final String playerName, final String worldName, final double amount) {
        return has(playerName, amount);
    }

    public final boolean has(final OfflinePlayer player, final String worldName, final double amount) {
        return has(player, amount);
    }

    public final boolean hasAccount(final String playerName) {
        final Server server = Bukkit.getServer();
        final OfflinePlayer offlinePlayer = server.getOfflinePlayer(playerName);
        return hasAccount(offlinePlayer);
    }

    public final boolean hasAccount(final @NotNull OfflinePlayer player) {
        return nextGenEconomyApi.hasAccount(player.getUniqueId());
    }

    public final boolean hasAccount(final String playerName, final String worldName) {
        return hasAccount(playerName);
    }

    public final boolean hasAccount(final OfflinePlayer player, final String worldName) {
        return hasAccount(player);
    }

    public final boolean hasBankSupport() {
        return false;
    }

    public final EconomyResponse isBankMember(final String name, final String playerName) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse isBankMember(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse isBankOwner(final String name, final String playerName) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final EconomyResponse isBankOwner(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, ECONOMY_DOES_NOT_SUPPORT_BANK_ACCOUNTS);
    }

    public final boolean isEnabled() {
        return true;
    }

    public final EconomyResponse withdrawPlayer(final String playerName, final double amount) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(offlinePlayer, amount);
    }

    public final EconomyResponse withdrawPlayer(final OfflinePlayer player, final double amount) {
        if (amount < 0.0D)
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, CANNOT_NEGATIVE_FUNDS);
        if (amount == 0.0D)
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, CANNOT_NULL);

        final EconomyResponse result;

        final UUID uuid = player.getUniqueId();
        final Boolean transaction = nextGenEconomyApi.removeBalance(uuid, new BigDecimal(amount));
        if (transaction) {
            result = new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            result = new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.FAILURE, TRANSACTION_WAS_NOT_ALLOWED);
        }

        return result;
    }

    public final @NotNull EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
        return withdrawPlayer(playerName, amount);
    }

    public final EconomyResponse withdrawPlayer(final OfflinePlayer player, final String worldName, final double amount) {
        return withdrawPlayer(player, amount);
    }

}

