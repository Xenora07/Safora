package com.safora.client;
public class CheckResources {
    public static void main(String[] args) {
        System.out.println("theme.css: " + CheckResources.class.getResource("/com/safora/client/css/theme.css"));
        System.out.println("layout.css: " + CheckResources.class.getResource("/com/safora/client/css/layout.css"));
        System.out.println("login.fxml: " + CheckResources.class.getResource("/com/safora/client/views/login.fxml"));
    }
}
