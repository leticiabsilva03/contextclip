package dev.contextclip.capture;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyAdapter;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import dev.contextclip.ui.HistoryPopup;

import java.util.logging.Level;
import java.util.logging.Logger;


public class HotkeyManager {

    private final HistoryPopup historyPopup;

    public HotkeyManager(HistoryPopup historyPopup) {
        this.historyPopup = historyPopup;
    }

    public void register() throws NativeHookException {
        var logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        GlobalScreen.registerNativeHook();

        GlobalScreen.addNativeKeyListener(new NativeKeyAdapter() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                boolean ctrl  = (e.getModifiers() & NativeInputEvent.CTRL_MASK)  != 0;
                boolean shift = (e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;
                boolean v     = e.getKeyCode() == NativeKeyEvent.VC_V;

                if (ctrl && shift && v) {
                    historyPopup.show();
                }
            }
        });
    }
}
