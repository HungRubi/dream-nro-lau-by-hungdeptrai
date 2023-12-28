package real.item;
import java.util.ArrayList;

public class ItemTemplates {

    public static ArrayList<ItemTemplate> itemTemplates = new ArrayList<>();

    public static void add(ItemTemplate it) {
        ItemTemplates.itemTemplates.add(it.id, it);
    }

    public static ItemTemplate get(short id) {
        return (ItemTemplate) ItemTemplates.itemTemplates.get(id);
    }

    public static short getPart(short itemTemplateID) {
        return ItemTemplates.get(itemTemplateID).part;
    }

    public static short getIcon(short itemTemplateID) {
        return ItemTemplates.get(itemTemplateID).iconID;
    }

}
