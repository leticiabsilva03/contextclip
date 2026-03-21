package dev.contextclip.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SystemTrayManager {

    private final HistoryPopup historyPopup;

    public SystemTrayManager(HistoryPopup historyPopup) {
        this.historyPopup = historyPopup;
    }

    public void init() throws AWTException {
        if (!SystemTray.isSupported()) {
            System.err.println("System Tray não suportado neste ambiente.");
            return;
        }

        var image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 16, 16);
        g.dispose();

        var popup = new PopupMenu();
        var itemHistorico = new MenuItem("Histórico");
        itemHistorico.addActionListener(e -> historyPopup.show());
        var itemSair = new MenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        popup.add(itemHistorico);
        popup.addSeparator();
        popup.add(itemSair);

        var trayIcon = new TrayIcon(image, "ContextClip", popup);
        trayIcon.setImageAutoSize(true);
        SystemTray.getSystemTray().add(trayIcon);
    }
}
