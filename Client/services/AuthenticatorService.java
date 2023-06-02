package com.syncadapters.czar.exchange.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.syncadapters.czar.exchange.authenticator.AccountAuthenticator;

public class AuthenticatorService extends Service {

    private AccountAuthenticator authenticator;

    public void onCreate(){
        super.onCreate();

        authenticator = new AccountAuthenticator(AuthenticatorService.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }


}
