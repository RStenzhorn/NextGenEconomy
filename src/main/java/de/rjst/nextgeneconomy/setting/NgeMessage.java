package de.rjst.nextgeneconomy.setting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NgeMessage {
    MESSAGE_ERROR_PLAYER_NOT_FOUND(
            "messages.error.playerNotFound",
            "%PREFIX% Der Spieler wurde nicht gefunden.",
            "%PREFIX% Player was not found."
    ),
    MESSAGE_ERROR_NO_CONSOLE(
            "messages.error.noConsole",
            "%PREFIX% Der Befehl kann nicht über die Konsole ausgeführt werden!",
            "%PREFIX% The command cannot be executed through the console!"
    ),
    MESSAGE_ERROR_NO_PERMISSION(
            "messages.error.noPermission",
            "%PREFIX% Dafür hast du nicht die nötigen Rechte!",
            "%PREFIX% You do not have the necessary permissions for this!"
    ),
    MESSAGE_ERROR_INVALID_CURRENCY(
            "messages.error.invalidCurrency",
            "%PREFIX% Du hast einen ungültigen Geld-Betrag!",
            "%PREFIX% You have entered an invalid money amount!"
    ),
    MESSAGE_ERROR_NOT_ENOUGH(
            "messages.error.notEnough",
            "%PREFIX% Du hast nicht genug %CURRENCY_PLURAL%.",
            "%PREFIX% You do not have enough %CURRENCY_PLURAL%."
    ),
    MESSAGE_ERROR_INVALID_PAGE(
            "messages.error.invalidPage",
            "%PREFIX% Du hast eine ungültige Seitenzahl eingegeben.",
            "%PREFIX% You have entered an invalid page number."
    ),
    MESSAGE_ERROR_ADMIN(
            "messages.error.admin",
            "%PREFIX% Die Transaktion ist aufgrund eines außergewöhnlichen Fehlers fehlgeschlagen. Bitte kontaktiere einen Admin.",
            "%PREFIX% The transaction failed due to an exceptional error. Please contact an administrator."
    ),
    MESSAGE_CMD_BALANCE_USAGE(
            "messages.cmd.balance.usage",
            "%PREFIX% Verwende: /balance [Spieler]",
            "%PREFIX% Usage: /balance [Player]"
    ),
    MESSAGE_CMD_BALANCE(
            "messages.cmd.balance.default",
            "%PREFIX% Dein Kontostand: %CURRENCY%",
            "%PREFIX% Your balance: %CURRENCY%"
    ),
    MESSAGE_CMD_BALANCE_OTHER(
            "messages.cmd.balance.other",
            "%PREFIX% Der Kontostand von %PLAYER%: %CURRENCY%",
            "%PREFIX% %PLAYER%'s balance: %CURRENCY%"
    ),
    MESSAGE_CMD_BALANCE_TOP(
            "messages.cmd.balancetop.default",
            "%PREFIX% #%POSITION% - %PLAYER%: %CURRENCY%",
            "%PREFIX% #%POSITION% - %PLAYER%: %CURRENCY%"
    ),

    MESSAGE_CMD_BALANCE_TOP_HEADER(
            "messages.cmd.balancetop.header",
            "--- BALANCETOP - Seite %PAGE% / %MAX_PAGE% ---",
            "--- BALANCETOP - Page %PAGE% / %MAX_PAGE% ---"
    ),
    MESSAGE_CMD_BALANCE_TOP_USAGE(
            "messages.cmd.balancetop.usage",
            "%PREFIX% Verwende: /balancetop [Seite]",
            "%PREFIX% Usage: /balancetop [Page]"
    ),
    MESSAGE_CMD_PAY_SOURCE(
            "messages.cmd.pay.source",
            "%PREFIX% Du hast an %PLAYER% einen Betrag in Höhe %CURRENCY% bezahlt.",
            "%PREFIX% You paid %CURRENCY% to %PLAYER%."
    ),
    MESSAGE_CMD_PAY_TARGET(
            "messages.cmd.pay.target",
            "%PREFIX% Du hast von %PLAYER% einen Betrag in Höhe %CURRENCY% erhalten.",
            "%PREFIX% You received %CURRENCY% from %PLAYER%."
    ),
    MESSAGE_CMD_PAY_USAGE(
            "messages.cmd.pay.usage",
            "%PREFIX% Verwende: /pay <Spieler> <Betrag>",
            "%PREFIX% Usage: /pay <Player> <Amount>"
    ),
    MESSAGE_CMD_ECO_USAGE(
            "messages.cmd.eco.usage",
            "%PREFIX% Verwende: /eco <take | give | set> <Spieler> <Betrag>",
            "%PREFIX% Usage: /pay <Player> <Amount>"
    ),

    MESSAGE_CMD_ECO_TAKE(
            "messages.cmd.eco.take",
            "%PREFIX% Du hast %PLAYER% einen Betrag in Höhe von %CURRENCY% abgezogen.",
            ""
    ),
    MESSAGE_CMD_ECO_SET(
            "messages.cmd.eco.set",
            "%PREFIX% Du hast den Kontostand %PLAYER% auf %CURRENCY% gesetzt.",
            ""
    ),
    MESSAGE_CMD_ECO_GIVE(
            "messages.cmd.eco.give",
            "%PREFIX% Du hast %PLAYER% einen Betrag in Höhe von %CURRENCY% hinzugefügt.",
            ""
    ),;


    private final String path;

    @Getter
    private final String german;
    @Getter
    private final String english;

    public String getPath(final @NotNull Locale locale) {
        return String.format("%s.%s", path, locale.getLanguage());
    }
}
