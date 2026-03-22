package dev.contextclip.ui;

import dev.contextclip.domain.ClipEntry;
import dev.contextclip.repository.ClipRepository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HistoryPopup {

    private final ClipRepository repository;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault());

    public HistoryPopup(ClipRepository repository) {
        this.repository = repository;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame();
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.setSize(520, 440);

            var searchField = new JTextField();
            searchField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        frame.dispose();
                    }
                }
            });
            searchField.putClientProperty("JTextField.placeholderText", "Buscar no histórico...");
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            searchField.setBackground(new Color(38, 38, 45));
            searchField.setForeground(new Color(220, 220, 225));
            searchField.setCaretColor(new Color(220, 220, 225));
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 85)),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            var listModel  = new DefaultListModel<ClipEntry>();
            var resultList = new JList<>(listModel);
            resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultList.setCellRenderer(new ClipEntryRenderer());
            resultList.setBackground(new Color(28, 28, 32));
            resultList.setSelectionBackground(new Color(50, 50, 60));

            var scrollPane = new JScrollPane(resultList);
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            loadHistory(listModel, "");

            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e)  { loadHistory(listModel, searchField.getText()); }
                public void removeUpdate(DocumentEvent e)  { loadHistory(listModel, searchField.getText()); }
                public void changedUpdate(DocumentEvent e) { loadHistory(listModel, searchField.getText()); }
            });

            resultList.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        var selected = resultList.getSelectedValue();
                        if (selected != null) {
                            var cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                            cb.setContents(new StringSelection(selected.text()), null);
                            frame.dispose();
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        frame.dispose();
                    }
                }
            });

            var searchLabel = new JLabel("  Buscar no histórico");
            searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            searchLabel.setForeground(new Color(90, 90, 110));
            searchLabel.setBorder(BorderFactory.createEmptyBorder(8, 16, 2, 16));
            searchLabel.setBackground(new Color(38, 38, 45));
            searchLabel.setOpaque(true);

            var searchPanel = new JPanel(new BorderLayout());
            searchPanel.setBackground(new Color(38, 38, 45));
            searchPanel.add(searchLabel, BorderLayout.NORTH);
            searchPanel.add(searchField, BorderLayout.CENTER);

            frame.setBackground(new Color(28, 28, 32));
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            frame.add(searchPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setVisible(true);
            searchField.requestFocus();
        });
    }

    private void loadHistory(DefaultListModel<ClipEntry> model, String query) {
        try {
            model.clear();
            var entries = query.isBlank()
                    ? repository.findRecent(50)
                    : repository.search(query);
            for (var entry : entries) {
                model.addElement(entry);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    private class ClipEntryRenderer extends JPanel implements ListCellRenderer<ClipEntry> {
        private final JLabel textLabel;
        private final JLabel metaLabel;

        ClipEntryRenderer() {
            setLayout(new BorderLayout(0, 2));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);

            textLabel = new JLabel();
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textLabel.setForeground(new Color(220, 220, 225));

            metaLabel = new JLabel();
            metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            metaLabel.setForeground(new Color(120, 120, 135));

            add(textLabel, BorderLayout.NORTH);
            add(metaLabel, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ClipEntry> list, ClipEntry entry,
                int index, boolean isSelected, boolean cellHasFocus) {

            var text = entry.text().length() > 80
                    ? entry.text().substring(0, 80) + "..."
                    : entry.text();
            textLabel.setText(text.replace("\n", " "));

            var source = entry.source() != null ? entry.source() : "unknown";
            var date   = FMT.format(entry.copiedAt());
            metaLabel.setText(source + " · " + date);

            setBackground(isSelected
                    ? new Color(50, 50, 65)
                    : new Color(28, 28, 32));

            return this;
        }
    }
}