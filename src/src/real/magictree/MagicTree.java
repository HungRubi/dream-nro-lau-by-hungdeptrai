package real.magictree;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import real.npc.Npc;
import real.player.Player;
import real.player.PlayerManger;
import server.Util;
import server.io.Message;

public class MagicTree  implements Runnable {
    
        public long playerId;
        public String name;
        public int level;
        public int currentPea;
        public int maxPea;
        public boolean isUpdate;
        public int timeUpdate;
        public int timeOnPea;
        public int gemOnPea;// Kết ngọc nhanh
        /// Upp đậu lv tiếp theo
        public int timeUpPea; 
        public int goldUpPea;
        public int gemUpPea; 
        public short idDau;
        
        public int hpOnPea;
        
        public MagicTree(long playerId, int level, int currentPea, boolean isUpdate, int timeUpdate) {
            this.playerId = playerId;
            this.name = "Đậu thần cấp " + level;
            this.level = level;
            this.currentPea = currentPea;
            this.isUpdate = isUpdate;
            this.timeUpdate = (int) (timeUpdate == 0 ? (System.currentTimeMillis()/1000)+60 : timeUpdate);
            this.active(level, level);
            new Thread(this).start();
            
        }
        
        public void setInfoDauThan(int level){
        this.timeOnPea = 60*level;
        this.name = "Đậu thần cấp "+level;
        switch(level){
            case 1:
                this.maxPea = 5;
                this.timeUpPea = Util.timeToInt(0,0,10);
                this.gemOnPea = 1;
                this.goldUpPea = 5000;
                this.gemUpPea = 20;
                this.hpOnPea = 100;
                this.idDau = 13;
                break;
            case 2:
                this.maxPea = 7;
                this.timeUpPea = Util.timeToInt(0,1,40);
                this.gemOnPea = 2;
                this.goldUpPea = 10000;
                this.gemUpPea = 50;
                this.hpOnPea = 500;
                this.idDau = 60;
                break;
            case 3:
                this.maxPea = 9;
                this.timeUpPea = Util.timeToInt(0,16,22);
                this.gemOnPea = 5;
                this.goldUpPea = 100000;
                this.gemUpPea = 120;
                this.hpOnPea = 2000;
                this.idDau = 61;
                break;
            case 4:
                this.maxPea = 10;
                this.timeUpPea = Util.timeToInt(6,22,0);
                this.gemOnPea = 7;
                this.goldUpPea = 1000000;
                this.gemUpPea = 300;
                this.hpOnPea = 4000;
                this.idDau = 62;
                break;
            case 5:
                this.maxPea = 12;
                this.timeUpPea = Util.timeToInt(13,22,0);
                this.gemOnPea = 9;
                this.goldUpPea = 10000000;
                this.gemUpPea = 800;
                this.hpOnPea = 8000;
                this.idDau = 63;
                break;
            case 6:
                this.maxPea = 14;
                this.timeUpPea = Util.timeToInt(30,0,0);
                this.gemOnPea = 12;
                this.goldUpPea = 20000000;
                this.gemUpPea = 1500;
                this.hpOnPea = 16000;
                this.idDau = 64;
                break;
            case 7:
                this.maxPea = 15;
                this.timeUpPea = Util.timeToInt(55,0,0);
                this.gemOnPea = 15;
                this.goldUpPea = 50000000;
                this.gemUpPea = 3000;
                this.hpOnPea =  32000;
                this.idDau = 65;
                break;
            case 8:
                this.maxPea = 17;
                this.timeUpPea = Util.timeToInt(69,0,0);
                this.gemOnPea = 20;
                this.goldUpPea = 100000000;
                this.gemUpPea = 6000;
                this.hpOnPea = 64000;
                this.idDau = 352;
                break;
            case 9:
                this.maxPea = 20;
                this.timeUpPea = Util.timeToInt(100,0,0);
                this.gemOnPea = 25;
                this.goldUpPea = 300000000;
                this.gemUpPea = 7500;
                this.hpOnPea = 128000;
                this.idDau = 523;
                break;
            case 10:
                this.maxPea = 25;
                this.timeUpPea = Util.timeToInt(150,0,0);
                this.gemOnPea = 30;
                this.goldUpPea = 1000000000;
                this.gemUpPea = 10000;
                this.hpOnPea = 256000;
                this.idDau = 595;
                break;
        }
    }
        
    
        public void displayMagicTree(Player pl){
            Message msg;
            try{
                this.name = "Đậu thần cấp "+this.level;
                this.setInfoDauThan(this.level);
                int[] iddauthan = null;
                int x = 0, y = 0;
                switch (pl.gender) {
                    case 0:
                        iddauthan = new int[]{84, 85, 86, 87, 88, 89, 90};
                        x = 348;
                        y = 336;
                        break;
                    case 1:
                        iddauthan = new int[]{371, 372, 373, 374, 375, 376, 377};
                         x = 372;
                        y = 336;
                        break;
                    case 2:
                        iddauthan = new int[]{378, 379, 380, 381, 382, 383, 384};
                         x = 348;
                        y = 336;
                        break;
                }
                   int timeDau = (this.timeUpdate -  (int)(System.currentTimeMillis()/1000));
                   msg = new Message(-34);
                   msg.writer().writeByte(0);
                        switch (level) {
                             case 1:
                                  msg.writer().writeShort(iddauthan[0]);
                            break;
                            case 2:
                            case 3:
                                msg.writer().writeShort(iddauthan[1]);
                            break;
                            case 4:
                                msg.writer().writeShort(iddauthan[2]);
                            break;
                            case 5:
                            case 6:
                                msg.writer().writeShort(iddauthan[3]);
                            break;
                            case 7:
                                msg.writer().writeShort(iddauthan[4]);
                            break;
                            case 8:
                            case 9:
                                msg.writer().writeShort(iddauthan[5]);
                            break;
                            case 10:
                                msg.writer().writeShort(iddauthan[6]);
                            break;
                     }
                    msg.writer().writeUTF(this.name);
                    msg.writer().writeShort(x);
                    msg.writer().writeShort(y);
                    msg.writer().writeByte(this.level);//cấp đậu
                    msg.writer().writeShort(this.currentPea);//số lượng hiện có
                    msg.writer().writeShort(this.maxPea);//max đậu
                    msg.writer().writeUTF("Đang kết hạt");
                    msg.writer().writeInt(timeDau);//time ra hạt đậu hoặc update
                    msg.writer().writeByte(0);//vị trí đậu
                    String[] vitri = new String[]{"32 86", "5 77", "5 77", "8 89", "29 68", "4 63", "18 61", "33 53", "8 48", "26 39", "11 36", "33 23", "18 25", "4 20", "26 12", "12 7", "19 0"};
                    for (int i = 0; i < 0; i++) {
                        msg.writer().writeByte((Integer.parseInt((vitri[i].split(" "))[0])));
                        msg.writer().writeByte((Integer.parseInt((vitri[i].split(" "))[1])));
                    }
                    msg.writer().writeBoolean(this.isUpdate);
                    pl.sendMessage(msg);
                    msg.cleanup();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
     public void update(){
            
            if((int)(System.currentTimeMillis()/1000) >= this.timeUpdate && this.currentPea < this.maxPea){
                this.currentPea += 1;
                if(this.currentPea > this.maxPea){ 
                    this.currentPea = this.maxPea;
                }
                this.timeUpdate = (int)(System.currentTimeMillis()/1000)+this.timeOnPea;
                displayMagicTree(PlayerManger.gI().getPlayerByID((int) this.playerId));
            }
     }   
        
    public void run() {
            
    }
    public Timer timer;
    protected boolean actived = false;

    public void active(int delay, int period) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, 1000L, 1000L);
            
            
        }
    }
}
