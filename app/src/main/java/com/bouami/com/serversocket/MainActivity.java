package com.bouami.com.serversocket;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements DataDisplay {
    private final int REQUEST_PERMISSION_STATE = 1;
    private static TextView serverMessage;
    private WifiManager wifiManager;
    private Handler mUpdateHandler;
    DataDisplay m_dataDisplay;
    private int port = 60123;
//    private String host = "127.0.0.1";
//    private String host = "192.168.1.60";
    private String host= null;

    public static TextView getZoneAffichage() {
        return serverMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverMessage = (TextView) findViewById(R.id.message);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        String ipAddressString = null;
        byte[] ipByteArray = BigInteger.valueOf(wifiManager.getConnectionInfo().getIpAddress()).toByteArray();
        try {
           ipAddressString = InetAddress.getByAddress(new byte[] { ipByteArray[3], ipByteArray[2], ipByteArray[1], ipByteArray[0] }).getHostAddress();
//            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
//            ipAddressString = InetAddress.getByAddress(InetAddress.getLocalHost().getAddress()).getHostAddress();

        } catch (UnknownHostException e) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
//        ((TextView) findViewById(R.id.adresseip)).setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        ((TextView) findViewById(R.id.adresseip)).setText(ipAddressString);
//        ((TextView) findViewById(R.id.port)).setText(""+port);
        wifiManager= (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_STATE);
        };
        mUpdateHandler = new Handler(){

            @Override
            public void handleMessage(Message status) {
                Display(status.obj.toString());
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STATE: {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                return;
            }
        }
    }


    public void connect(View view) {
        MyServer server = new MyServer();
        server.setEventListner(this);
        server.startListner();
    }

    public void Display(String message) {
        serverMessage.setText(message);
    }

    public void TesterPort(View view){
        for(int port = 1; port <= 65535; port++){
            try {
                ServerSocket sSocket = new ServerSocket(port);
            } catch (IOException e) {
//                System.err.println("Le port " + port + " est déjà utilisé ! ");
                Log.d("SOCKET","Le port " + port + " est déjà utilisé ! ");
            }
        }
    }

    public void LancerServeur(View view){
        host = ((TextView) findViewById(R.id.adresseip)).getText().toString();
        TimeServer server = new TimeServer(host,mUpdateHandler);
        ((TextView) findViewById(R.id.port)).setText(""+server.getLocalPort());
//        port = parseInt(((TextView) findViewById(R.id.port)).getText().toString());
        server.open();
    }
}
