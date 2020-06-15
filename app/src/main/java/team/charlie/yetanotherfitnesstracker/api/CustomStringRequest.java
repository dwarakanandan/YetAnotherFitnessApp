package team.charlie.yetanotherfitnesstracker.api;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class CustomStringRequest extends StringRequest {

    private ApiBase apiBase;

    CustomStringRequest(ApiBase apiBase, int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.apiBase = apiBase;
    }

    @Override
    public Map<String, String> getHeaders() {
        return apiBase.getCookies();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        apiBase.parseNetworkResponseForCookies(response);
        return super.parseNetworkResponse(response);
    }
}
