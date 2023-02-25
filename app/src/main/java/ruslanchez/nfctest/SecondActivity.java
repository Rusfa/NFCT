package ruslanchez.nfctest;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.nfc.Tag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class SecondActivity extends AppCompatActivity
        implements NfcAdapter.CreateNdefMessageCallback,
                    NfcAdapter.OnNdefPushCompleteCallback
{
    String TAG = "TEST";
    NfcAdapter mNfcAdapter;
    ArrayList<String> mRecvdMessages = new ArrayList<>();
    ArrayList<String> mToSendMessages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        setupGUI();
    }

    protected void setupGUI()
    {
        TextView hello = findViewById(R.id.hello_view);
        if (mNfcAdapter == null)
        {
            hello.setText("Cant get nfc adapter!");
        }
        else
        {
            hello.setText(" Connected!\n");
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }

        Button sendMessageSubmit = findViewById(R.id.send_messages_submit);
        sendMessageSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandler(v);
            }
        });
    }

    protected void sendMessageHandler(View view)
    {
        EditText messageInput = findViewById(R.id.message_input);
        String newMessage = messageInput.getText().toString();
        mToSendMessages.add(newMessage);
        TextView chatView = findViewById(R.id.hello_view);
        String chatString = chatView.getText().toString();
        chatString += newMessage + "\n\r";
        chatView.setText(chatString);
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if (mToSendMessages.size() == 0)
        {
            return null;
        }
        Log.i(TAG, "Detected another device!");
        int messageCount = mToSendMessages.size();
        NdefRecord[] messageRecords = new NdefRecord[messageCount];
        for (int i = 0; i < messageCount; ++i)
        {
//            byte[] payload = mToSendMessages.get(i).getBytes(StandardCharsets.UTF_8);
            byte[] payload = "That's all folks!".getBytes(StandardCharsets.UTF_8);
            messageRecords[i] = NdefRecord.createMime("text/plain", payload);
//                    new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
//                    new byte[0], payload);
        }
        mToSendMessages.clear();
        return new NdefMessage(messageRecords);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        if (mToSendMessages.size() == 0)
        {
            return;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            handleTagIntent(intent);
        }
        else
        {
            handleNfcIntent(intent);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))
        {
            handleTagIntent(getIntent());
        }
        else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            handleNfcIntent(getIntent());
        }
    }


    protected void handleTagIntent(Intent tagIntent)
    {
        Tag tag = tagIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        StringBuilder idString = new StringBuilder();
        for (byte num : id)
        {
            idString.append(String.format("%02x ", num));
        }
        AndroidMessageBox.showMessageBox(this, "Info", "Connected tag id = " + idString.toString());
    }


    protected void handleNfcIntent(Intent nfcIntent)
    {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction()))
        {
            Parcelable[] recievedArray =
                    nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (recievedArray != null)
            {
                NdefMessage[] messages = (NdefMessage[]) recievedArray;
                for (NdefMessage message: messages)
                {
                    for (NdefRecord record: message.getRecords())
                    {
                        String messageText = new String(record.getPayload());
                        mRecvdMessages.add(messageText);
                    }
                }
            }
            updateMessageView();
        }
    }

    protected void updateMessageView()
    {
        TextView chatView = findViewById(R.id.hello_view);
        String chatString = chatView.getText().toString();
        for (String messageText: mRecvdMessages)
        {
            chatString = chatString + "\r\n" + messageText;
        }
        chatView.setText(chatString);
        mRecvdMessages.clear();
    }
}