package common;

public class UserAuthData {
    private final String userId;
    private final String token;
    private final String cookie;

    public UserAuthData(String userId, String token, String cookie) {
        this.userId = userId;
        this.token = token;
        this.cookie = cookie;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getCookie() {
        return cookie;
    }
}
