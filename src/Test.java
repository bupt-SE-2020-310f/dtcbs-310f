import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.message.BasicHttpResponse;

import java.util.Calendar;

public class Test {
    public static void main(String[] args){
/*        BasicHttpRequest request = new BasicHttpRequest("GET", "/",
                HttpVersion.HTTP_1_1);

        System.out.println(request.getRequestLine().getMethod());
        System.out.println(request.getRequestLine().getUri());
        System.out.println(request.getProtocolVersion());
        System.out.println(request.getRequestLine().toString());*/

        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie",
                "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie",
                "c2=b; path=\"/\", c3=c; domain=\"localhost\"");

        HeaderIterator it = response.headerIterator("Set-Cookie");

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1,0,0,0);
        long tt = System.currentTimeMillis() - calendar.getTime().getTime();
        System.out.println((int)tt);

    }
}
