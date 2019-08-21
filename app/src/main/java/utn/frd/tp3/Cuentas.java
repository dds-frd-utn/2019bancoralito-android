package utn.frd.tp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Cuentas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);
        MainActivity.Datos datos = new MainActivity.Datos();
        String idCliente = datos.getIdClienteString();
        String urlCuenta = datos.getUrlRalito() + "/cuenta/listaCuentas/"+idCliente;

        //busco las cuentas del usuario

        new MiAsyncTask().execute(urlCuenta,"GET");

    }

    class MiAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings){
            String method = strings[1];
            if(method == "GET"){
                return MainActivity.RESTService.makeGetRequest(strings[0]);
            }else{
                String jsonParam = strings[2];
                return MainActivity.RESTService.callREST(strings[0],strings[1],jsonParam);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            final TextView helloTextView = (TextView) findViewById(R.id.text_cuentas);
            helloTextView.setText(result);
        }
    }
}
