import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Room {
    public static void main(String[] args){
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://www.runoob.com/java/java-url-processing.html");
            URLConnection urlConnection = url.openConnection();
            connection = (HttpURLConnection) urlConnection;
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder urlString = new StringBuilder();
            String current;
            while((current = in.readLine()) != null) {
                urlString.append(current);
            }
            System.out.println(urlString);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
