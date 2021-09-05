package com.hermes.listener;


public interface ObservableInput {
    void addListener(InputListener listener, boolean isPersistent);
    void removeListener(InputListener listener);
    void notifyListeners(int keycode);
}
