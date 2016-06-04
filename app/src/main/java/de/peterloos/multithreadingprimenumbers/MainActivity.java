package de.peterloos.multithreadingprimenumbers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener, UpdateUIHandlerListener, Runnable {

    private long minimum;
    private long maximum;
    private long total;

    private final int MaxThreads = 4;
    private Thread t;

    private int lastResult;

    private EditText editTextThreadCtrls[];
    private EditText editTextFrom;
    private EditText editTextTo;
    private EditText textViewTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setContentView(R.layout.activity_main);

    // setup event handlers
        Button buttonCalc = (Button) this.findViewById(R.id.buttonCalc);
        buttonCalc.setOnClickListener(this);

    // retrieve UI references
        this.editTextThreadCtrls = new EditText[MaxThreads];

        EditText editTextThread01 =
                (EditText) this.findViewById(R.id.editTextThread01);
        EditText editTextThread02 =
                (EditText) this.findViewById(R.id.editTextThread02);
        EditText editTextThread03 =
                (EditText) this.findViewById(R.id.editTextThread03);
        EditText editTextThread04 =
                (EditText) this.findViewById(R.id.editTextThread04);

        this.editTextThreadCtrls[0] = editTextThread01;
        this.editTextThreadCtrls[1] = editTextThread02;
        this.editTextThreadCtrls[2] = editTextThread03;
        this.editTextThreadCtrls[3] = editTextThread04;

        this.editTextFrom = (EditText) this.findViewById(R.id.editTextFrom);
        this.editTextTo = (EditText) this.findViewById(R.id.editTextTo);
        this.textViewTotal = (EditText) this.findViewById(R.id.textViewTotal);

        // miscellaneous initializations
        this.t = null;
        this.minimum = 2;
        this.maximum = 1000;
        this.lastResult = 0;
    }

    @Override
    /* public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    // @Override
    public void onClick(View v) {

        // current prime numbers search is still active
        if (this.t != null)
            return;

        // retrieve lower and upper limit for prime numbers calculation
        Editable editableFrom = this.editTextFrom.getText();
        Editable editableTo = this.editTextTo.getText();
        this.minimum = Integer.parseInt(editableFrom.toString());
        this.maximum = Integer.parseInt(editableTo.toString());

        // clear UI
        this.textViewTotal.setText ("");
        for (int i = 0; i < MaxThreads; i++)
            this.editTextThreadCtrls[i].setText("");

        // asynchronous invocation
        this.t = new Thread (this);
        this.t.start();
    }

    public void run ()
    {
        // create (single) calculator object
        PrimeNumberCalculator calculator = new PrimeNumberCalculator();
        calculator.setMinimum(this.minimum);
        calculator.setMaximum(this.maximum);

        // register (single) event listener
        calculator.setUpdateUIHandlerListener (this);

        this.total = 0;
        this.lastResult = 0;

        // create and launch threads
        Thread[] workers = new Thread [MaxThreads];
        for (int i = 0; i < MaxThreads; i++)
        {
            workers[i] = new Thread(calculator);
            workers[i].start();
        }

        for (int i = 0; i < MaxThreads; i++)
        {
            try
            {
                workers[i].join();
            }
            catch (InterruptedException e) {}
        }

        final String s = String.format(Locale.US, "Total: %d ", this.total);
        Log.i("PeLo", s.toString());

        // copy message into text box
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // editTextMessages.append(response);
                // editTextMessages.append("\n");
                textViewTotal.setText(s);
            }
        });

        // enable UI
        this.t = null;
    }

    @Override
    public void UpdateUIHandler(long tid, long found) {

        final int currentIndex;

        final String s = String.format("Found %d [TID: %d] ", found, tid);
        Log.i("PeLo", s.toString());

        synchronized (this)
        {
            this.total += found;
            currentIndex = this.lastResult;
            this.lastResult ++;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextThreadCtrls[currentIndex].setText(s);
            }
        });
    }


}
