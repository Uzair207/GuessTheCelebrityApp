package com.example.guessthecelebrityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    ArrayList<String> imgList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    int locationOfCorrectAnswer;
    String[] answers = new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Random rand;
    int selectedImgIndex;
    public void guessCelebrity(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong it was "+nameList.get(selectedImgIndex),Toast.LENGTH_SHORT).show();
        }
        generateQuestion();

    }
    public void generateQuestion(){
        try {
            selectedImgIndex = rand.nextInt(imgList.size());

            DownloadImageTask imgTask = new DownloadImageTask();
            Bitmap myBitMap = imgTask.execute(imgList.get(selectedImgIndex)).get();
            img.setImageBitmap(myBitMap);
            locationOfCorrectAnswer = rand.nextInt(4);
            int locationOfIncorrectAnswer;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = nameList.get(selectedImgIndex);
                } else {
                    locationOfIncorrectAnswer = rand.nextInt(imgList.size());
                    while (locationOfIncorrectAnswer == selectedImgIndex) {
                        locationOfIncorrectAnswer = rand.nextInt(imgList.size());
                    }
                    answers[i] = nameList.get(locationOfIncorrectAnswer);
                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while(data!=-1){
                    char c = (char) data;
                    result+=c;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Something went Wrong!";
            }
        }
    }
    class DownloadImageTask extends AsyncTask<String,Void, Bitmap>{
            @Override
        protected Bitmap doInBackground(String... urls) {
                try{
                    URL url = new URL(urls[0]);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
                }
                catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        rand = new Random();
        String result = "";
        DownloadTask task = new DownloadTask();

        try {
            result = task.execute("https://web.archive.org/web/20190119082828/www.posh24.se/kandisar").get();
            String stuff[] = result.split("<div class=\"listedArticles\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher = p.matcher(stuff[0]);
            while(matcher.find()){
                imgList.add(matcher.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            matcher = p.matcher(stuff[0]);
            while(matcher.find()){
                nameList.add(matcher.group(1));
            }
            generateQuestion();
            }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}