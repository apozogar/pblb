package com.softwells.fanops.controller;

import com.softwells.fanops.controller.dto.ApiResponse;
import com.softwells.fanops.controller.dto.AuthRequest;
import com.softwells.fanops.controller.dto.AuthResponse;
import com.softwells.fanops.controller.dto.ForgotPasswordRequest;
import com.softwells.fanops.controller.dto.RegisterRequest;
import com.softwells.fanops.controller.dto.ResetPasswordRequest;
import com.softwells.fanops.exception.EmailAlreadyExistsException;
import com.softwells.fanops.model.SocioEntity;
import com.softwells.fanops.model.UsuarioEntity;
import com.softwells.fanops.repository.UsuarioRepository;
import com.softwells.fanops.security.JwtService;
import com.softwells.fanops.service.SocioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
@Slf4j
public class AuthController {

  private final SocioService socioService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender mailSender;

  @Value("${mail.from.address}")
  private String fromAddress;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
      final String jwt = jwtService.generateToken(userDetails);
      return ResponseEntity.ok(new AuthResponse(jwt));
    } catch (AuthenticationException e) {
      // Si las credenciales son incorrectas, devolvemos un 401 Unauthorized
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Email o contraseña incorrectos", null));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<SocioEntity>> register(
      @RequestBody RegisterRequest registerRequest) {
    try {
      SocioEntity nuevoSocio = socioService.registrarSocio(registerRequest);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse<>(true, "Usuario y socio registrados exitosamente", nuevoSocio));
    } catch (EmailAlreadyExistsException e) {
      // Captura el error si el email ya existe
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ApiResponse<>(false, e.getMessage(), null));
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request,
      HttpServletRequest servletRequest) {
    usuarioRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(usuario -> {
      String token = jwtService.generateToken(usuario);

      // Construimos el enlace de reseteo dinámicamente a partir del origen de la petición
      String origin = servletRequest.getHeader("Origin");
      String frontendBaseUrl =
          (origin != null) ? origin : "http://localhost:4200"; // Fallback por si no hay Origin
      String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + token;

      try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(usuario.getEmail());
        message.setSubject("Solicitud de restablecimiento de contraseña");
        message.setText(
            "Para restablecer tu contraseña, haz clic en el siguiente enlace: " + resetLink);
        mailSender.send(message);
      } catch (Exception e) {
        log.error("Error al enviar el correo de restablecimiento de contraseña a {}",
            usuario.getEmail(), e);
      }
    });

    // Siempre se devuelve OK para no revelar si un email existe en el sistema (prevención de enumeración de emails)
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
    try {
      String userEmail = jwtService.extractUsername(request.getToken());
      if (userEmail != null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if (jwtService.isTokenValid(request.getToken(), userDetails)) {
          UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(userEmail)
              .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
          usuario.setPassword(passwordEncoder.encode(request.getPassword()));
          usuarioRepository.save(usuario);
          return ResponseEntity.ok().build();
        }
      }
    } catch (Exception e) {
      log.error("Error al restablecer la contraseña", e);
    }
    return ResponseEntity.badRequest().build();
  }
}