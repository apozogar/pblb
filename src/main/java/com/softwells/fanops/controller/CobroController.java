package com.softwells.fanops.controller;

import com.softwells.fanops.controller.dto.ApiResponse;
import com.softwells.fanops.controller.dto.RemesaResponse;
import com.softwells.fanops.service.CuotaService;
import com.softwells.fanops.service.RetornoSepaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cobros")
@RequiredArgsConstructor
public class CobroController {

    private final CuotaService cuotaService;
    private final RetornoSepaService retornoSepaService;

    @PostMapping("/generar-remesa")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> generarRemesaMensual() {
        RemesaResponse resultado = cuotaService.generarCuotasYRemesaSemestral();
        return ResponseEntity.ok(new ApiResponse<>(true, resultado.getMensaje(), resultado.getXmlContent()));
    }

    @PostMapping("/procesar-retorno")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> procesarRetorno(@RequestParam("file") MultipartFile file) {
        String resultado = retornoSepaService.procesarFicheroRetorno(file);
        return ResponseEntity.ok(new ApiResponse<>(true, resultado, null));
    }

    @PostMapping("/confirmar-pagos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> confirmarPagos() {
        String resultado = cuotaService.marcarPendientesComoPagadas();
        return ResponseEntity.ok(new ApiResponse<>(true, resultado, null));
    }
}