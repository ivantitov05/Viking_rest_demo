package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;
import ru.mephi.vikingdemo.service.VikingSpecialService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;


public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingTableModel tableModel = new VikingTableModel();
    private final VikingSpecialService vikingSpecialService;

    public VikingDesktopFrame(VikingService vikingService, VikingSpecialService vikingSpecialService) {
        this.vikingService = vikingService;
        this.vikingSpecialService = vikingSpecialService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());

        JButton statsButton = new JButton(" Статистика викингов");
        statsButton.addActionListener(event -> openStatsDialog());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        bottomPanel.add(statsButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        onInit();
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }

    private void openStatsDialog() {
        SwingUtilities.invokeLater(() -> {
            VikingStatsDialog statsDialog = new VikingStatsDialog(this, vikingService, vikingSpecialService);
            statsDialog.setVisible(true);
        });
    }

    public void deleteViking(String name) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String vikingName = (String) tableModel.getValueAt(i, 0);
            if (vikingName.equalsIgnoreCase(name)) {
                tableModel.deleteRow(i);
                break;
            }
        }
    }

    public void updateViking(String oldName, Viking updatedViking){
        boolean updated = tableModel.updateViking(oldName, updatedViking);
        
        if (updated) {
            System.out.println("✓ GUI updated: " + oldName + " -> " + updatedViking.name());
            
            // Опционально: показать уведомление
            JOptionPane.showMessageDialog(this, 
                "Viking updated successfully!\n" +
                "Old name: " + oldName + "\n" +
                "New name: " + updatedViking.name(),
                "Update Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("✗ Viking not found in GUI: " + oldName);
            
            // Опционально: показать ошибку
            JOptionPane.showMessageDialog(this, 
                "Viking '" + oldName + "' not found in table",
                "Update Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addNewViking(Viking viking){
        tableModel.addViking(viking);
    }

    private void onInit() {
        List<Viking> all = vikingService.findAll();
        if (!all.isEmpty()){
            for (Viking viking : all) {
                tableModel.addViking(viking);
            }
        }
    }
}
