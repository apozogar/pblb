package com.softwells.pblb.controller;

import com.softwells.pblb.controller.dto.ApiResponse;
import com.softwells.pblb.controller.dto.AuthRequest;
import com.softwells.pblb.controller.dto.AuthResponse;
import com.softwells.pblb.controller.dto.ForgotPasswordRequest;
import com.softwells.pblb.controller.dto.RegisterRequest;
import com.softwells.pblb.controller.dto.ResetPasswordRequest;
import com.softwells.pblb.exception.EmailAlreadyExistsException;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.model.UsuarioEntity;
import com.softwells.pblb.repository.UsuarioRepository;
import com.softwells.pblb.security.JwtService;
import com.softwells.pblb.service.SocioService;
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
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    final String jwt = jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new AuthResponse(jwt));
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