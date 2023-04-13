package com.mx.sireco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrintReceiptCustom extends AppCompatActivity implements View.OnClickListener, IOCallBack {

    private LinearLayout linearlayoutdevices;
    //private ProgressBar progressBarSearchStatus;

    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;
    String ide = "",nom = "",mts = "",vnt = "",total = "";
    Button btnSearch, btnReturn, btnPrint;
    PrintReceiptCustom mActivity;

    ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    BTPrinting mBt = new BTPrinting();

    private static String TAG = "SearchBTActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_receipt_custom);

        mActivity = this;

        linearlayoutdevices = (LinearLayout) findViewById(R.id.linearLayoutDevices);

        btnSearch = (Button) findViewById(R.id.btnFindPrinter);
        btnReturn = (Button) findViewById(R.id.btnReturn);
        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnSearch.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnSearch.setEnabled(true);
        btnReturn.setEnabled(true);
        btnPrint.setEnabled(false);

        mPos.Set(mBt);
        mBt.SetCallBack(this);

        initBroadcast();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFindPrinter: {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    finish();
                    break;
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (!adapter.isEnabled()) {

                        if (adapter.enable()) {
                            while (!adapter.isEnabled())
                                ;
                            Log.v(TAG, "Enable BluetoothAdapter");
                        } else {
                            finish();
                            break;
                        }
                    }

                    adapter.cancelDiscovery();
                    linearlayoutdevices.removeAllViews();
                    adapter.startDiscovery();
                }

                break;
            }

            case R.id.btnPrint:
                ide = ((EditText) findViewById(R.id.identificador)).getText().toString();
                nom = ((EditText) findViewById(R.id.nombre)).getText().toString();
                mts = ((EditText) findViewById(R.id.mts)).getText().toString();
                vnt = ((EditText) findViewById(R.id.vnt)).getText().toString();
                total = ((EditText) findViewById(R.id.total)).getText().toString();
                btnPrint.setEnabled(false);

                es.submit(new TaskPrint(mPos));
                btnPrint.setEnabled(true);
                break;

            case R.id.btnReturn:
                es.submit(new TaskClose(mBt));
                startActivity(new Intent(this, HomeActivity.class));
                break;
        }
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;
                    final String address = device.getAddress();
                    @SuppressLint("MissingPermission") String name = device.getName();
                    if (name == null)
                        name = "BT";
                    else if (name.equals(address))
                        name = "BT";
                    Button button = new Button(context);
                    button.setText(name + ": " + address);

                    for (int i = 0; i < linearlayoutdevices.getChildCount(); ++i) {
                        Button btn = (Button) linearlayoutdevices.getChildAt(i);
                        if (btn.getText().equals(button.getText())) {
                            return;
                        }
                    }

                    button.setGravity(android.view.Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            Toast.makeText(mActivity, "Connecting...", Toast.LENGTH_SHORT).show();
                            btnSearch.setEnabled(false);
                            linearlayoutdevices.setEnabled(false);
                            for (int i = 0; i < linearlayoutdevices.getChildCount(); ++i) {
                                Button btn = (Button) linearlayoutdevices.getChildAt(i);
                                btn.setEnabled(false);
                            }
                            btnReturn.setEnabled(true);
                            btnPrint.setEnabled(false);
                            es.submit(new TaskOpen(mBt, address, mActivity));
                        }
                    });
                    button.getBackground().setAlpha(100);
                    linearlayoutdevices.addView(button);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    //progressBarSearchStatus.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    //progressBarSearchStatus.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void OnOpen() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                btnReturn.setEnabled(true);
                btnPrint.setEnabled(true);
                btnSearch.setEnabled(false);
                linearlayoutdevices.setEnabled(false);
                for (int i = 0; i < linearlayoutdevices.getChildCount(); ++i) {
                    Button btn = (Button) linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(false);
                }
                Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                btnReturn.setEnabled(true);
                btnPrint.setEnabled(false);
                btnSearch.setEnabled(true);
                linearlayoutdevices.setEnabled(true);
                for (int i = 0; i < linearlayoutdevices.getChildCount(); ++i) {
                    Button btn = (Button) linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
                Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                btnReturn.setEnabled(true);
                btnPrint.setEnabled(false);
                btnSearch.setEnabled(true);
                linearlayoutdevices.setEnabled(true);
                for (int i = 0; i < linearlayoutdevices.getChildCount(); ++i) {
                    Button btn = (Button) linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
            }
        });
    }

    public Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    public Bitmap getTestImage1(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for (int i = 0; i < 8; ++i) {
            for (int x = i; x < width; x += 8) {
                for (int y = i; y < height; y += 8) {
                    canvas.drawPoint(x, y, paint);
                }
            }
        }
        return bitmap;
    }

    public Bitmap getTestImage2(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for (int y = 0; y < height; y += 4) {
            for (int x = y % 32; x < width; x += 32) {
                canvas.drawRect(x, y, x + 4, y + 4, paint);
            }
        }
        return bitmap;
    }

    public class TaskOpen implements Runnable {
        BTPrinting bt = null;
        String address = null;
        Context context = null;

        public TaskOpen(BTPrinting bt, String address, Context context) {
            this.bt = bt;
            this.address = address;
            this.context = context;
        }

        @Override
        public void run() {
            bt.Open(address, context);
        }
    }

    static int dwWriteIndex = 1;


    public class TaskPrint implements Runnable {
        Pos pos = null;

        public int nPrintWidth = 384;
        public boolean bCutter = true;
        public boolean bDrawer = false;
        public boolean bBeeper = true;
        public int nPrintCount = 1;
        public int nCompressMethod = 0;
        public boolean bAutoPrint = false;
        public int nPrintContent = 1;
        public boolean bCheckReturn = false;

        public TaskPrint(Pos pos) {
            this.pos = pos;
        }

        @Override
        public void run() {

            PrintTicket(this.nPrintWidth,  this.nCompressMethod, this.bCheckReturn);
            final boolean bIsOpened = pos.GetIO().IsOpened();
            mActivity.btnPrint.setEnabled(true);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.btnPrint.setEnabled(true);
                }
            });

        }


        public boolean PrintTicket(int nPrintWidth, int nCompressMethod, boolean bCheckReturn) {
            boolean bPrintResult = false;

            byte[] status = new byte[1];
            if (!bCheckReturn || (bCheckReturn && pos.POS_QueryStatus(status, 3000, 2))) {

                  if (!pos.GetIO().IsOpened())
                        return false;



                        pos.POS_FeedLine();

                        Bitmap sirecologo = getImageFromAssetsFile("ticket.png");
                        pos.POS_PrintPicture(sirecologo, nPrintWidth, 1, nCompressMethod);
                        pos.POS_FeedLine();
                        pos.POS_FeedLine();
                        pos.POS_S_Align(0);
                        pos.POS_S_TextOut("Identificador: "+ide, 0, 0, 0, 0, 0x00);
                        pos.POS_FeedLine();
                        pos.POS_S_TextOut("Nombre: "+nom, 0, 0, 0, 0, 0x00);
                        pos.POS_FeedLine();
                        pos.POS_S_TextOut("Metros: "+mts, 0, 0, 0, 0, 0x00);
                        pos.POS_FeedLine();
                        pos.POS_S_TextOut("Venta: "+vnt, 0, 0, 0, 0, 0x00);
                        pos.POS_FeedLine();
                        pos.POS_S_TextOut("Total: $ "+total, 0, 0, 0, 0, 0x00);


                        pos.POS_FeedLine();





            }

            return bPrintResult;
        }
    }

    public class TaskClose implements Runnable {
        BTPrinting bt = null;

        public TaskClose(BTPrinting bt) {
            this.bt = bt;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            bt.Close();
        }

    }
}