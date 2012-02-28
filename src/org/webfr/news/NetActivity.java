package org.webfr.news;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;

abstract public class NetActivity extends Activity {
	public boolean testReseau() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null)
			return false;
		State networkState = networkInfo.getState();
		return networkState == State.CONNECTED;
	}
	
	public void dlgSansReseau() {
		Builder dlgBuilder = new AlertDialog.Builder(this);
		dlgBuilder.setTitle("Pas de réseau");
		dlgBuilder.setMessage("Un accès à Internet est nécessaire");
		dlgBuilder.setNeutralButton("ok",null);
		dlgBuilder.show();
	}
}
