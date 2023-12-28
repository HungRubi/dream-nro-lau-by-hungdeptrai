package real.item;

import java.util.ArrayList;

public class Item {

    public int id;

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public ArrayList<ItemOption> itemOptions = new ArrayList<>();

    public Item() {

    }

    public Item(Item item) {
        this.id = item.id;
        this.template = item.template;
        this.info = item.info;
        this.content = item.content;
        this.quantity = item.quantity;
        for (ItemOption io : item.itemOptions) {
            this.itemOptions.add(new ItemOption(io));
        }
    }

    public static void main(String[] args) {

    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

}
