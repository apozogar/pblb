package com.softwells.fanops.service;

import com.softwells.fanops.enums.EstadoCuota;
import com.softwells.fanops.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetornoSepaService {

  private final CuotaRepository cuotaRepository;

  @Transactional
  public String procesarFicheroRetorno(MultipartFile file) {
    int cuotasRechazadas = 0;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Desactiva la validación DTD para evitar problemas de seguridad y conexión
      factory.setNamespaceAware(true); // ¡Importante! Habilitar el reconocimiento de namespaces
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document doc = factory.newDocumentBuilder().parse(file.getInputStream());
      doc.getDocumentElement().normalize();

      // Buscamos directamente todas las etiquetas "EndToEndId" dentro del documento
      NodeList nodeList = doc.getElementsByTagName("EndToEndId");

      for (int i = 0; i < nodeList.getLength(); i++) {
        String endToEndId = nodeList.item(i).getTextContent();

        // El EndToEndId es el UID de nuestra cuota
        UUID cuotaUid = UUID.fromString(endToEndId);

        cuotaRepository.findById(cuotaUid).ifPresent(cuota -> {
          if (cuota.getEstado() == EstadoCuota.PENDIENTE) {
            cuota.setEstado(EstadoCuota.RECHAZADA);
            cuotaRepository.save(cuota);
            log.info("Cuota {} marcada como RECHAZADA.", cuota.getUid());
          }
        });
        cuotasRechazadas++;
      }
    } catch (Exception e) {
      log.error("Error procesando el fichero de retorno SEPA", e);
      throw new RuntimeException("Error al leer el fichero de retorno: " + e.getMessage());
    }
    return "Procesadas " + cuotasRechazadas + " devoluciones.";
  }
}