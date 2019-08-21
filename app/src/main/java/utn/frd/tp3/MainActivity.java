package utn.frd.tp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Handler del botón
        findViewById(R.id.btnIngresar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlCliente = new Datos().getUrlRalito() + "/cliente/39387297";

                new MiAsyncTask().execute(urlCliente,"GET");

                Intent i = new Intent(MainActivity.this, Inicio.class);
                startActivity(i);
            }

        });

    }
    public static class Datos{
        String urlRalito = "http://192.168.0.47:8080/tp2019/rest";
        String urlEsferopolis = "http://lsi.no-ip.org:8282/esferopolis/api";
        int idCliente = 65;
        String du = "623";

        public String getUrlRalito() {
            return urlRalito;
        }

        public int getIdCliente() {
            return idCliente;
        }
        public String getIdClienteString() {
            return String.valueOf(this.idCliente);
        }

        public String getUrlEsferopolis() {
            return urlEsferopolis;
        }

        public String getDu() {
            return du;
        }
    }
    public class MiAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings){
            String method = strings[1];
            if(method == "GET"){
                return RESTService.makeGetRequest(strings[0]);
            }else{
                String jsonParam = strings[2];
                return RESTService.callREST(strings[0],strings[1],jsonParam);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast notificacion = Toast.makeText(
                    getApplicationContext(), result, Toast.LENGTH_LONG);
            notificacion.show();
        }
    }

    public static class RESTService{
        public static String makeGetRequest(String restURL){

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL( restURL );
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }
                inputStream.close();

                result = sBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;

        }
        public static String callREST(String restURL, String method, String jsonParam){
            String result = "";

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL( restURL );
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod( method );
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(
                        jsonParam.getBytes().length);

                urlConnection.setRequestProperty(
                        "Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                urlConnection.connect();

                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(jsonParam.getBytes());
                os.flush();

                StringBuilder sBuilder;
                InputStream inputStream;
                inputStream= urlConnection.getInputStream();

                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 5);
                sBuilder = new StringBuilder();
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }
                result = sBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return result;
        }

    }

}
