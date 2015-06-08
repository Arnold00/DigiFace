package arnold.example.com.watchface.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import arnold.example.com.watchface.R;

/**
 * Created by arnold on 08.06.2015.
 */
public class OptionListItem extends RelativeLayout implements View.OnClickListener {

    private TextView primaryText;
    private View colorPreview;
    private OnColorClickListener onColorClickListener;
    private int previewColor;

    public OptionListItem(Context context) {
        super(context);
        inflateview();
    }

    public OptionListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateview();
    }

    public OptionListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateview();
    }

    public int getPreviewColor() {
        return previewColor;
    }

    public void setOnColorClickListener(OnColorClickListener onColorClickListener) {
        this.onColorClickListener = onColorClickListener;
    }

    private void inflateview() {
        inflate(getContext(), R.layout.option_item, this);
        primaryText = (TextView) findViewById(R.id.primary_text);

        colorPreview = findViewById(R.id.color_preview);
        colorPreview.setOnClickListener(this);
    }

    public void setPrimaryText(String text) {
        if (!TextUtils.isEmpty(text)) {
            primaryText.setText(text);
        }
    }

    public void setPreviewColor(int color) {
        colorPreview.setBackgroundColor(color);
        previewColor = color;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.color_preview && onColorClickListener != null) {
            onColorClickListener.onColorClicked(this);
        }
    }

    public interface OnColorClickListener {
        void onColorClicked(View view);
    }
}
