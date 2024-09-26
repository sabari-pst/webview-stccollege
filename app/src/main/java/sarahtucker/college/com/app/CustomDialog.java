package sarahtucker.college.com.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialog  extends Dialog  implements  View.OnClickListener
{

    public Activity c;
    public Dialog d;
    public Button yes, no;

    public CustomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_alert);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_yes:
                constantvalues.bForCloseApplication=true;
                break;
            case R.id.btn_no:
                constantvalues.bForCloseApplication=false;
                break;
            default:
                break;
        }
        dismiss();

    }
}
