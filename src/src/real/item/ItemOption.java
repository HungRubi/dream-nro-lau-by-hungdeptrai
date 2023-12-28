package real.item;
import server.Util;

public class ItemOption {

    public short param;

    public ItemOptionTemplate optionTemplate;

    public ItemOption() {
    }
    
    public ItemOption(ItemOption io){
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }

    public ItemOption(int tempId, short param) {
        this.optionTemplate = ItemData.getItemOptionTemplate(tempId);
        this.param = param;
    }

    public String getOptionString() {
        return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
    }

}
