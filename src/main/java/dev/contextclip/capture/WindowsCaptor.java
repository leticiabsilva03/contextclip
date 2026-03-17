package dev.contextclip.capture;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import dev.contextclip.domain.WindowContext;

public class WindowsCaptor implements ContextCaptor {
    @Override
    public WindowContext capture() {
        try {
            var hwnd   = User32.INSTANCE.GetForegroundWindow();
            var buffer = new char[512];
            User32.INSTANCE.GetWindowText(hwnd, buffer, buffer.length);
            var title  = Native.toString(buffer);
            return new WindowContext(title, "unknown");
        } catch (Exception e) {
            return new WindowContext("unknown", "unknown");
        }
    }
}
