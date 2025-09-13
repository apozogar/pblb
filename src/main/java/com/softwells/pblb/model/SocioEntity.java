package com.softwells.pblb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "socios")
@Data
public class SocioEntity {

  @Id
  @GeneratedValue
  private UUID uid;

  @Column(unique = true, nullable = false)
  private Integer numeroSocio;

  @Column(nullable = false)
  private String nombre;

  private LocalDate fechaNacimiento;

  // Eliminamos la restricción 'unique' del DNI y email
  private String dni;
  private String direccion;
  private String poblacion;
  private String provincia;
  private String codigoPostal;
  private String telefono;
  private String email;

  @Column(nullable = false)
  private LocalDate fechaAlta;

  @Column(nullable = false)
  private boolean activo;

  @Column(nullable = false)
  private boolean abonadoBetis;

  @Column(nullable = false)
  private boolean accionistaBetis;

  @Column(nullable = false)
  private boolean exentoPago = false;

  private String numeroCuenta;

  @Column(length = 1000)
  private String observaciones;

  private String mandateId;

  private LocalDate mandateSignatureDate;

  @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
  private Set<CuotaEntity> cuotas = new HashSet<>();

  @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL)
  private Set<ParticipacionEventoEntity> participaciones = new HashSet<>();

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_uid")
  private UsuarioEntity usuario;
}
