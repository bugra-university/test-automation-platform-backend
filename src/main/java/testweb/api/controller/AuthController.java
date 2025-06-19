package testweb.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import testweb.api.model.request.LoginRequest;
import testweb.api.model.request.SignupRequest;
import testweb.api.model.response.ApiResponse;
import testweb.api.model.response.JwtResponse;
import testweb.entity.Auth;
import testweb.repository.UserRepository;
import testweb.security.JwtUtils;
import testweb.security.services.UserDetailsImpl;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder encoder;
        private final JwtUtils jwtUtils;

        public AuthController(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        PasswordEncoder encoder,
                        JwtUtils jwtUtils) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.encoder = encoder;
                this.jwtUtils = jwtUtils;
        }

        @PostMapping("/signin")
        public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt,
                                userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getEmail(),
                                roles));
        }

        @PostMapping("/signup")
        public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
                if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new ApiResponse(false, "Bu kullanıcı adı zaten alınmış!"));
                }

                if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new ApiResponse(false, "Bu e-posta adresi zaten kullanımda!"));
                }

                // Create new user's account
                Auth.User user = new Auth.User(signUpRequest.getUsername(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()));
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());

                userRepository.save(user);

                return ResponseEntity.ok(new ApiResponse(true, "Kullanıcı başarıyla kaydedildi!"));
        }
}
