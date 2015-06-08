package arnold.example.com.watchface.activities;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import arnold.example.com.watchface.R;
import arnold.example.com.watchface.ui.OptionListItem;
import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends ActionBarActivity implements OptionListItem.OnColorClickListener {

    private OptionListItem accentColor;
    private OptionListItem backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accentColor = (OptionListItem) findViewById(R.id.accent_color);
        backgroundColor = (OptionListItem) findViewById(R.id.background_color);

        accentColor.setPrimaryText(getString(R.string.accent_color));
        accentColor.setOnColorClickListener(this);
        accentColor.setPreviewColor(Color.RED);

        backgroundColor.setPrimaryText(getString(R.string.background_color));
        backgroundColor.setOnColorClickListener(this);
        backgroundColor.setPreviewColor(Color.BLACK);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        TextView view = (TextView) findViewById(R.id.test);
//
//
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/digital_7.ttf");
//        view.setTypeface(typeface);
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
    public void onColorClicked(View view) {
        switch (view.getId()) {
            case R.id.accent_color:
                startColorPicker(accentColor);
                break;
            case R.id.background_color:
                startColorPicker(backgroundColor);
                break;
        }
    }

    private void startColorPicker(final OptionListItem item) {
        new AmbilWarnaDialog(this, item.getPreviewColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                item.setPreviewColor(i);
            }
        }).show();
    }
}
