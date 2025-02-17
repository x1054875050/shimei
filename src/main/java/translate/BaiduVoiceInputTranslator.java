package translate;

import com.baidu.aip.speech.AipSpeech;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Base64;

public class BaiduVoiceInputTranslator {
    private static final String APP_ID = "YOUR_APP_ID"; // 替换为你的APP ID
    private static final String API_KEY = "YOUR_API_KEY"; // 替换为你的API KEY
    private static final String SECRET_KEY = "YOUR_SECRET_KEY"; // 替换为你的SECRET KEY
    private static final String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String SPEECH_RECOGNITION_URL = "https://vop.baidu.com/pro_api";
    private static final String TRANSLATE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    public static void main(String[] args) throws Exception {
        String audioFilePath = "path/to/audio.wav"; // 替换为你的音频文件路径
        String accessToken = getAccessToken();

        String recognizedText = recognizeSpeech(audioFilePath, accessToken);
        String translatedText = translateText(recognizedText, "en", "zh");

        System.out.println("识别文本: " + recognizedText);
        System.out.println("翻译文本: " + translatedText);
    }

    private static String getAccessToken() throws IOException {
        String url = ACCESS_TOKEN_URL + "?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("access_token");
        }
    }

    private static String recognizeSpeech(String audioFilePath, String accessToken) throws IOException {
        byte[] audioData = readAudioFile(audioFilePath);
        String audioBase64 = Base64.getEncoder().encodeToString(audioData);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("format", "wav");
        jsonObject.put("rate", 16000);
        jsonObject.put("channel", 1);
        jsonObject.put("cuid", "YOUR_CUID"); // 自定义用户ID
        jsonObject.put("token", accessToken);
        jsonObject.put("len", audioData.length);
        jsonObject.put("speech", audioBase64);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(SPEECH_RECOGNITION_URL);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonObject.toString()));

            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String jsonResponse = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(jsonResponse);

            JSONArray jsonArray = result.getJSONArray("result");
            return jsonArray.getString(0);
        }
    }

    private static String translateText(String text, String from, String to) throws IOException {
        String url = TRANSLATE_URL + "?q=" + text + "&from=" + from + "&to=" + to + "&appid=" + APP_ID;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String jsonResponse = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(jsonResponse);

            JSONArray transResult = result.getJSONArray("trans_result");
            return transResult.getJSONObject(0).getString("dst");
        }
    }

    private static byte[] readAudioFile(String audioFilePath) throws IOException {
        File file = new File(audioFilePath);
        byte[] audioData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(audioData);
        }
        return audioData;
    }
}
