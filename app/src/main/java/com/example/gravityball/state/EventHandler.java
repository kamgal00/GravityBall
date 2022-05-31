package com.example.gravityball.state;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public interface EventHandler {

    default void connected(Connection c) {}
    default void received (Connection c, Object object) {}
    default void disconnected (Connection c) {};
    default void prepare() {}
}
