package com.softwells.fanops.controller.dto;

import lombok.Data;

@Data
public class AuthRequest {
  private String email;
  private String password;
}