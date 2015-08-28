package eiyou.us.text.qqShare;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class BaseUiListener implements IUiListener {

    public void onComplete(JSONObject response) {
        doComplete(response);
    }

    protected void doComplete(JSONObject values) {
    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError e) {
    }

    @Override
    public void onCancel() {
    }
}