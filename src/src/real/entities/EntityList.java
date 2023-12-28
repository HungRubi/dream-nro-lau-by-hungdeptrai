package real.entities;

import real.player.Inventory;
import real.player.Player;

public class EntityList extends Player {

    public EntityList() {
        this.inventory = new Inventory(this);
    }

}
