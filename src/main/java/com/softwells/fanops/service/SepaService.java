package com.softwells.fanops.service;

import com.softwells.fanops.model.PenaEntity;
import com.softwells.fanops.model.SocioEntity;
import com.softwells.fanops.repository.SocioRepository;
import com.softwells.fanops.repository.PenaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SepaService {

  private final SocioRepository socioRepository;
  private final PenaRepository penaRepository;

  public void generarFicheroSepa(LocalDateTime fechaPago) {
    List<SocioEntity> sociosActivos = socioRepository.findByActivo(true);
    String xmlContent = generarXml(sociosActivos, fechaPago);
    // Aquí puedes guardar el 'xmlContent' en un fichero, enviarlo, etc.
    // Por ejemplo: Files.writeString(Paths.get("sepa.xml"), xmlContent);
  }

  public String generarXml(List<SocioEntity> socios, LocalDateTime fechaPago) {
    double montoTotal = 0.0;
    int numeroTransacciones = socios.size();
    StringBuilder transaccionesXml = new StringBuilder();

    // Recuperamos los datos de la peña desde la base de datos
    // Asumimos que solo hay una entrada en la tabla 'pena' con ID 1
    PenaEntity pena = penaRepository.findById(1L)
        .orElseThrow(
            () -> new IllegalStateException("No se encontraron los datos de la peña con ID 1"));

    final String FECHA_COBRO = fechaPago.toLocalDate().toString();

    // Asume que la lista de socios incluye los datos de mandato y dirección
    for (SocioEntity socio : socios) {
      double montoCuota;
      int edad = 30;
      if (socio.getFechaNacimiento() != null) {
        edad = Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears();
      }
      if (edad > pena.getEdadMayoria()) {
        montoCuota = pena.getCuotaAdulto();
      } else {
        montoCuota = pena.getCuotaMenor();
      }

      montoTotal += montoCuota;

      // Aquí se asume que SocioEntity tiene los nuevos campos necesarios
      transaccionesXml.append(
          String.format(
              """
                    <DrctDbtTxInf>
                      <PmtId>
                        <EndToEndId>%s</EndToEndId>
                      </PmtId>
                      <InstdAmt Ccy="EUR">%.2f</InstdAmt>
                      <DrctDbtTx>
                        <MndtRltdInf>
                          <MndtId>%s</MndtId>
                          <DtOfSgntr>%s</DtOfSgntr>
                        </MndtRltdInf>
                      </DrctDbtTx>
                      <DbtrAgt>
                        <FinInstnId/>
                      </DbtrAgt>
                      <Dbtr>
                        <Nm>%s</Nm>
                        <PstlAdr>
                          <Ctry>ES</Ctry>
                          <AdrLine>%s</AdrLine>
                          <AdrLine>%s</AdrLine>
                        </PstlAdr>
                      </Dbtr>
                      <DbtrAcct>
                        <Id>
                          <IBAN>%s</IBAN>
                        </Id>
                      </DbtrAcct>
                      <RmtInf>
                        <Ustrd>%s</Ustrd>
                      </RmtInf>
                    </DrctDbtTxInf>
                  """,
              socio.getUid(), // Asume que SocioEntity tiene un ID
              montoCuota,
              socio.getMandateId(), // Asume un nuevo campo para el ID del mandato
              socio.getMandateSignatureDate(), // Asume un nuevo campo para la fecha de firma
              socio.getNombre(),
              socio.getDireccion(), // Asume un nuevo campo para la dirección
              socio.getCodigoPostal(), // Asume un nuevo campo para la localidad y CP
              socio.getNumeroCuenta(),
              "CUOTA " + FECHA_COBRO
          )
      );
    }

    String xmlTemplate =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pain.008.001.08">
              <CstmrDrctDbtInitn>
                <GrpHdr>
                  <MsgId>%s</MsgId>
                  <CreDtTm>%s</CreDtTm>
                  <NbOfTxs>%d</NbOfTxs>
                  <CtrlSum>%.2f</CtrlSum>
                  <InitgPty>
                    <Nm>%s</Nm>
                    <Id>
                      <OrgId>
                        <Othr>
                          <Id>%s</Id>
                          <SchmeNm>
                            <Prtry>SEPA</Prtry>
                          </SchmeNm>
                        </Othr>
                      </OrgId>
                    </Id>
                  </InitgPty>
                </GrpHdr>
                <PmtInf>
                  <PmtInfId>%s</PmtInfId>
                  <PmtMtd>DD</PmtMtd>
                  <BtchBkg>true</BtchBkg>
                  <NbOfTxs>%d</NbOfTxs>
                  <CtrlSum>%.2f</CtrlSum>
                  <PmtTpInf>
                    <SvcLvl>
                      <Cd>SEPA</Cd>
                    </SvcLvl>
                    <LclInstrm>
                      <Cd>CORE</Cd>
                    </LclInstrm>
                    <SeqTp>FNAL</SeqTp>
                  </PmtTpInf>
                  <ReqdColltnDt>%s</ReqdColltnDt>
                  <Cdtr>
                    <Nm>%s</Nm>
                    <PstlAdr>
                      <Ctry>ES</Ctry>
                      <AdrLine>%s</AdrLine>
                      <AdrLine>%s</AdrLine>
                    </PstlAdr>
                  </Cdtr>
                  <CdtrAcct>
                    <Id>
                      <IBAN>%s</IBAN>
                    </Id>
                  </CdtrAcct>
                  <CdtrAgt>
                    <FinInstnId>
                      <BICFI>%s</BICFI>
                    </FinInstnId>
                  </CdtrAgt>
                  <ChrgBr>SLEV</ChrgBr>
                  <CdtrSchmeId>
                    <Id>
                      <PrvtId>
                        <Othr>
                          <Id>%s</Id>
                          <SchmeNm>
                            <Prtry>SEPA</Prtry>
                          </SchmeNm>
                        </Othr>
                      </PrvtId>
                    </Id>
                  </CdtrSchmeId>
                  %s
                </PmtInf>
              </CstmrDrctDbtInitn>
            </Document>
            """;

    return String.format(
        xmlTemplate,
        "PRE" + fechaPago.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + UUID.randomUUID()
            .toString().substring(0, 8), // MsgId
        fechaPago.format(DateTimeFormatter.ISO_DATE_TIME),
        numeroTransacciones,
        montoTotal,
        pena.getNombre(),
        pena.getIniciadorId(),
        "PMTINF-" + UUID.randomUUID().toString().substring(0, 8), // PmtInfId
        numeroTransacciones,
        montoTotal,
        FECHA_COBRO,
        pena.getNombre(),
        pena.getDireccion1(),
        pena.getDireccion2(),
        pena.getCuentaIban(),
        pena.getCuentaBic(),
        pena.getIniciadorId(),
        transaccionesXml.toString()
    );
  }
}