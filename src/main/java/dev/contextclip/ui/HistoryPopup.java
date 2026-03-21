package dev.contextclip.ui;

import dev.contextclip.repository.ClipRepository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HistoryPopup {

    private final ClipRepository repository;

    public HistoryPopup(ClipRepository repository) {
        this.repository = repository;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame();
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.setSize(500, 400);

            var searchField = new JTextField();
            var listModel   = new DefaultListModel<String>();
            var resultList  = new JList<>(listModel);

            resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
                            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(new StringSelection(selected), null);
                            frame.dispose();
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        frame.dispose();
                    }
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            frame.add(searchField, BorderLayout.NORTH);
            frame.add(new JScrollPane(resultList), BorderLayout.CENTER);
            frame.setVisible(true);
            searchField.requestFocus();
        });
    }

    private void loadHistory(DefaultListModel<String> model, String query) {
        try {
            model.clear();
            var entries = repository.findRecent(50);
            for (var entry : entries) {
                if (query.isBlank() || entry.text().toLowerCase().contains(query.toLowerCase())) {
                    model.addElement(entry.text());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }
}
