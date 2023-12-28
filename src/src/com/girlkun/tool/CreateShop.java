package com.girlkun.tool;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemOptionTemplate;
import real.item.ItemOptionTemplateDAO;
import real.item.ItemTemplate;
import real.item.ItemTemplateDAO;

public class CreateShop
        extends JFrame {

    int index;
    private List<Item> listItem;
    private List<Integer> listGold;
    private List<Integer> listGem;
    private DefaultTableModel model;
    private List<ItemTemplate> itemTemplates;
    private List<ItemOptionTemplate> itemOptionTemplates;
    private JComboBox<String> cboItem;
    private JComboBox<String> cboOption;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JLabel lblImage;
    private JTable tblList;
    private JTextField txtGem;
    private JTextField txtGold;
    private JTextArea txtInfo;
    private JTextField txtNPC;
    private JTextField txtParam;
    private JTextField txtTab;
    private JTextField txtTabName;

    public CreateShop() {
        this.index = -1;
        this.listItem = new LinkedList<>();
        this.listGold = new LinkedList<>();
        this.listGem = new LinkedList<>();
        initComponents();
        setup();
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.cboItem = new JComboBox<>();
        this.jLabel3 = new JLabel();
        this.txtParam = new JTextField();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.tblList = new JTable();
        this.jLabel4 = new JLabel();
        this.txtTab = new JTextField();
        this.jLabel5 = new JLabel();
        this.txtNPC = new JTextField();
        this.txtTabName = new JTextField();
        this.lblImage = new JLabel();
        this.jScrollPane2 = new JScrollPane();
        this.txtInfo = new JTextArea();
        this.jButton3 = new JButton();
        this.jLabel6 = new JLabel();
        this.txtGold = new JTextField();
        this.txtGem = new JTextField();
        this.jButton4 = new JButton();
        this.cboOption = new JComboBox<>();
        this.jLabel7 = new JLabel();
        setDefaultCloseOperation(3);
        this.jLabel1.setFont(new Font("sansserif", 1, 12));
        this.jLabel1.setHorizontalAlignment(0);
        this.jLabel1.setText("Item");
        this.jLabel2.setFont(new Font("sansserif", 1, 12));
        this.jLabel2.setHorizontalAlignment(0);
        this.jLabel2.setText("Cose (gold/ gem)");
        this.cboItem.setFont(new Font("sansserif", 1, 12));
        this.jLabel3.setFont(new Font("sansserif", 1, 12));
        this.jLabel3.setHorizontalAlignment(0);
        this.jLabel3.setText("Option");
        this.txtParam.setFont(new Font("sansserif", 1, 12));
        this.txtParam.setText("0");
        this.jButton1.setFont(new Font("sansserif", 1, 12));
        this.jButton1.setText("Add");
        this.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CreateShop.this.jButton1ActionPerformed(evt);
            }
        }
        );
        this.jButton2.setFont(new Font("sansserif", 1, 12));
        this.jButton2.setText("Add");
        this.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CreateShop.this.jButton2ActionPerformed(evt);
            }
        }
        );
        this.tblList.setFont(new Font("sansserif", 1, 12));
        this.tblList.setModel(new DefaultTableModel(new Object[0][], (Object[]) new String[0]));
        this.tblList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                CreateShop.this.tblListMouseClicked(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.tblList);
        this.jLabel4.setFont(new Font("sansserif", 1, 12));
        this.jLabel4.setHorizontalAlignment(0);
        this.jLabel4.setText("Tab");
        this.txtTab.setFont(new Font("sansserif", 1, 12));
        this.txtTab.setText("0");
        this.jLabel5.setFont(new Font("sansserif", 1, 12));
        this.jLabel5.setHorizontalAlignment(0);
        this.jLabel5.setText("Npc");
        this.txtNPC.setFont(new Font("sansserif", 1, 12));
        this.txtNPC.setText("0");
        this.txtTabName.setFont(new Font("sansserif", 1, 12));
        this.txtTabName.setText("Tab name");
        this.lblImage.setHorizontalAlignment(0);
        this.txtInfo.setEditable(false);
        this.txtInfo.setColumns(20);
        this.txtInfo.setFont(new Font("sansserif", 1, 12));
        this.txtInfo.setLineWrap(true);
        this.txtInfo.setRows(5);
        this.txtInfo.setWrapStyleWord(true);
        this.jScrollPane2.setViewportView(this.txtInfo);
        this.jButton3.setFont(new Font("sansserif", 1, 12));
        this.jButton3.setText("Create");
        this.jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CreateShop.this.jButton3ActionPerformed(evt);
            }
        });
        this.jLabel6.setFont(new Font("sansserif", 1, 12));
        this.jLabel6.setHorizontalAlignment(0);
        this.jLabel6.setText("Param");
        this.txtGold.setFont(new Font("sansserif", 1, 12));
        this.txtGold.setText("0");
        this.txtGem.setFont(new Font("sansserif", 1, 12));
        this.txtGem.setText("0");
        this.jButton4.setFont(new Font("sansserif", 1, 12));
        this.jButton4.setText("Remove");
        this.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CreateShop.this.jButton4ActionPerformed(evt);
            }
        });
        this.cboOption.setFont(new Font("sansserif", 1, 12));
        this.jLabel7.setFont(new Font("sansserif", 1, 12));
        this.jLabel7.setText("Param <= 32.767");
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jLabel1, -2, 99, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.cboItem, -2, 284, -2)).addGroup(layout.createSequentialGroup().addComponent(this.jLabel2, -2, 99, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtGold, -2, 135, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtGem, -2, 143, -2)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jLabel6, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jLabel3, GroupLayout.Alignment.LEADING, -1, 99, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.txtParam).addComponent(this.cboOption, 0, 274, 32767).addComponent(this.jLabel7, -1, -1, 32767)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jButton1, -2, 89, -2).addComponent(this.jButton2, -2, 89, -2))).addGroup(layout.createSequentialGroup().addComponent(this.jLabel4, -2, 99, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtTab, -2, 68, -2)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.txtTabName, -2, 210, -2).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addComponent(this.jLabel5, -2, 99, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtNPC, -2, 284, -2))).addGroup(layout.createSequentialGroup().addComponent(this.lblImage, -2, 201, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -2, 325, -2).addGap(0, 6, 32767)).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.jButton4, -2, 100, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton3, -2, 118, -2).addContainerGap()));
        layout.linkSize(0, new Component[]{this.cboItem, this.cboOption});
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel5, -1, -1, 32767).addComponent(this.txtNPC, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel4, -1, -1, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtTab, -2, -1, -2).addComponent(this.txtTabName, -2, -1, -2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jLabel1, -1, -1, 32767).addComponent(this.cboItem, -1, 39, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2, -2, 39, -2).addComponent(this.txtGold).addComponent(this.txtGem).addComponent(this.jButton1, -2, 39, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel3, -1, -1, 32767).addGroup(layout.createSequentialGroup().addComponent(this.cboOption, -2, 39, -2).addGap(6, 6, 6).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel6, -2, 17, -2).addComponent(this.txtParam).addComponent(this.jButton2, -1, -1, 32767)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel7).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.lblImage, -1, -1, 32767).addComponent(this.jScrollPane2, -1, 184, 32767))).addComponent(this.jScrollPane1, -1, 476, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jButton3, -1, 45, 32767).addComponent(this.jButton4, -1, -1, 32767)).addContainerGap(-1, 32767)));
        layout.linkSize(1, new Component[]{this.cboItem, this.jButton2, this.jLabel4, this.jLabel5, this.txtNPC, this.txtParam, this.txtTab, this.txtTabName});
        layout.linkSize(1, new Component[]{this.jLabel2, this.jLabel3, this.jLabel6, this.txtGem, this.txtGold});
        pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        Item item = new Item();
        ItemTemplate temp = this.itemTemplates.get(this.cboItem.getSelectedIndex());
        item.template = new ItemTemplate(temp.id, temp.type, temp.gender, temp.name, temp.description, temp.iconID, temp.part, temp.isUpToUp, temp.strRequire);
        this.listGold.add(Integer.valueOf(Integer.parseInt(this.txtGold.getText())));
        this.listGem.add(Integer.valueOf(Integer.parseInt(this.txtGem.getText())));
        ItemOption op = new ItemOption();
        op.optionTemplate = new ItemOptionTemplate(73, "",0);
        op.param = 0;
        item.itemOptions.add(op);
        this.listItem.add(item);
        fillToTable();
        this.index = this.listItem.size() - 1;
        showInfoItem(item);
        this.tblList.setRowSelectionInterval(this.index, this.index);
    }

    private void tblListMouseClicked(MouseEvent evt) {
        this.index = this.tblList.getSelectedRow();
        if (this.index != -1) {
            loadImageItem(((Item) this.listItem.get(this.index)).template.iconID);
            showInfoItem(this.listItem.get(this.index));
        }
    }

    private void fillToTable() {
        this.model.setRowCount(0);
        for (int i = 0; i < this.listItem.size(); i++) {
            this.model.addRow(new Object[]{
                Integer.valueOf(i), ((Item) this.listItem.get(i)).template.name, this.listGold.get(i), this.listGem.get(i)
            });
        }
        this.index = -1;
    }

    private void jButton2ActionPerformed(ActionEvent evt) {
        if (this.index != -1) {
            try {
                int param = Integer.parseInt(this.txtParam.getText());
                if (param >= 0) {
                    ItemOption op = new ItemOption();
                    op.optionTemplate = new ItemOptionTemplate(((ItemOptionTemplate) this.itemOptionTemplates.get(this.cboOption.getSelectedIndex())).id, ((ItemOptionTemplate) this.itemOptionTemplates.get(this.cboOption.getSelectedIndex())).name,0);
                    op.param = (short) param;
                    ((Item) this.listItem.get(this.index)).itemOptions.add(op);
                    showInfoItem(this.listItem.get(this.index));
                    JOptionPane.showMessageDialog(this, "done add option");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void jButton4ActionPerformed(ActionEvent evt) {
        if (this.index != -1) {
            this.listItem.remove(this.index);
            this.listGold.remove(this.index);
            this.listGem.remove(this.index);
            fillToTable();
        }
    }

    private void jButton3ActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == 0) {
            ShopData.writeShop(fileChooser.getSelectedFile().getAbsolutePath(), Integer.parseInt(this.txtNPC.getText()), Integer.parseInt(this.txtTab.getText()), this.txtTabName.getText(), this.listItem, this.listGold, this.listGem);
            JOptionPane.showMessageDialog(this, "Successed!");
        }
    }

    private void showInfoItem(Item item) {
        if (item != null) {
            this.txtInfo.setText("");
            this.txtInfo.append(item.template.name + "\n");
            for (ItemOption op : item.itemOptions) {
                this.txtInfo.append(op.optionTemplate.name.replaceAll("#", op.param + "") + "\n");
            }
        }
    }

    private void setup() {
        setLocationRelativeTo(null);
        setResizable(false);
        this.model = new DefaultTableModel((Object[]) new String[]{"Index", "Item", "Gold", "Gem"}, 0) {
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        this.tblList.setModel(this.model);
        init();
        this.cboItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                CreateShop.this.loadImageItem((CreateShop.this.itemTemplates.get(CreateShop.this.cboItem.getSelectedIndex())).iconID);
            }
        });
    }

    private void init() {
        this.itemTemplates = ItemTemplateDAO.getAll();
        this.itemTemplates.sort((o1, o2) -> o1.name.compareToIgnoreCase(o2.name));

        this.itemOptionTemplates = ItemOptionTemplateDAO.getAll();
        for (ItemOptionTemplate iot : this.itemOptionTemplates) {
            this.cboOption.addItem(iot.id + " - " + iot.name);
        }
        for (ItemTemplate it : this.itemTemplates) {
            this.cboItem.addItem(it.id + " - " + it.name + " (" + it.description + ")");
        }
    }

    private void loadImageItem(int iconId) {
        try {
            BufferedImage img = ImageIO.read(new File("data//icon//" + iconId));
            this.lblImage.setIcon(new ImageIcon(img.getScaledInstance(img.getWidth() * 2, img.getHeight() * 2, 4)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CreateShop.class.getName()).log(Level.SEVERE, (String) null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CreateShop.class.getName()).log(Level.SEVERE, (String) null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CreateShop.class.getName()).log(Level.SEVERE, (String) null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(CreateShop.class.getName()).log(Level.SEVERE, (String) null, ex);
        }

        EventQueue.invokeLater(() -> (new CreateShop()).setVisible(true));
    }
}
