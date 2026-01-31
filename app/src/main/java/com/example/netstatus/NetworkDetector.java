package com.example.netstatus;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class NetworkDetector extends AppCompatActivity {
    private AlertDialog dialog;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;

    void resumeApp(){
        hideError();
    }
    void hideAction(){

    }
    boolean checkInternet(){
        boolean isWifiConn = false;
        boolean isMobileConn = false;

        Network network = cm.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps != null) {
                isWifiConn = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                isMobileConn = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }
        return isWifiConn || isMobileConn;
    }

    void displayError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null){
                    AlertDialog.Builder alert = new AlertDialog.Builder(NetworkDetector.this);
                    alert.setTitle(R.string.error_title);
                    alert.setMessage(R.string.error_description);
                    alert.setCancelable(false);
                    alert.setIcon(android.R.drawable.ic_delete);
                    dialog = alert.create();
                    dialog.show();
                }else {
                    dialog.show();
                }
            }
        });
    }

    void hideError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null){
                    hideAction();
                    dialog.dismiss();
                }
            }
        });
    }

    void connect(){
        cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
        // notifié uniquement des réseaux qui déclarent avoir une capacité Internet

        //On crée un objet de type NetworkCallback pour écouter les changements de réseau
        //on fait cela pour pouvoir déconnecter les evenements lors de la destruction de l'activity
        networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                //se déclanche a l'ouverture de l'app
                resumeApp();
            }

            @Override
            public void onLost(Network network) {
                if (!checkInternet()){ //onLost se déclanche aussi lorsque le wifi s'active par ex
                    displayError();
                }
            }
        };

        //On enregistre notre NetworkCallback auprès du ConnectivityManager
        //pour recevoir les événements liés au réseau
        cm.registerNetworkCallback(request,networkCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCallback != null) {
            cm.unregisterNetworkCallback(networkCallback);
            //deconnexion des events
        }
    }
}
