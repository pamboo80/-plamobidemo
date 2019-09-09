package in.bangbit.plamobidemo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessagesRequest extends JsonArrayRequest {

    public MessagesRequest(double latitude,double longitude, final Response.Listener<ArrayList<Message>> listener, final Response.ErrorListener errorListener) {
        super(Method.GET,
                "http://api.plamobi.com:3000/users/landmarklist/"
                        .concat(String.valueOf(latitude))
                        .concat("/")
                        .concat(String.valueOf(longitude)),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        final ArrayList<Message> messages = new ArrayList<>(response.length());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonMessage = response.getJSONObject(i);
                                messages.add(new Message(
                                        jsonMessage.getString("username"),
                                        jsonMessage.getString("userImg"),
                                        jsonMessage.getDouble("latitude"),
                                        jsonMessage.getDouble("longitude"),
                                        jsonMessage.getString("email"),
                                        jsonMessage.getString("phone"),
                                        jsonMessage.getString("chat"),
                                        jsonMessage.getString("message"),
                                        jsonMessage.getString("messageType"),
                                        jsonMessage.getLong("messageTimeStamp")
                                ));
                            }
                            listener.onResponse(messages);
                            //write to DB
                            MainSingleton.getDatabaseHelper().putMessages(messages);
                        } catch (JSONException e) {
                            errorListener.onErrorResponse(new VolleyError(e.getCause()));
                        }
                    }
                },
                errorListener
        );
    }
}
