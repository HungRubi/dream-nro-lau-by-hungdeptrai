package tool;

import java.io.FileNotFoundException;
import real.item.CaiTrangData;
import real.item.ItemOptionTemplateDAO;
import real.item.ItemTemplateDAO;

public class LoadDB {

    public static void main(String[] args) throws FileNotFoundException {
        ItemTemplateDAO.readFile();
        ItemOptionTemplateDAO.readfile();
   
        CaiTrangData.main(args);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        CaiTrangData.loadCaiTrang();

//        ItemData.itemTemplates = (ArrayList<ItemTemplate>) ItemTemplateDAO.getAll();
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopAppule.txt"); //2
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopBunma.txt"); //0
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopDende.txt"); //1
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_0.txt"); //0
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_1.txt"); //1
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_2.txt"); // 2
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_0.txt"); //0
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_1.txt"); //1
//        ItemShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_2.txt"); //2
//
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopAppule_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopBunma_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopDende_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_0_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_1_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopSanta_2_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_0_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_1_option.txt");
//        ItemOptionShopDAO.readFile("C:\\Users\\adm\\Desktop\\shop\\shopUron_2_option.txt");
    }
}
