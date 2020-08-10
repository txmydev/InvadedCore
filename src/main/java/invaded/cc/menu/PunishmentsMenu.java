package invaded.cc.menu;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.punishment.Punishment;
import invaded.cc.util.DateUtils;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PunishmentsMenu extends Menu {

    private int page;
    private Profile profile;
    private List<Punishment> punishments;

    public PunishmentsMenu(Profile target, List<Punishment> punishments) {
        super("Punishments of " + target.getColoredName(), 27);

        this.page = 1;
        this.profile = target;
        this.punishments = punishments;
    }

    @Override
    public void update() {
        int slot = 9;

        if (profile.getBan() != null) {
            String formatAt = new SimpleDateFormat("dd'/'MM 'at' hh:mm:ss").format(new Date(profile.getBan().getPunishedAt()));
            String formatExpire = profile.getBan().getType() == Punishment.Type.BAN || profile.getBan().getType()
                    == Punishment.Type.BLACKLIST ? "Never" : DateUtils.formatTime(profile.getBan().getExpire() - System.currentTimeMillis());

            ItemBuilder itemBuilder = new ItemBuilder()
                    .type(Material.PAPER).name("&bActive Punishment")
                    .lore("&7&m" + Strings.repeat('-', 20)
                            , "&fPunished At&7: &b" + formatAt
                            , "&fPunished By&7: &f" + profile.getBan().getStaffName()
                            , "&fExpires In&7: &b" + formatExpire
                            , "&fReason&7: &b" + profile.getBan().getReason()
                            , "&7&m" + Strings.repeat('-', 20));

            inventory.setItem(slot++, itemBuilder.build());
        }

        if (profile.getMute() != null) {
            String formatAt = new SimpleDateFormat("dd'/'MM 'at' hh:mm:ss").format(new Date(profile.getMute().getPunishedAt()));
            String formatExpire = profile.getMute().getType()
                    == Punishment.Type.MUTE ? "Never" : DateUtils.formatTime(profile.getMute().getExpire() - System.currentTimeMillis());

            ItemBuilder itemBuilder = new ItemBuilder()
                    .type(Material.PAPER).name("&bActive Mute")
                    .lore("&7&m" + Strings.repeat('-', 20)
                            , "&fPunished At&7: &b" + formatAt
                            , "&fPunished By&7: &f" + profile.getMute().getStaffName()
                            , "&fExpires In&7: &b" + formatExpire
                            , "&fReason&7: &b" + profile.getMute().getReason()
                            , "&7&m" + Strings.repeat('-', 20));

            inventory.setItem(slot++, itemBuilder.build());
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'/'MM 'at' hh:mm:ss");

        int index = page * 27 - 27;

        while (slot < 26 && index < punishments.size()) {
            Punishment punishment = punishments.get(index);

            String formatAt = simpleDateFormat.format(new Date(punishment.getPunishedAt()));

            ItemBuilder itemBuilder = new ItemBuilder()
                    .type(Material.PAPER).name("&b" + punishment.getType().getNice())
                    .lore("&7&m" + Strings.repeat('-', 20)
                            , "&fPunished At&7: &b" + formatAt
                            , "&fPunished By&7: &f" + punishment.getStaffName()
                            , "&fReason&7: &b" + punishment.getReason()
                            , "&7&m" + Strings.repeat('-', 20));

            if (punishment.getRemovedAt() > 0L) {
                String formatRemoved = simpleDateFormat.format(new Date(punishment.getRemovedAt()));
                itemBuilder.lore("&fRemoved At&7: &b" + formatRemoved);
            }

            if (!punishment.getRemovedBy().equals("")) {
                itemBuilder.lore("&fRemoved By&7: &b" + punishment.getRemovedBy());
                itemBuilder.lore("&7&m" + Strings.repeat('-', 20));
            }

            inventory.setItem(slot++, itemBuilder.build());
            index++;
        }

        inventory.setItem(8, new ItemBuilder().type(Material.CARPET)
                .data(5).name("&bNext Page")
                .lore("&7Click to see the next page"
                        , "&7of " + profile.getName() + "'s punishments").build());

        inventory.setItem(0, new ItemBuilder().type(Material.CARPET)
                .data(5).name("&cPrevious Page")
                .lore("&7Click to see the previous page"
                        , "&7of " + profile.getName() + "'s punishments").build());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 0) {
            if (page == 1) return;
            page--;
            update();
        } else if (slot == 8) {
            if (page == getTotalPages()) return;
            page++;
            update();
        }
    }

    private int getTotalPages() {
        return punishments.size() / 19 + 1;
    }
}
