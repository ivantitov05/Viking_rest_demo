package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;


public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService) {
        this.vikingService = vikingService;

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

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
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
}
