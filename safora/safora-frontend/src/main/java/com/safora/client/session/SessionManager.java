package com.safora.client.session;

public class SessionManager {
    private static Long loggedInUserId = null;
    private static String authToken = null;

    public static void setSession(Long userId, String token) {
        loggedInUserId = userId;
        authToken = token;
    }

    public static boolean isLoggedIn() {
        return loggedInUserId != null && authToken != null;
    }

    public static Long getUserId() {
        return loggedInUserId;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void logout() {
        loggedInUserId = null;
        authToken = null;
    }
}
