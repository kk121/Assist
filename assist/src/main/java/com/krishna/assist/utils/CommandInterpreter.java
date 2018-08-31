package com.krishna.assist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.krishna.assist.api.ApiClient;
import com.krishna.assist.api.ApiInterface;
import com.krishna.assist.api.NotificationData;
import com.krishna.assist.api.RequestNotificaton;
import com.krishna.assist.data.pojo.Command;
import com.krishna.assist.data.pojo.DBPojo;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CommandInterpreter {
    private static final String TAG = "CommandInterpreter";
    //commands
    private static final String SQL_QUERY = "sql_query";
    private static final String SHARED_PREF = "shared_pref";
    private static final String APP_INFO = "app_info";
    private static final String LIST_DIR = "list_dir";

    public static void interpret(Context context, Command commandObj, String to) throws Exception {
        String command = commandObj.getCommand();
        if (command == null) return;
        String result = null;
        switch (command) {
            case SQL_QUERY:
                result = runSqlQuery(context, commandObj);
                break;
            case SHARED_PREF:
                result = getSharePrefValue(context, commandObj);
                break;
            case APP_INFO:
                result = getAppInfo(context);
                break;
            case LIST_DIR:
                result = listDir(context, commandObj);
                break;
            default:
                result = "command not found";
        }
        sendResultToServer(commandObj, to, result);
    }

    private static String listDir(Context context, Command commandObj) {
        StringBuilder sb = new StringBuilder();
        if (commandObj.getArgs() != null && commandObj.getArgs().length > 0) {
            String dirName = commandObj.getArgs()[0];
            if (!TextUtils.isEmpty(dirName)) {
                File dir = new File(context.getFilesDir().getParent(), dirName);
                if (dir.exists()) {
                    if (dir.isDirectory()) {
                        File filesList[] = dir.listFiles();
                        if (filesList != null) {
                            for (File file : filesList) {
                                sb.append("{")
                                        .append(file.getName()).append(", ")
                                        .append(file.length()).append(", ")
                                        .append(file.lastModified())
                                        .append("}, ");
                            }
                            sb.deleteCharAt(sb.length() - 1);
                        }
                    } else {
                        sb.append("{")
                                .append(dir.getName()).append(",")
                                .append(dir.length()).append(", ")
                                .append(dir.lastModified())
                                .append("}");
                    }
                } else {
                    sb.append("file does not exists");
                }
            } else {
                sb.append("file name is null");
            }
        }
        return sb.toString();
    }

    private static String getAppInfo(Context context) {
        Map<String, String> deviceInfo = Utililty.getDeviceInfo(context);
        JSONObject jsonObject = new JSONObject(deviceInfo);
        return jsonObject.toString();
    }

    private static String getSharePrefValue(Context context, Command commandObj) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String args[] = commandObj.getArgs();
        String flags[] = commandObj.getFlags();
        if (args != null && args.length >= 2) {
            if (flags != null && flags.length > 0 && flags[0].equals("memory")) {
                SharedPreferences pref = context.getSharedPreferences(args[0], Context.MODE_PRIVATE);
                for (int i = 1; i < args.length; i++) {
                    String key = args[i];
                    sb.append(pref.getString(key, null));
                    if (i != args.length - 1) {
                        sb.append(", ");
                    }
                }
            } else {
                List<File> allPrefFiles = DBUtils.getAllSharedPreferences(context);
                for (File file : allPrefFiles) {
                    if (file.getName().equals(args[0])) {
                        for (int i = 1; i < args.length; i++) {
                            String key = args[i];
                            sb.append(DBUtils.getSharedPrefValue(file.getPath(), key));
                            if (i != args.length - 1) {
                                sb.append(", ");
                            }
                        }
                        break;
                    }
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static String runSqlQuery(Context context, Command commandObj) {
        List<DBPojo> dbPojoList = DBUtils.getAllDatabases(context);
        if (dbPojoList != null) {
            if (commandObj.getArgs() != null && commandObj.getArgs().length >= 2) {
                String dbName = commandObj.getArgs()[0];
                String query = commandObj.getArgs()[1];
                for (DBPojo dbPojo : dbPojoList) {
                    if (dbPojo.getDbName().equals(dbName)) {
                        return DBUtils.executeQuery(context, dbPojo, query);
                    }
                }
            }
        }
        return null;
    }

    private static void sendResultToServer(Command commandObj, String from, String result) throws IOException {
        String args = commandObj.getArgs() != null ? Arrays.toString(commandObj.getArgs()).replace("[", "").replace("]", "") : "";
        String flags = commandObj.getFlags() != null ? Arrays.toString(commandObj.getFlags()).replace("[", "").replace("]", "") : "";

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        RequestNotificaton requestNotificaton = new RequestNotificaton("/topics/assist", new NotificationData(from, commandObj.getCommand(), args, flags, result));
        String fcmKey = "key=AAAAhJqp1LU:APA91bFtFgVC3j-MC3WaMhYokCcOWNtNoJpsXl5LsYzoUOSEx5syvC7nGSVdZsT-pjMkdWvey0gK_6PjsQVyl2ddkoGaai5bjdI8GEwJy62iBXxp_vUUoYqhxhq5VJv5enTyoH3-Qj4f";
        Call<ResponseBody> call = api.sendPushNotification(fcmKey, requestNotificaton);

        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            Log.d(TAG, "result sent to assist server: " + result);
        } else {
            Log.d(TAG, "failed to send result to assist server: status code: " + response.code() + ", \n" + result);
        }
    }
}
