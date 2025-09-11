package com.softwells.pblb.controller.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

  private String token;
  private String password;
}