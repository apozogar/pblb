package com.softwells.pblb.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "socios")
@Data
public class SocioEntity implements UserDetails {

  @Id
  @GeneratedValue
  private UUID uid;

  @Column(unique = true, nullable = false)
  private String numeroSocio;

  @Column(nullable = false)
  private String nombre;

  private LocalDate fechaNacimiento;
  private String dni;
  private String direccion;
  private String poblacion;
  private String provincia;
  private String codigoPostal;
  private String telefono;
  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @Column(nullable = false)
  private LocalDate fechaAlta;

  @Column(nullable = false)
  private boolean activo;

  @Column(nullable = false)
  private boolean abonadoBetis;

  @Column(nullable = false)
  private boolean accionistaBetis;

  private String numeroCuenta;

  @Column(length = 1000)
  private String observaciones;

  private String mandateId;

  private LocalDate mandateSignatureDate;

  @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
  private Set<CuotaEntity> cuotas = new HashSet<>();

  @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
  private Set<ParticipacionEventoEntity> participaciones = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "socio_roles",
      joinColumns = @JoinColumn(name = "socio_uid"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return activo;
  }
}
