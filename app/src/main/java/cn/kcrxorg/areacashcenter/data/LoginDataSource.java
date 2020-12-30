package cn.kcrxorg.areacashcenter.data;

import android.util.Log;

import cn.kcrxorg.areacashcenter.data.model.LoggedInUser;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.data.model.msg.UserQueryMsg;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication\
            if(!HttpLogin.getCode().equals("0"))
            {
                return new Result.Error(new Exception("登录失败:"+HttpLogin.getMsg()));
            }
              LoggedInUser fakeUser =
                      new LoggedInUser(
                              HttpLogin.getUserID(),
                              HttpLogin.getUserName());
              return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            Log.e("kcrx","登录失败："+e.getMessage());
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}