package com.gym.autenticacion.dto;

// dto/RegisterRequest.java
public record RegisterRequest(String rut, String correo, String clave,
                              String pnombre, String snombre, String appaterno, String apmaterno) {}