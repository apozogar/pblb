-- Borra los datos existentes para evitar duplicados al reiniciar la aplicación.
-- Esto es útil en desarrollo para asegurar que la tabla siempre tiene los datos más recientes del script.
DELETE FROM iban_bic_mapping;

-- Inserta los mapeos de Código de Banco (4 dígitos del IBAN) a su código BIC correspondiente.
INSERT INTO iban_bic_mapping (bank_code, bic) VALUES
-- Grandes Bancos
('0049', 'BSCHESMMXXX'), -- Banco Santander
('0182', 'BBVAESMMXXX'), -- BBVA
('2100', 'CAIXESBBXXX'), -- CaixaBank (incluye Bankia, cuyo código era 2038)
('0081', 'BSABESBBXXX'), -- Banco Sabadell
('0128', 'BKBKESMMXXX'), -- Bankinter
('0030', 'ESPCESMMXXX'), -- Antiguo Banesto (integrado en Santander)

-- Otros Bancos Nacionales
('2080', 'CAGLESMMXXX'), -- Abanca
('2095', 'CASKES2BXXX'), -- Kutxabank
('2103', 'UCJAES2MXXX'), -- Unicaja Banco
('0061', 'BMARES2MXXX'), -- Banca March
('0186', 'MEDBESMMXXX'), -- Banco Mediolanum
('0019', 'DEUTESBBXXX'), -- Deutsche Bank

-- Cajas Rurales y Cooperativas de Crédito
('3035', 'CLPEES2MXXX'), -- Laboral Kutxa
('3058', 'CCRIES2AXXX'), -- Cajamar
('3025', 'CDENESBBXXX'), -- Caja de Ingenieros (ahora Arquia Banca)
('3190', 'BCOEESMMXXX'), -- Globalcaja (y otras Cajas Rurales asociadas a Banco Cooperativo Español)
('3008', 'BCOEESMMXXX'), -- Caja Rural de Navarra (asociada a Banco Cooperativo Español)
('3187', 'BCOEESMMXXX'), -- Caja Rural del Sur (asociada a Banco Cooperativo Español)
('2085', 'CAZRES2ZXXX'), -- Ibercaja

-- Bancos Online y Neobancos
('1465', 'INGDESMMXXX'), -- ING
('0073', 'OPENESMMXXX'), -- Openbank
('0239', 'EVOBESMMXXX'), -- EVO Banco
('1491', 'TRIOESMMXXX'), -- Triodos Bank
('1563', 'NTSBESM1XXX'), -- N26
('1583', 'REVOESM2XXX'), -- Revolut (para IBANs españoles)
('1573', 'TRWIESM1XXX'); -- Wise (para IBANs españoles)