package com.wcn.jdk.example.io.mynetty;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

public class MyEventLoop {
    private Selector selector;

    MyEventLoop() throws IOException {
        selector = Selector.open();
    }

    void register(SelectableChannel channel, int ops, Object atr) throws ClosedChannelException {
        channel.register(selector, ops, atr);
    }
}
