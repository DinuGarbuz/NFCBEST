package com.example.nfcbest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Bar extends AppCompatActivity {

    // list of NFC technologies detected:
    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_bar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button pay = (Button) findViewById(R.id.pay_btn);
        Button menu = (Button) findViewById(R.id.menuBar_btn);
        Button berePlus = (Button) findViewById(R.id.berePlus_btn);
        Button bereMinus = (Button) findViewById(R.id.bereMinus_btn);
        Button shotPlus = (Button) findViewById(R.id.shotPlus_btn);
        Button shotMinus = (Button) findViewById(R.id.shotMinusBtn);
        Button cockMinus = (Button) findViewById(R.id.cocktailMinusBtn);
        Button cockPlus = (Button) findViewById(R.id.cocktailPlus_btn);
        TextView bereNr = findViewById(R.id.bereNr_txt);
        TextView shotNr = findViewById(R.id.shotNr_txt);
        TextView cockNr = findViewById(R.id.cocktailNr_txt);
        TextView sum = findViewById(R.id.suma_txt);
        TextView name = findViewById(R.id.nameBar_txt);
        TextView money = findViewById(R.id.moneyBar_txt);
        TextView tagID = findViewById(R.id.tagIDBar_txt);

        String TagID = tagID.getText().toString();



        berePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String BereNr = bereNr.getText().toString();
                int num1 = Integer.parseInt(BereNr);
                int sum = num1 + 1;
                String Sum =String.valueOf(sum);
                bereNr.setText(Sum);
                calculateSum(sum, Integer.parseInt(shotNr.getText().toString()),Integer.parseInt(cockNr.getText().toString()) );
            }
        });

        bereMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String BereNr = bereNr.getText().toString();
                int num1 = Integer.parseInt(BereNr);
                int sum = 0;
                if (num1 >0) {
                    sum = num1 - 1;
                }
                String Sum =String.valueOf(sum);
                bereNr.setText(Sum);
                calculateSum(sum, Integer.parseInt(shotNr.getText().toString()), Integer.parseInt(cockNr.getText().toString()));
            }
        });

        shotPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ShotNr = shotNr.getText().toString();
                int num1 = Integer.parseInt(ShotNr);
                int sum = num1 + 1;
                String Sum =String.valueOf(sum);
                shotNr.setText(Sum);
                calculateSum(Integer.parseInt(bereNr.getText().toString()), sum, Integer.parseInt(cockNr.getText().toString()));
            }
        });

        shotMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ShotNr = shotNr.getText().toString();
                int num1 = Integer.parseInt(ShotNr);
                int sum = 0;
                if (num1 >0) {
                     sum = num1 - 1;
                }
                String Sum =String.valueOf(sum);
                shotNr.setText(Sum);
                calculateSum(Integer.parseInt(bereNr.getText().toString()), sum, Integer.parseInt(cockNr.getText().toString()));
            }
        });

        cockPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String CockNr = cockNr.getText().toString();
                int num1 = Integer.parseInt(CockNr);
                int sum = num1 + 1;
                String Sum =String.valueOf(sum);
                cockNr.setText(Sum);
                calculateSum(Integer.parseInt(bereNr.getText().toString()),Integer.parseInt(shotNr.getText().toString()), sum );
            }
        });

        cockMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String CockNr = cockNr.getText().toString();
                int num1 = Integer.parseInt(CockNr);
                int sum = 0;
                if (num1 >0) {
                    sum = num1 - 1;
                }
                String Sum =String.valueOf(sum);
                cockNr.setText(Sum);
                calculateSum(Integer.parseInt(bereNr.getText().toString()),Integer.parseInt(shotNr.getText().toString()), sum );
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tagID.getText().toString().length() > 5 && name.getText().toString() != "Name") {
                    String Name = name.getText().toString();
                    String Money = money.getText().toString();
                    String Total = sum.getText().toString();
                    //     String MoneyInput = moneyInput.getText().toString();
                    int num1 = Integer.parseInt(Money);
                    int num2 = Integer.parseInt(Total);
                    if (num1 >= num2) {
                        int sum = num1 - num2;
                        String Sum = String.valueOf(sum);
                        String TagID = tagID.getText().toString();
                        Map<String, Object> city = new HashMap<>();
                        city.put("ID", TagID);
                        city.put("Name", Name);
                        city.put("Money", Sum);

                        db.collection("test").document(TagID)
                                .set(city)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                        DocumentReference docRef = db.collection("test").document(TagID);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(getApplicationContext(), "Cumparare cu succes", Toast.LENGTH_LONG).show();
                                        ((TextView) findViewById(R.id.moneyBar_txt)).setText(document.get("Money").toString());
                                    } else {
                                        //Log.d(TAG, "No such document");
                                    }
                                } else {
                                    //  Log.d(TAG, "get failed with ", task.getException());
                                }
                            }

                        });

                        Intent i = new Intent(getApplicationContext(),Bar.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Nu ai bani frate", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Intai scaneaza NFC", Toast.LENGTH_LONG).show();
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                //    setContentView(R.layout.activity_bar);
            }
        });


    }

    public void calculateSum(int bereNr, int shotNr, int cockNr) {
        int Total = 4*bereNr + 4* shotNr + 6* cockNr;
        TextView sum = findViewById(R.id.suma_txt);
        String Sum =String.valueOf(Total);
        sum.setText(Sum);
    }

    public void resetBar(TextView bere, TextView shot, TextView cock){
        bere.setText("0");
        shot.setText("0");
        cock.setText("0");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_ONE_SHOT);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView tagID = findViewById(R.id.tagIDBar_txt);
        String TagID = tagID.getText().toString();
        if (tagID.getText().toString().length() > 7) {
            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                          //  Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameBar_txt)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyBar_txt)).setText(document.get("Money").toString());
                        } else {
                         //   Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //  Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView tagID = findViewById(R.id.tagIDBar_txt);
        String TagID = tagID.getText().toString();
        if (tagID.getText().toString().length() > 7) {
            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                          //  Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameBar_txt)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyBar_txt)).setText(document.get("Money").toString());
                        } else {
                          //  Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //  Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        TextView tagID = findViewById(R.id.tagIDBar_txt);
        String TagID = tagID.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            ((TextView)findViewById(R.id.tagIDBar_txt)).setText(

                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                        //    Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameBar_txt)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyBar_txt)).setText(document.get("Money").toString());
                        } else {
                            //Log.d(TAG, "No such document");
                        }
                    } else {
                        //  Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

    }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}