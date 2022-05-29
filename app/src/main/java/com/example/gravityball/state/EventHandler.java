package com.example.gravityball.state;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public interface EventHandler {

    void connected(Connection c);
    void received (Connection c, Object object) ;
    void disconnected (Connection c);
    void action(Object action);
    void prepare();
}
