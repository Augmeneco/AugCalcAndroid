package tk.augmeneco.augcalcandroid;

import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager2.widget.ViewPager2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //private ArrayList<Pair<String, Rule>> exprTokens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        char sep = ((DecimalFormat)DecimalFormat.getInstance(Locale.getDefault())).getDecimalFormatSymbols().getDecimalSeparator();
//        findViewById(R.id.buttonDot).setTag(String.valueOf(sep));
//        ((Button)findViewById(R.id.buttonDot)).setText(String.valueOf(sep));
    }
    
    public void onEnterButtonClick(View view) {
        ((TextView)findViewById(R.id.textExpression)).append((String)(view.getTag()));

        scrollToBottom();
    }

    public void onCalcButtonClick(View view) {
        TextView textExpr = findViewById(R.id.textExpression);
        String expr = textExpr.getText().toString();

        MathAST ast;
        try {
             ast = Parser.parse(expr);
        } catch (Exception e) {
//            String errorMsg = e.getClass().getName()+(e.getLocalizedMessage() == null ? "" : "\n"+e.getLocalizedMessage());
//            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            textExpr.setText("parse error");
            return;
        }

        double result;
        try {
            result = Calculator.calculate(ast);
        } catch (Exception e) {
            e.printStackTrace();
            textExpr.setText("calculate error");
            return;
        }

        String resultStr = String.valueOf(result)
                                 .replaceAll("\\.0$", "")
                                 .replaceAll(",0$", "")
                                 .toLowerCase();
        textExpr.setText(resultStr);
    }

    public void onClearButtonClick(View view) {
        ((TextView)findViewById(R.id.textExpression)).setText("");
    }

    public void onRemoveButtonClick(View view) {
        String str = ((TextView)findViewById(R.id.textExpression)).getText().toString();
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
            ((TextView)findViewById(R.id.textExpression)).setText(str);
        }
    }

    private void scrollToBottom() {
        final ScrollView scrollview = findViewById(R.id.scroll);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));
    }
}