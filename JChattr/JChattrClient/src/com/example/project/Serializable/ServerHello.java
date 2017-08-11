package com.example.project.Serializable;

import java.io.Serializable;

public class ServerHello implements Serializable {
    private boolean requestUserCreation, requestLogin;

    public boolean isRequestUserCreation() {
        return requestUserCreation;
    }

    public void setRequestUserCreation(boolean requestUserCreation) {
        this.requestUserCreation = requestUserCreation;
    }

    public boolean isRequestLogin() {
        return requestLogin;
    }

    public void setRequestLogin(boolean requestLogin) {
        this.requestLogin = requestLogin;
    }
}
