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
        Button berePlus = (Button) findViewById(R.id.berePlus_btn);
        Button bereMinus = (Button) findViewById(R.id.bereMinus_btn);
        Button shotPlus = (Button) findViewById(R.id.shotPlus_btn);
        Button shotMinus = (Button) findViewById(R.id.shotMinusBtn);
        TextView bereNr = findViewById(R.id.bereNr_txt);
        TextView shotNr = findViewById(R.id.shotNr_txt);
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
                calculateSum(sum, Integer.parseInt(shotNr.getText().toString()));
            }
        });

        bereMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String BereNr = bereNr.getText().toString();
                int num1 = Integer.parseInt(BereNr);
                int sum = num1 - 1;
                String Sum =String.valueOf(sum);
                bereNr.setText(Sum);
                calculateSum(sum, Integer.parseInt(shotNr.getText().toString()));
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
                calculateSum(sum, Integer.parseInt(bereNr.getText().toString()));
            }
        });

        shotMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ShotNr = shotNr.getText().toString();
                int num1 = Integer.parseInt(ShotNr);
                int sum = num1 - 1;
                String Sum =String.valueOf(sum);
                shotNr.setText(Sum);
                calculateSum(sum, Integer.parseInt(bereNr.getText().toString()));
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String Name = name.getText().toString();
                String Money =  money.getText().toString();
                String Total = sum.getText().toString();
           //     String MoneyInput = moneyInput.getText().toString();
                int num1 = Integer.parseInt(Money);
                int num2 = Integer.parseInt(Total);
                int sum = num1 - num2;
                String Sum =String.valueOf(sum);
                String TagID = tagID.getText().toString();
                Map<String, Object> city = new HashMap<>();
                city.put("ID", TagID);
                city.put("Name", Name);
                city.put("Money",  Sum);

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
        });



    }

    public void calculateSum(int bereNr, int shotNr) {
        int Total = 3*bereNr + 5* shotNr;
        TextView sum = findViewById(R.id.suma_txt);
        String Sum =String.valueOf(Total);
        sum.setText(Sum);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        TextView tagID = findViewById(R.id.tagIDBar_txt);
        String TagID = tagID.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            ((TextView)findViewById(R.id.tagIDBar_txt)).setText(
                    "NFC Tag\n" +
                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
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