package real.item;

import org.json.simple.JSONObject;

public class ItemTime {
    public short itemTempId;
    public long timeStart;
    public long timeLength;
    public long timeRemove;
    public static long A_SECOND = 1000;

    public ItemTime()
    {
    }

    public ItemTime(final short itemTempId, final long timeStart, final long timeLength) {
        this.itemTempId = itemTempId;
        this.timeStart = timeStart;
        this.timeLength = timeLength;
        this.timeRemove = System.currentTimeMillis() - timeStart + timeLength;
    }

    public static ItemTime fromJSONObject(JSONObject jsonObject) {
        ItemTime object = new ItemTime();
        object.itemTempId = Short.parseShort(jsonObject.get("itemTempId").toString());
        object.timeStart = Long.parseLong(jsonObject.get("timeStart").toString());
        object.timeLength = Long.parseLong(jsonObject.get("timeLength").toString());
        object.timeRemove = Long.parseLong(jsonObject.get("timeRemove").toString());
        return object;
    }

    public Object toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("itemTempId", this.itemTempId);
        object.put("timeStart", this.timeStart);
        object.put("timeLength", this.timeLength);
        object.put("timeRemove", this.timeRemove);
        object.put("saveTime", System.currentTimeMillis());
        return object;
    }
}
