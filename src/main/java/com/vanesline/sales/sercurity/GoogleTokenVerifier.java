package com.vanesline.sales.sercurity;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.oauth.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleUserInfo verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return new GoogleUserInfo(
                    payload.getSubject(), // Google user ID
                    payload.getEmail(),
                    (String) payload.get("name"),
                    payload.getEmailVerified());
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Failed to verify Google ID token", e);
        }
    }

    @Getter
    public static class GoogleUserInfo {
        private final String googleId;
        private final String email;
        private final String name;
        private final boolean emailVerified;

        public GoogleUserInfo(String googleId, String email, String name, boolean emailVerified) {
            this.googleId = googleId;
            this.email = email;
            this.name = name;
            this.emailVerified = emailVerified;
        }
    }
}
