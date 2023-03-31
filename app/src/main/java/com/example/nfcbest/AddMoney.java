package com.example.nfcbest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.InFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AddMoney extends AppCompatActivity {


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


        setContentView(R.layout.activity_add_money);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button button = (Button) findViewById(R.id.bagabani_btn);
        Button menu = (Button) findViewById(R.id.menuAddMoney_btn);
        TextView tagID = findViewById(R.id.edit_message);
        TextView name = findViewById(R.id.nameFromNfc);
        TextView money = findViewById(R.id.moneyFromNfc);
        EditText moneyInput = (EditText)findViewById(R.id.adauga_input) ;

        String TagID = tagID.getText().toString();



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tagID.getText().toString().length() > 7 && name.getText().toString().length() > 1 ) {
                    String Name = name.getText().toString();
                    String Money = money.getText().toString();
                    String MoneyInput = moneyInput.getText().toString();
                    int num1 = Integer.parseInt(Money);
                    int num2 = Integer.parseInt(MoneyInput);
                    int sum = num1 + num2;
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
                                    Toast.makeText(getApplicationContext(), "Adaugare bani cu succes", Toast.LENGTH_LONG).show();
                                    ((TextView) findViewById(R.id.moneyFromNfc)).setText(document.get("Money").toString());
                                } else {
                                    //Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                //  Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    Intent i = new Intent(getApplicationContext(),AddMoney.class);
                    startActivity(i);
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

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView tagID = findViewById(R.id.edit_message);
        String TagID = tagID.getText().toString();
        if (tagID.getText().toString().length() > 7) {
            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                         //   Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameFromNfc)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyFromNfc)).setText(document.get("Money").toString());
                        } else {
                           // Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
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
        TextView tagID = findViewById(R.id.edit_message);
        String TagID = tagID.getText().toString();
        if (tagID.getText().toString().length() > 7) {
            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                           // Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameFromNfc)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyFromNfc)).setText(document.get("Money").toString());
                        } else {
                           // Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
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
        TextView tagID = findViewById(R.id.edit_message);
        String TagID = tagID.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            ((TextView)findViewById(R.id.edit_message)).setText(
                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                          //  Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameFromNfc)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyFromNfc)).setText(document.get("Money").toString());
                        } else {
                            //Log.d(TAG, "No such document");
                        }
                    } else {
                        //  Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        if (tagID.getText().toString().length() > 7) {
            DocumentReference docRef = db.collection("test").document(TagID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                        //    Toast.makeText(getApplicationContext(), "Luare date cu succes", Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.nameFromNfc)).setText(document.get("Name").toString());
                            ((TextView)findViewById(R.id.moneyFromNfc)).setText(document.get("Money").toString());
                        } else {
                            //Toast.makeText(getApplicationContext(), "Tag-ul nu a fost inregistrat", Toast.LENGTH_LONG).show();
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