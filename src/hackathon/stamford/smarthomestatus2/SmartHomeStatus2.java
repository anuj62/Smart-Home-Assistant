package hackathon.stamford.smarthomestatus2;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class SmartHomeStatus2 extends Activity implements OnInitListener{

	ImageButton micButton;
	TextView speech_text;
	TextToSpeech tts;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_status2);
        
        micButton = (ImageButton)findViewById(R.id.mic_button);
        speech_text = (TextView) findViewById(R.id.speech_text);
        tts = new TextToSpeech(this, this);
        System.out.println("texttospeech");
		
        micButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
				try {
		            startActivityForResult(intent, 1);
		        } catch (ActivityNotFoundException a) {
		            Log.e("On Click", "activity not found exception");
		        }
			}
		});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode == 1 && resultCode == RESULT_OK && data != null){
    		ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    		final String command = result.get(0);
            speech_text.setText(command);
            new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new RelationshipExtractor(command, tts);
				}
			}).start();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smart_home_status2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status==TextToSpeech.SUCCESS) {
			System.out.println("TTS init success");
			tts.setLanguage(Locale.getDefault());
		}else{
			System.out.println("Error in TTS init");
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (tts!=null) {
			tts.stop();
			tts.shutdown();
		}
	}
}
