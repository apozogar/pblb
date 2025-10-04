// generar-retorno.js

const fs = require('fs').promises;
const { parseStringPromise } = require('xml2js');
const { create } = require('xmlbuilder2');
const yargs = require('yargs/yargs');
const { hideBin } = require('yargs/helpers');

// --- Configuración de Argumentos ---
const argv = yargs(hideBin(process.argv))
.option('input', {
  alias: 'i',
  description: 'Ruta al fichero de remesa SEPA (pain.008) de entrada',
  type: 'string',
  demandOption: true,
})
.option('output', {
  alias: 'o',
  description: 'Ruta donde se guardará el fichero de retorno (camt.054)',
  type: 'string',
  default: 'fichero_retorno.xml',
})
.option('rate', {
  alias: 'r',
  description: 'Tasa de rechazo (un número entre 0 y 1, ej: 0.3 para 30%)',
  type: 'number',
  default: 0.3,
})
.help()
.alias('help', 'h')
    .argv;

// --- Lógica Principal ---
async function generarFicheroRetorno() {
  try {
    console.log(`Leyendo fichero de remesa: ${argv.input}`);
    const remesaXml = await fs.readFile(argv.input, 'utf-8');

    const remesaJson = await parseStringPromise(remesaXml);
    const transacciones = remesaJson.Document.CstmrDrctDbtInitn[0].PmtInf[0].DrctDbtTxInf;

    console.log(`Se encontraron ${transacciones.length} transacciones en la remesa.`);

    const transaccionesRechazadas = transacciones.filter(() => {
      return Math.random() < argv.rate; // Decide aleatoriamente si se rechaza
    });

    if (transaccionesRechazadas.length === 0) {
      console.log('¡Qué suerte! Ninguna transacción fue rechazada en esta simulación.');
      return;
    }

    console.log(`Simulando rechazo de ${transaccionesRechazadas.length} transacciones.`);

    // Construimos el XML de retorno (formato camt.054 simplificado)
    const root = create({ version: '1.0', encoding: 'UTF-8' })
    .ele('Document', { xmlns: 'urn:iso:std:iso:20022:tech:xsd:camt.054.001.04' });

    const ntfctn = root.ele('BkToCstmrDbtCdtNtfctn').ele('Ntfctn');

    for (const tx of transaccionesRechazadas) {
      const endToEndId = tx.PmtId[0].EndToEndId[0];
      const importe = tx.InstdAmt[0]._;
      const nombre = tx.Dbtr[0].Nm[0];

      ntfctn.ele('Ntry')
      .ele('NtryDtls')
      .ele('TxDtls')
      .ele('Refs')
      .ele('EndToEndId').txt(endToEndId).up() // ID de la cuota
      .up()
      .ele('Amt').txt(importe).up()
      .ele('RmtInf')
      .ele('Ustrd').txt(`Devolución de recibo para ${nombre}`).up();

      console.log(`  -> Rechazando cuota: ${endToEndId} (${nombre})`);
    }

    const retornoXml = root.end({ prettyPrint: true });

    await fs.writeFile(argv.output, retornoXml);
    console.log(`\n¡Éxito! Fichero de retorno generado en: ${argv.output}`);

  } catch (error) {
    console.error('Error al generar el fichero de retorno:', error.message);
  }
}

generarFicheroRetorno();