import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.message.BasicHttpResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args){

        //roomId, state, currTemp, targetTemp, fan, fee
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        List<String> strs = new ArrayList<>();
        String str = "hell0";
        JSONObject jo = new JSONObject();
        strs.add(str);
        strs.add(str + 1);
        strs.add(str);
        jo.put("data", strs);
        String sss = ("{\"data\":{\"Room1\":[\"roomId\", \"state\", \"currTemp\", \"targetTemp\", \"fan\", \"fee\"]," +
                                    "\"Room2\":[\"roomId\", \"state\", \"currTemp\", \"targetTemp\", \"fan\", \"fee\"]," +
                                   "\"Room3\":[\"roomId\", \"state\", \"currTemp\", \"targetTemp\", \"fan\", \"fee\"]}}");
        JSONObject j = JSONObject.parseObject(sss);
        System.out.print(j);

    }
}
