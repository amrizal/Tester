package com.amrizal.example.timertester;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int TIMER_1 = 1001;
    private static final int TIMER_2 = 1002;
    private static final long TIMER_1_ELAPSE = 1000;
    private static final long TIMER_2_ELAPSE = 3000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TIMER_1_VALUE = "timer_1_value";
    private static final String TIMER_2_VALUE = "timer_2_value";

    final Handler timer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int counter;
            Bundle data = msg.getData();
            switch (msg.what){
                case TIMER_1:
                    counter = data.getInt(TIMER_1_VALUE, 0);
                    onTimer1(counter);
                    break;
                case TIMER_2:
                    counter = data.getInt(TIMER_2_VALUE, 0);
                    onTimer2(counter);
                    break;
            }

            return false;
        }
    });
    private TextView display1;
    private TextView display2;

    private void onTimer1(int counter) {
        display1.setText(String.valueOf(counter));
        counter++;
        Message message = timer.obtainMessage(TIMER_1);
        Bundle data = message.getData();
        data.putInt(TIMER_1_VALUE, counter);
        timer.sendMessageDelayed(message, TIMER_1_ELAPSE);
    }

    private void onTimer2(int counter) {
        display2.setText(String.valueOf(counter));
        counter++;
        Message message = timer.obtainMessage(TIMER_2);
        Bundle data = message.getData();
        data.putInt(TIMER_2_VALUE, counter);
        timer.sendMessageDelayed(message, TIMER_2_ELAPSE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        onTimer1(0);
        onTimer2(0);
    }

    private void initView() {
        display1 = (TextView)findViewById(R.id.counter_1);
        display2 = (TextView)findViewById(R.id.counter_2);
    }
}
