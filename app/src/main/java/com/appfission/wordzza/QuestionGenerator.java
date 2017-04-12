package com.appfission.wordzza;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srikanthmannepalle on 3/18/17.
 */

public abstract class QuestionGenerator extends Activity {
    protected String TAG;
    protected ArrayList<String> totaldWordList;
    protected ArrayList<String> processedWordList;
    protected HashMap<String, ArrayList<String>> wordlist;
    private long size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = QuestionGenerator.class.getName();
        processedWordList = new ArrayList<>();
        totaldWordList = new ArrayList<>();
        try {
            populateTotalWordList();
        } catch (IOException e) {
          Log.d(TAG, "Exception occurred in populating the massive wordlist");
        }
    }

    private void populateTotalWordList() throws IOException {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.finalwordfile_unique));
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                totaldWordList.add(line.toUpperCase()); //bug fix for alternate option
            }
            Log.d(TAG, "Size of total word list = " + totaldWordList.size());
        } catch (Exception e) {
            Log.d(TAG, "Exception occurred in creating word list");
            throw e;
        }
    }

    protected void readWordsFromFile() throws IOException {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.wordslist);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //convert json string to object
        wordlist = objectMapper.readValue(json, HashMap.class);
        for (Map.Entry<String, ArrayList<String>> eachEntry : wordlist.entrySet()) {
            processedWordList.add(eachEntry.getKey());
        }
    }

    protected boolean isAnagram(String userAnswer, String correctAnswer) {
        boolean isAnagram = false;
        if (userAnswer.length() != correctAnswer.length()) {
            return isAnagram;
        } else {
            char[] chArr1 = userAnswer.toCharArray();
            Arrays.sort(chArr1);
            char[] chArr2 = correctAnswer.toCharArray();
            Arrays.sort(chArr2);
            if (new String(chArr1).equals(new String(chArr2))) {
                isAnagram = true;
            } else {
                isAnagram = false;
            }
        }
        return isAnagram;
    }

    protected String getObfuscatedActualWord() {
        int index = (int) Math.round(Math.random()*(totaldWordList.size() - 1));
        return totaldWordList.get(index);
    }

    protected   ArrayList<ArrayList<String>> generateQuestionAndAnswers() {

        //decide the logic for index as the user starts scoring more
        size = wordlist.size();
        Log.d(TAG, "wordlist size = " + size);
        int index = (int) (Math.random()*(size - 1));
        String newWord = processedWordList.get(index);
        ArrayList<String> oneNewWord = new ArrayList<>();
        oneNewWord.add(newWord);
        ArrayList<String> wordCombinations = wordlist.get(newWord);
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        output.add(0, oneNewWord);
        output.add(1, wordCombinations);
        return output;
    }

}
