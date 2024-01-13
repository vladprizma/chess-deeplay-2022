package io.deeplay.client;

import io.deeplay.client.nettyClient.ChessNettyClient;

public class Main {
    public static void main(String[] args) {
        new ChessNettyClient("localhost", 8189).start();
    }
}