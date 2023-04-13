package com.mx.sireco.data;

import android.os.StrictMode;
import android.util.Log;

import com.mx.sireco.data.model.LoggedInUser;
import com.mx.sireco.util.Config;
import com.mx.sireco.util.UtilGS;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    UtilGS util = new UtilGS();
    public Result<LoggedInUser> login(String username, String password,String pin) {

        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
            }
            String res = this.util.sendPost("http://sireco.com.mx/mob/log.php", "user=" + username + "&&pass=" + password+ "&&pin=" + pin);
            Log.d("AAA",res);
            if (res == null || !"success".equals(res.trim())) {
                return new Result.Error(new IOException("Error logging in"));
            }
            Config.getInstance().setKey(Config.USER_BEAN, username);



            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}