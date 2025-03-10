package com.example.barretina_mobil.Utils;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class UtilsWS extends WebSocketClient {

    public static UtilsWS sharedInstance = null;
    private Consumer<String> onMessageCallBack = null;
    private static String location = "";
    private static AtomicBoolean exitRequested = new AtomicBoolean(false); // Thread safe
    private static AtomicBoolean isConnected = new AtomicBoolean(false);

    private UtilsWS (String location, Draft draft) throws URISyntaxException {
        super (new URI(location), draft);
    }

    public static void init (String location) {
        UtilsWS.location = location;
        if (sharedInstance == null) {
            try {
                sharedInstance = new UtilsWS(UtilsWS.location, (Draft) new Draft_6455());
                sharedInstance.connect();
                isConnected.set(true);
            } catch (URISyntaxException e) { 
                System.out.println("WS Error, " + location + " is not a valid URI");
                e.printStackTrace(); 
            }
        }
    }

    public static UtilsWS getSharedInstance () {
        if (UtilsWS.location.isEmpty()) {
            System.out.println("WS location is not set");
            return null;
        }
        if (sharedInstance == null) {
            init(UtilsWS.location);
        }
        return sharedInstance;
    }

    public void setOnMessage (Consumer<String> callBack) {
        Log.d("setOnMessage", "callBack: " + (callBack != null));
        this.onMessageCallBack = callBack;
    }

    @Override
    public void onMessage(String message) {
        Log.d("onMessage", "message: " + message);
        Log.d("onMessage", "onMessageCallBack: " + (onMessageCallBack != null));
        if (onMessageCallBack != null) {
            onMessageCallBack.accept(message);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WS connected with: " + getURI() + ", to " + getRemoteSocketAddress());
        isConnected.set(true);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WS closed connection from: " + getURI());

        if (remote) {
            reconnect();
        }
    }

    @Override
    public void onError(Exception e) {
        System.out.println("WS connection error: " + e.getMessage());
        e.printStackTrace();
        if (e.getMessage().contains("Connection refused") || e.getMessage().contains("Connection reset")) {
            reconnect();
        }
    }

    public void safeSend(String text) {
        try {
            sharedInstance.send(text);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WS Error sending message");
        }
    }

    public void reconnect () {
        if (exitRequested.get()) { return; }
        forceExit();
    
        System.out.println("WS reconnecting to: " + UtilsWS.location);
        isConnected.set(false);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("WD Error, waiting");
            Thread.currentThread().interrupt();
        }
    
        if (exitRequested.get()) { return; }
        
        Consumer<String> oldCallBack = this.onMessageCallBack;
        if (sharedInstance != null) {
            sharedInstance.close();
        }
        sharedInstance = null;
        getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.setOnMessage(oldCallBack);
            isConnected.set(true);
        }
    }
    
    public void forceExit () {
        System.out.println("WS Closing ...");
        isConnected.set(false);
        sharedInstance.close();
        sharedInstance = null;
    }

    public boolean isConnected() {
        return UtilsWS.isConnected.get();
    }
}