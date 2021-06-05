package invaded.core.menu;

import invaded.core.profile.Profile;
import invaded.core.rank.Rank;
import invaded.core.util.Common;
import invaded.core.util.ItemBuilder;
import invaded.core.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class PermissionMenu {

    public static class ProfileMenu extends Menu {

        private final Profile profile;
        private int page;

        public ProfileMenu(Profile profile) {
            super("&bPermissions of " + profile.getColoredName(), 27);
            this.profile = profile;
            this.page = 1;
        }

        private int getTotalPages(){
            return profile.getPermissions().size() / 27 + 1;
        }

        @Override
        public void update() {
            List<String> list = new ArrayList<>(profile.getPermissions());

            int slot = 9;
            int index = page * 27 - 27;

            this.inventory.setItem(0, new ItemBuilder().type(Material.CARPET).data(page == 1 ? 7 : 14).name((page == 1 ? ChatColor.GRAY : ChatColor.RED) + "Previous Page").build());
            this.inventory.setItem(8, new ItemBuilder().type(Material.CARPET).data( page + 1 > getTotalPages() ? 7 : 13).name((page + 1 > getTotalPages() ? ChatColor.GRAY : ChatColor.GREEN) + "Next Page").build());

            while(slot < 27 && index < list.size()) {
                inventory.setItem(slot++,
                        new ItemBuilder()
                        .type(Material.BOOK_AND_QUILL)
                        .name("&fPermission #" + index)
                        .lore(Common.getLine(20))
                        .lore("&fValue&7: &b" + list.get(index))
                        .lore(Common.getLine(20))
                        .build()
                );

                index++;
            }
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if(event.getSlot() == 0) {
                if(page == 1) return;
                page--;
                update();
            }
            else if(event.getSlot() == 8) {
                if(page == getTotalPages()) return;
                page++;
            }
        }
    }

    public static class RankMenu extends Menu {

        private final Rank rank;
        private int page;

        public RankMenu(Rank rank) {
            super("&bPermissions of " + rank.getColoredName(), 27);
            this.rank = rank;
            this.page = 1;
        }

        private int getTotalPages(){
            return rank.getPermissions().size() / 27 + 1;
        }

        @Override
        public void update() {
            List<String> list = new ArrayList<>(rank.getPermissions());

            int slot = 9;
            int index = page * 27 - 27;

            this.inventory.setItem(0, new ItemBuilder().type(Material.CARPET).data(page == 1 ? 7 : 14).name((page == 1 ? ChatColor.GRAY : ChatColor.RED) + "Previous Page").build());
            this.inventory.setItem(8, new ItemBuilder().type(Material.CARPET).data( page + 1 > getTotalPages() ? 7 : 13).name((page + 1 > getTotalPages() ? ChatColor.GRAY : ChatColor.GREEN) + "Next Page").build());

            while(slot < 27 && index < list.size()) {
                inventory.setItem(slot++,
                        new ItemBuilder()
                                .type(Material.BOOK_AND_QUILL)
                                .name("&fPermission #" + index)
                                .lore(Common.getLine(20))
                                .lore("&fValue&7: &b" + list.get(index))
                                .lore(Common.getLine(20))
                                .build()
                );

                index++;
            }
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if(event.getSlot() == 0) {
                if(page == 1) return;
                page--;
                update();
            }
            else if(event.getSlot() == 8) {
                if(page == getTotalPages()) return;
                page++;
            }
        }
    }
}
