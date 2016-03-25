package com.sotayamashita.growthbeatstarterproject;

import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.growthbeat.Growthbeat;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.intenthandler.IntentHandler;
import com.growthbeat.intenthandler.NoopIntentHandler;
import com.growthbeat.intenthandler.UrlIntentHandler;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.model.CustomIntent;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ****************************************************************************
        // Uncomment and fill in with your Growthbeat application id, credentials and senderID
        // String growthbeatApplicationID = "Your application ID";
        // String growthbeatCredentialID  = "Your Growthbeat Credential ID";
        // String senderID = "Your Sender ID"; // a.k.a Project number
        // ****************************************************************************

        Growthbeat.getInstance().initialize(this, growthbeatApplicationID, growthbeatCredentialID);
        GrowthLink.getInstance().initialize(this, growthbeatApplicationID, growthbeatCredentialID);
        GrowthPush.getInstance().requestRegistrationId(senderID, BuildConfig.DEBUG ? Environment.development : Environment.production);
        GrowthLink.getInstance().handleOpenUrl(getIntent().getData());

        List<IntentHandler> intentHandlers = new ArrayList<IntentHandler>();
        intentHandlers.add(new UrlIntentHandler(GrowthbeatCore.getInstance().getContext()));
        intentHandlers.add(new NoopIntentHandler());
        intentHandlers.add(new IntentHandler() {
            public boolean handle(com.growthbeat.model.Intent intent) {
                if (intent.getType() != com.growthbeat.model.Intent.Type.custom)
                    return false;
                Map<String, String> extra = ((CustomIntent) intent).getExtra();
                // TODO ここにアプリ内の画面を開く処理を実装します。
                Log.d("Growth Link", "extra: " + extra);
                return true;
            }
        });
        GrowthbeatCore.getInstance().setIntentHandlers(intentHandlers);

        GrowthPush.getInstance().setTag("tag1", "TAG");
        GrowthPush.getInstance().trackEvent("event1");

        findViewById(R.id.random_tag_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthAnalytics.getInstance().setRandom();
            }
        });

        findViewById(R.id.development_tag_check_box).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                GrowthAnalytics.getInstance().setDevelopment(checkBox.isChecked());
            }
        });

        findViewById(R.id.level_tag_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText editText = (EditText) findViewById(R.id.level_edit_text);
                    GrowthAnalytics.getInstance().setLevel(Integer.valueOf(editText.getText().toString()));
                } catch (NumberFormatException e) {
                    Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
                }
            }
        });

        findViewById(R.id.purchase_event_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText priceEditText = (EditText) findViewById(R.id.price_edit_text);
                    EditText productEditText = (EditText) findViewById(R.id.product_edit_text);
                    GrowthAnalytics.getInstance().purchase(Integer.valueOf(priceEditText.getText().toString()), "item",
                            productEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Log.w("Grwothbeat Sample", "Input value error :" + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Growthbeat.getInstance().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        Growthbeat.getInstance().stop();
    }
}
