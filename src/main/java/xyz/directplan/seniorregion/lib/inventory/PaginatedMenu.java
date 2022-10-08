package xyz.directplan.seniorregion.lib.inventory;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.config.ConfigKeys;
import xyz.directplan.seniorregion.lib.config.replacement.Replacement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author DirectPlan
 */
@Getter
public abstract class PaginatedMenu<T> extends InventoryUI {

    private int page = 1;
    private final int pageSize;


    public PaginatedMenu(String title, int rows) {
        this(title, rows, ((rows - 1) * 9)); // We're going to assume there's a header which will be ignored.
    }

    public PaginatedMenu(String title, int rows, int pageSize) {
        super(title, rows);
        this.pageSize = pageSize;
    }


    public void nextPage(Player player){
        this.page += 1;
        this.clearItems();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
        lock();
        this.open(player);
        unlock();
    }

    public void previousPage(Player player){
        this.page -= 1;
        this.clearItems();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
        lock();
        this.open(player);
        unlock();
    }

    public int getTotalPages(){
        int size = getList().size();
        return (int) Math.ceil(((double)size / pageSize));
    }

    // TODO: Remove all manual headers from what extends this class

    @Override
    public void build(Player player) {
        int nextPageSlot = 8;
        int previousPageSlot = 0;
        int totalPages = this.getTotalPages();

        // Building the page here before
        List<T> currentPageContents = this.getCurrentPageList();

        this.buildHeader();
        Replacement totalPageReplacement = new Replacement("total_pages", String.valueOf(totalPages));

        this.setTitle(getTitle().replace("current_page", String.valueOf(page)));

        if(this.page < totalPages){
            String nextPageDisplayName = ConfigKeys.PAGINATED_GUI_NEXT_PAGE.getStringValue(
                    new Replacement("next_page", String.valueOf(page + 1)), totalPageReplacement);

            MenuItem nextPageItem = new MenuItem(Material.ARROW, nextPageDisplayName, (item, clicker, clickType) -> this.nextPage(clicker));
            this.setSlot(nextPageSlot, nextPageItem);
        }
        if(this.page > 1) {
            String previousPageDisplayName = ConfigKeys.PAGINATED_GUI_PREVIOUS_PAGE.getStringValue(
                    new Replacement("previous_page", String.valueOf(page - 1)), totalPageReplacement);
            MenuItem previousPageItem = new MenuItem(Material.ARROW, previousPageDisplayName, (item, clicker, clickType) -> this.previousPage(clicker));
            this.setSlot(previousPageSlot, previousPageItem);
        }
        buildPage(player, currentPageContents);
    }

    public List<T> getCurrentPageList(){
        List<T> objects = new ArrayList<>(this.getList());

        List<T> paginatedObjects = new ArrayList<>();

        int page = (this.page - 1);

        if(page < 0 || page >= getTotalPages()) return objects;

        int min = page * pageSize;
        int max = (min + pageSize);
        if(max > objects.size()) {
            max = objects.size();
        }
        for(int i = min; i < max; i++){
            paginatedObjects.add(objects.get(i));
        }
        return paginatedObjects;
    }

    public abstract Collection<T> getList();

    public abstract void buildPage(Player player, List<T> pageContents);
}
