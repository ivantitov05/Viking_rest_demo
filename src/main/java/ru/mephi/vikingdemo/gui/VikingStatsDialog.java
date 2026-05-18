package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class VikingStatsDialog extends JDialog {

    private final VikingService vikingService;
    private JTabbedPane tabbedPane;
    private JTextArea randomTallArea;
    private JTable legendaryTable;
    private JTable redheadsTable;
    private JTextArea idOperationsArea;

    public VikingStatsDialog(JFrame parent, VikingService vikingService) {
        super(parent, "Статистика викингов", true);
        this.vikingService = vikingService;
        initUI();
        loadData();
        setSize(950, 650);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Случайный высокий викинг", createRandomTallPanel());
        tabbedPane.addTab("Легендарное снаряжение", createLegendaryPanel());
        tabbedPane.addTab("Рыжеволосые (по возрасту)", createRedheadsPanel());
        tabbedPane.addTab("Операции с ID", createIdOperationsPanel());

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createRandomTallPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        randomTallArea = new JTextArea();
        randomTallArea.setEditable(false);
        randomTallArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        randomTallArea.setBackground(new Color(240, 248, 255));

        JButton randomButton = new JButton("Показать случайного высокого викинга");
        randomButton.setFont(randomButton.getFont().deriveFont(Font.BOLD, 14f));
        randomButton.addActionListener(e -> showRandomTallViking());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(randomButton);

        panel.add(new JScrollPane(randomTallArea), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showRandomTallViking() {
        List<Viking> tallVikings = vikingService.findAll().stream()
                .filter(v -> v.heightCm() > 180)
                .toList();

        if (tallVikings.isEmpty()) {
            randomTallArea.setText("Нет викингов ростом выше 180 см!");
            return;
        }

        Random random = new Random();
        Viking viking = tallVikings.get(random.nextInt(tallVikings.size()));

        StringBuilder equipmentText = new StringBuilder();
        viking.equipment().forEach(item ->
                equipmentText.append(String.format("║    • %-20s (%s)%28s ║\n",
                        item.name(), item.quality(), ""))
        );

        String text = String.format(
                "╔══════════════════════════════════════════════════════════════╗\n" +
                        "║                    СЛУЧАЙНЫЙ ВЫСОКИЙ ВИКИНГ                   ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣\n" +
                        "║  Имя:        %-30s ║\n" +
                        "║  Возраст:    %-30d ║\n" +
                        "║  Рост:       %-30d ║\n" +
                        "║  Цвет волос: %-30s ║\n" +
                        "║  Борода:     %-30s ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣\n" +
                        "║  СНАРЯЖЕНИЕ:                                                 ║\n" +
                        "%s" +
                        "╚══════════════════════════════════════════════════════════════╝",
                viking.name(), viking.age(), viking.heightCm(),
                viking.hairColor(), viking.beardStyle(),
                equipmentText.toString()
        );

        randomTallArea.setText(text);
    }

    private JPanel createLegendaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Имя", "Возраст", "Рост", "Цвет волос", "Борода", "Легендарное снаряжение"};
        legendaryTable = new JTable(new DefaultTableModel(columnNames, 0));
        legendaryTable.setRowHeight(25);
        legendaryTable.getTableHeader().setFont(legendaryTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        JScrollPane scrollPane = new JScrollPane(legendaryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRedheadsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Имя", "Возраст", "Рост", "Борода", "Снаряжение"};
        redheadsTable = new JTable(new DefaultTableModel(columnNames, 0));
        redheadsTable.setRowHeight(25);
        redheadsTable.getTableHeader().setFont(redheadsTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton ascButton = new JButton("По возрастанию возраста");
        JButton descButton = new JButton("По убыванию возраста");

        ascButton.addActionListener(e -> loadRedheads(true));
        descButton.addActionListener(e -> loadRedheads(false));

        sortPanel.add(ascButton);
        sortPanel.add(descButton);

        panel.add(sortPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(redheadsTable), BorderLayout.CENTER);

        return panel;
    }


    private JPanel createIdOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Операции с ID викингов (индекс в списке)", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        idOperationsArea = new JTextArea();
        idOperationsArea.setEditable(false);
        idOperationsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        idOperationsArea.setBackground(new Color(240, 248, 255));

        JButton allIdsButton = new JButton("Все ID");
        JButton maxIdButton = new JButton("Max ID (последний)");
        JButton evenIdsButton = new JButton("Четные ID");
        JButton oddIdsButton = new JButton("Нечетные ID");
        JButton statsButton = new JButton("Статистика ID");

        allIdsButton.addActionListener(e -> showAllIds());
        maxIdButton.addActionListener(e -> showMaxId());
        evenIdsButton.addActionListener(e -> showEvenIds());
        oddIdsButton.addActionListener(e -> showOddIds());
        statsButton.addActionListener(e -> showIdStats());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(allIdsButton);
        buttonPanel.add(maxIdButton);
        buttonPanel.add(evenIdsButton);
        buttonPanel.add(oddIdsButton);
        buttonPanel.add(statsButton);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Результат"));
        resultPanel.add(new JScrollPane(idOperationsArea), BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private List<Integer> getAllIds() {
        return IntStream.rangeClosed(1, vikingService.findAll().size())
                .boxed()
                .toList();
    }

    private List<IdVikingPair> getVikingsWithIds() {
        List<Viking> vikings = vikingService.findAll();
        return IntStream.range(0, vikings.size())
                .mapToObj(i -> new IdVikingPair(i + 1, vikings.get(i)))
                .toList();
    }

    private record IdVikingPair(int id, Viking viking) {}

    private void showAllIds() {
        List<Integer> ids = getAllIds();

        if (ids.isEmpty()) {
            idOperationsArea.setText("Нет викингов в базе данных!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                      ВСЕ ID ВИКИНГОВ                          ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Всего викингов: %-44d ║\n", ids.size()));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║  ID: ");

        for (int i = 0; i < ids.size(); i++) {
            if (i > 0 && i % 15 == 0) sb.append("\n║       ");
            sb.append(String.format("%3d ", ids.get(i)));
        }
        sb.append("\n╚══════════════════════════════════════════════════════════════╝");

        idOperationsArea.setText(sb.toString());
    }

    private void showMaxId() {
        List<Viking> vikings = vikingService.findAll();

        if (vikings.isEmpty()) {
            idOperationsArea.setText("Нет викингов в базе данных!");
            return;
        }

        List<IdVikingPair> vikingsWithIds = IntStream.range(0, vikings.size())
                .mapToObj(i -> new IdVikingPair(i + 1, vikings.get(i)))
                .toList();

        IdVikingPair maxPair = vikingsWithIds.stream()
                .max(Comparator.comparingInt(IdVikingPair::id))
                .orElse(null);

        if (maxPair == null) {
            idOperationsArea.setText("Ошибка: не удалось найти максимальный ID");
            return;
        }

        int maxId = maxPair.id();
        Viking lastViking = maxPair.viking();

        String result = String.format(
                "╔══════════════════════════════════════════════════════════════╗\n" +
                        "║                    ПОСЛЕДНЯЯ ЗАПИСЬ (MAX ID)                  ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣\n" +
                        "║  Max ID: %-50d ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣\n" +
                        "║  ИНФОРМАЦИЯ О ВИКИНГЕ:                                        ║\n" +
                        "║    Имя:        %-40s ║\n" +
                        "║    Возраст:    %-40d ║\n" +
                        "║    Рост:       %-40d ║\n" +
                        "║    Волосы:     %-40s ║\n" +
                        "║    Борода:     %-40s ║\n" +
                        "╚══════════════════════════════════════════════════════════════╝",
                maxId,
                lastViking.name(),
                lastViking.age(),
                lastViking.heightCm(),
                lastViking.hairColor(),
                lastViking.beardStyle()
        );

        idOperationsArea.setText(result);
    }

    private void showEvenIds() {
        List<Integer> ids = getAllIds();

        if (ids.isEmpty()) {
            idOperationsArea.setText("Нет викингов в базе данных!");
            return;
        }

        List<Integer> evenIds = ids.stream()
                .filter(id -> id % 2 == 0)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                      ЧЕТНЫЕ ID ВИКИНГОВ                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Всего четных ID: %-44d ║\n", evenIds.size()));

        if (!evenIds.isEmpty()) {
            sb.append("╠══════════════════════════════════════════════════════════════╣\n");
            sb.append("║  ID: ");
            for (int i = 0; i < evenIds.size(); i++) {
                if (i > 0 && i % 15 == 0) sb.append("\n║       ");
                sb.append(String.format("%3d ", evenIds.get(i)));
            }
            sb.append("\n");
        }
        sb.append("╚══════════════════════════════════════════════════════════════╝");

        idOperationsArea.setText(sb.toString());
    }

    private void showOddIds() {
        List<Integer> ids = getAllIds();

        if (ids.isEmpty()) {
            idOperationsArea.setText("Нет викингов в базе данных!");
            return;
        }

        List<Integer> oddIds = ids.stream()
                .filter(id -> id % 2 != 0)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                     НЕЧЕТНЫЕ ID ВИКИНГОВ                      ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Всего нечетных ID: %-43d ║\n", oddIds.size()));

        if (!oddIds.isEmpty()) {
            sb.append("╠══════════════════════════════════════════════════════════════╣\n");
            sb.append("║  ID: ");
            for (int i = 0; i < oddIds.size(); i++) {
                if (i > 0 && i % 15 == 0) sb.append("\n║       ");
                sb.append(String.format("%3d ", oddIds.get(i)));
            }
            sb.append("\n");
        }
        sb.append("╚══════════════════════════════════════════════════════════════╝");

        idOperationsArea.setText(sb.toString());
    }

    private void showIdStats() {
        List<Integer> ids = getAllIds();

        if (ids.isEmpty()) {
            idOperationsArea.setText("Нет викингов в базе данных!");
            return;
        }

        int maxId = ids.stream().max(Integer::compareTo).orElse(0);
        int minId = ids.stream().min(Integer::compareTo).orElse(0);
        double average = ids.stream().mapToInt(Integer::intValue).average().orElse(0);
        long evenCount = ids.stream().filter(id -> id % 2 == 0).count();
        long oddCount = ids.size() - evenCount;

        String result = String.format(
                "╔══════════════════════════════════════════════════════════════╗\n" +
                        "║                   СТАТИСТИКА ПО ID ВИКИНГОВ                   ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣\n" +
                        "║  Всего ID:          %-40d ║\n" +
                        "║  Минимальный ID:    %-40d ║\n" +
                        "║  Максимальный ID:   %-40d ║\n" +
                        "║  Среднее значение:  %-40.2f ║\n" +
                        "║  Четных ID:         %-40d ║\n" +
                        "║  Нечетных ID:       %-40d ║\n" +
                        "╚══════════════════════════════════════════════════════════════╝",
                ids.size(), minId, maxId, average, evenCount, oddCount
        );

        idOperationsArea.setText(result);
    }

    private void loadData() {
        loadLegendaryVikings();
        loadRedheads(true);
        showAllIds();
    }

    private void loadLegendaryVikings() {
        List<IdVikingPair> vikingsWithIds = getVikingsWithIds().stream()
                .filter(pair -> pair.viking().equipment().stream()
                        .anyMatch(e -> "Legendary".equals(e.quality())))
                .toList();

        DefaultTableModel model = (DefaultTableModel) legendaryTable.getModel();
        model.setRowCount(0);

        vikingsWithIds.forEach(pair -> {
            Viking v = pair.viking();
            String legendaryItems = v.equipment().stream()
                    .filter(e -> "Legendary".equals(e.quality()))
                    .map(e -> e.name())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            model.addRow(new Object[]{
                    pair.id(), v.name(), v.age(), v.heightCm(),
                    v.hairColor(), v.beardStyle(), legendaryItems
            });
        });

        if (vikingsWithIds.isEmpty()) {
            model.addRow(new Object[]{"Нет данных", "", "", "", "", "", ""});
        }
    }

    private void loadRedheads(boolean ascending) {
        List<IdVikingPair> redheadsWithIds = getVikingsWithIds().stream()
                .filter(pair -> pair.viking().hairColor().name().equalsIgnoreCase("Red"))
                .toList();

        if (ascending) {
            redheadsWithIds = redheadsWithIds.stream()
                    .sorted((p1, p2) -> p1.viking().age() - p2.viking().age())
                    .toList();
        } else {
            redheadsWithIds = redheadsWithIds.stream()
                    .sorted((p1, p2) -> p2.viking().age() - p1.viking().age())
                    .toList();
        }

        DefaultTableModel model = (DefaultTableModel) redheadsTable.getModel();
        model.setRowCount(0);

        redheadsWithIds.forEach(pair -> {
            Viking v = pair.viking();
            String equipmentStr = v.equipment().stream()
                    .map(e -> e.name() + " (" + e.quality() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            model.addRow(new Object[]{
                    pair.id(), v.name(), v.age(), v.heightCm(),
                    v.beardStyle(), equipmentStr
            });
        });

        if (redheadsWithIds.isEmpty()) {
            model.addRow(new Object[]{"Нет рыжеволосых викингов", "", "", "", "", ""});
        }
    }
}