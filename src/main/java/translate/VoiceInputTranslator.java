//package translate;
//
//import com.baidu.aip.speech.AipSpeech;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class VoiceInputTranslator {
//    // 设置你的百度 API 密钥和密钥 ID
//    public static final String APP_ID = "YOUR_APP_ID";
//    public static final String API_KEY = "YOUR_API_KEY";
//    public static final String SECRET_KEY = "YOUR_SECRET_KEY";
//
//    public static void main(String[] args) {
//        AipSpeech speechClient = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
//        AipTranslate translateClient = new AipTranslate(APP_ID, API_KEY, SECRET_KEY);
//
//        // 进行语音识别
//        System.out.println("请说话，按下回车键结束录音...");
//        try {
//            String voiceResult = recognizeVoice(speechClient);
//            System.out.println("识别的语音内容：" + voiceResult);
//
//            // 识别目标语言或直接指定为阿拉伯语
//            System.out.println("请说出目标语言，按下回车键结束录音...或者可以直接在代码中指定为阿拉伯语（'ar'）");
//            String targetLanguageVoiceResult = recognizeVoice(speechClient);
//            String targetLanguage = determineTargetLanguage(targetLanguageVoiceResult);
//            if (targetLanguage == null) {
//                targetLanguage = "ar"; // 默认阿拉伯语
//            }
//
//            // 进行翻译
//            JSONObject translateResult = translate(translateClient, voiceResult, "auto", targetLanguage);
//            System.out.println("翻译结果：" + translateResult.getString("trans_result").split(":")[1].replaceAll("\"", ""));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 语音识别方法
//    public static String recognizeVoice(AipSpeech client) throws IOException {
//        // 创建临时文件来保存语音数据
//        File tempFile = File.createTempFile("voice", ".pcm");
//        tempFile.deleteOnExit();
//        FileOutputStream fos = new FileOutputStream(tempFile);
//
//        // 获取麦克风输入的音频流并保存到临时文件
//        InputStream microphoneInput = System.in;
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = microphoneInput.read(buffer))!= -1) {
//            fos.write(buffer, 0, bytesRead);
//        }
//        fos.close();
//
//        // 调用百度语音识别 API
//        JSONObject result = client.asr(tempFile.getAbsolutePath(), "pcm", 16000, null);
//        if (result.getInt("err_no") == 0) {
//            return result.getString("result")[0];
//        } else {
//            return "语音识别失败";
//        }
//    }
//
//    // 根据语音识别结果确定目标语言
//    public static String determineTargetLanguage(String voiceResult) {
//        switch (voiceResult.toLowerCase()) {
//            case "英语":
//                return "en";
//            case "法语":
//                return "fr";
//            case "德语":
//                return "de";
//            // 添加更多语言映射
//            default:
//                return null;
//        }
//    }
//
//    // 翻译方法
//    public static JSONObject translate(AipTranslate client, String text, String sourceLanguage, String targetLanguage) {
//        return client.translate(text, sourceLanguage, targetLanguage);
//    }
//}
