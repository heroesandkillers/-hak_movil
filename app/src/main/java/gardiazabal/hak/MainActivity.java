package gardiazabal.hak;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    //https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
    private String android_id;

    SharedPreferences prefs;
    WebView webView;
    TelephonyManager telephonyManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        Context mContext = getApplicationContext();
        prefs = mContext.getSharedPreferences("hakPrefs", Context.MODE_PRIVATE);

        webView = new WebView(this);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); //localStorage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        //LOAD WEBVIEW
        webView.loadUrl("javascript: var Gusuario = '" + email() + "'"
                + ", Gpass = '" + android_id + "'"
                + ", Gperfil = '" + prefs.getString("perfil", "{}") + "'"
                + ", Gliga = '" + prefs.getString("liga", "{}") + "'"
                + ", Gcalendario = '" + prefs.getString("calendario", "{}") + "'"
                + ", GcalendarioDivision = '" + prefs.getString("calendarioDivision", "{}") + "'"
                + ", Gequipo = '" + prefs.getString("equipo", "{}") + "'"
                + ", GequipoAcademia = '" + prefs.getString("equipoAcademia", "{}") + "'"
                + ";");

        webView.loadUrl("file:///android_asset/pages/index_mob.html");
        setContentView(webView);
    }

    public class WebAppInterface {

        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }
        SharedPreferences.Editor editor = prefs.edit();

        @JavascriptInterface
        public void setPref(String name, String data) {
            editor.putString(name, data);
            editor.commit();
        }
    }

    public String email() {
        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.google");
        if (accounts[0].name != null) {
            return accounts[0].name;
        }
        return "";
    }

}
