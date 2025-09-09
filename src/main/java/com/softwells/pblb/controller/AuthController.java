package com.softwells.pblb.controller;

import com.softwells.pblb.dto.AuthRequest;
import com.softwells.pblb.dto.AuthResponse;
import com.softwells.pblb.dto.ForgotPasswordRequest;
import com.softwells.pblb.dto.ResetPasswordRequest;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.repository.SocioRepository;
import com.softwells.pblb.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final SocioRepository socioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${mail.from.address}")
    private String fromAddress;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        socioRepository.findByEmail(request.getEmail()).ifPresent(socio -> {
            String token = jwtService.generateToken(socio);
            String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(socio.getEmail());
                message.setSubject("Solicitud de restablecimiento de contraseña");
                message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace: " + resetLink);
                mailSender.send(message);
            } catch (Exception e) {
                logger.error("Error al enviar el correo de restablecimiento de contraseña a {}", socio.getEmail(), e);
            }
        });

        // Always return OK to prevent email enumeration.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            String userEmail = jwtService.extractUsername(request.getToken());
            if (userEmail != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(request.getToken(), userDetails)) {
                    SocioEntity socio = socioRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
                    socio.setPassword(passwordEncoder.encode(request.getPassword()));
                    socioRepository.save(socio);
                    return ResponseEntity.ok().build();
                }
            }
        } catch (Exception e) {
            logger.error("Error resetting password", e);
        }
        return ResponseEntity.badRequest().build();
    }
}
