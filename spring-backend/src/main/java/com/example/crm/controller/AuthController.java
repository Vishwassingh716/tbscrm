package com.example.crm.controller;

import com.example.crm.model.User;
import com.example.crm.security.JwtUtil;
import com.example.crm.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${google.code.verifier}")
    private String googleCodeVerifier;

    @Value("${frontend.callback.url:http://localhost:5173/auth/callback}")
    private String frontendCallbackUrl;

    private final ConcurrentHashMap<String, Map<String, Object>> authCodeCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 🔹 Google Login (EXACT Django mirror)
    @GetMapping("/social/google")
    public ResponseEntity<?> googleLogin(@RequestParam(value = "code", required = false) String code) {
        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing code"));
        }

        String tokenUrl = "https://oauth2.googleapis.com/token";

        RestTemplate rt = new RestTemplate();

        // Django → requests.post(token_url, data=data)
        String form = "code=" + encode(code)
                + "&client_id=" + encode(googleClientId)
                + "&client_secret=" + encode(googleClientSecret)
                + "&redirect_uri=" + encode(googleRedirectUri)
                + "&grant_type=authorization_code"
                + "&code_verifier=" + encode(googleCodeVerifier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("User-Agent", "python-requests/2.31.0");
        headers.set("Accept", "application/json");

        HttpEntity<String> req = new HttpEntity<>(form, headers);
        Map<String, Object> tokenResponse;

        try {
            tokenResponse = rt.postForObject(tokenUrl, req, Map.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("detail", "Failed to exchange code", "error", e.getMessage()));
        }

        if (tokenResponse == null || tokenResponse.get("access_token") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("detail", "No access_token provided"));
        }

        String accessToken = (String) tokenResponse.get("access_token");

        // Use the access token to get user info
        HttpHeaders infoHeaders = new HttpHeaders();
        infoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> infoReq = new HttpEntity<>(infoHeaders);

        ResponseEntity<Map> infoResp = rt.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET, infoReq, Map.class);

        if (infoResp.getStatusCode() != HttpStatus.OK || infoResp.getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("detail", "Invalid Google token"));
        }

        Map<String, Object> info = infoResp.getBody();
        String email = (String) info.get("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("detail", "Google token invalid: no email"));
        }

        boolean created = false;
        User user = userService.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(UUID.randomUUID().toString());
            user.setFirstName((String) info.getOrDefault("given_name", ""));
            user.setLastName((String) info.getOrDefault("family_name", ""));
            userService.register(user);
            created = true;
        }

        String jwtAccess = jwtUtil.generateAccessToken(user.getEmail());
        String jwtRefresh = jwtUtil.generateRefreshToken(user.getEmail());

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("access", jwtAccess);
        tokens.put("refresh", jwtRefresh);
        tokens.put("email", email);
        tokens.put("created", created);

        // ---- Store tokens in server cache for 1 minute ----
        String authCode = UUID.randomUUID().toString();
        authCodeCache.put(authCode, tokens);
        scheduler.schedule(() -> authCodeCache.remove(authCode), 60, TimeUnit.SECONDS);

        // ---- Redirect frontend with ONLY auth_code ----
        String frontendUrl = frontendCallbackUrl + "?auth_code=" + encode(authCode);
        HttpHeaders redirectHeaders = new HttpHeaders();
        redirectHeaders.setLocation(URI.create(frontendUrl));

        return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);
    }

    // 🔹 Exchange Code (identical to Django @api_view exchange_code)
    @GetMapping("/social/google/exchange")
    public ResponseEntity<?> exchangeCode(@RequestParam(value = "auth_code", required = false) String auth_code) {
        if (auth_code == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Missing auth_code"));

        Map<String, Object> tokens = authCodeCache.get(auth_code);
        if (tokens == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired auth_code"));

        // cache.delete(auth_code) is commented in Django, so we leave it
        return ResponseEntity.ok(tokens);
    }

    // 🔹 Me (same as Django MeView)
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("detail", "Unauthorized"));

        String email = jwtUtil.validateTokenAndGetSubject(auth.substring(7));
        if (email == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("detail", "Invalid token"));

        User user = userService.findByEmail(email);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("detail", "User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "first_name", user.getFirstName(),
                "last_name", user.getLastName()));
    }

    private static String encode(String val) {
        return java.net.URLEncoder.encode(val, StandardCharsets.UTF_8);
    }
}
