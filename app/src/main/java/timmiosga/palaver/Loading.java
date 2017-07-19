package timmiosga.palaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Thread sp = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    int wait = 0;
                    while (wait < 5000)
                    {
                        sleep(100);
                        wait += 100;
                    }
                } catch (InterruptedException e)
                {

                } finally
                {
                    finish();

                }
            }
        };
        sp.start();
    }
}
