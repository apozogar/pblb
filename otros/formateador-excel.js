const xlsx = require('xlsx');
const path = require('path');

// --- CONFIGURACIÓN ---
// Nombre del fichero de entrada (debe estar en la misma carpeta que el script)
const inputFile = 'Remesa nuevos socios.xlsx';
// Nombre de la columna que contiene los IBAN (sensible a mayúsculas/minúsculas)
const ibanColumnName = 'Domiciliación Bancaria (introduce nombre del banco e IBAN)';
// Nombre del fichero de salida que se generará
const outputFile = 'Remesa nuevos socios_corregido.xlsx';
// --------------------

// Construye las rutas completas a los ficheros
const inputFilePath = path.join(__dirname, inputFile);
const outputFilePath = path.join(__dirname, outputFile);

try {
    // 1. Leer el fichero Excel
    const workbook = xlsx.readFile(inputFilePath);
    const sheetName = workbook.SheetNames[0]; // Usamos la primera hoja del libro
    const worksheet = workbook.Sheets[sheetName];

    // 2. Convertir la hoja a un formato más manejable (JSON)
    const data = xlsx.utils.sheet_to_json(worksheet);

    let ibanColumnIndex = -1;

    // 3. Encontrar el índice de la columna IBAN y procesar los datos
    if (data.length > 0) {
        const headers = Object.keys(data[0]);
        const headerCell = Object.keys(worksheet).find(
            key => worksheet[key].v === ibanColumnName);

        if (!headerCell) {
            throw new Error(
                `No se encontró la columna "${ibanColumnName}". Revisa el nombre en la configuración.`);
        }

        // Procesamos cada fila
        data.forEach(row => {
            const ibanValue = row[ibanColumnName];
            if (ibanValue) {
                // Convertimos el valor a string para poder procesarlo
                const cellText = String(ibanValue);
                // PRIMERO, limpiamos todo el texto de espacios y guiones para facilitar la búsqueda.
                const cleanedText = cellText.replace(/[\s-]/g, '');

                // Usamos una expresión regular para encontrar el IBAN dentro del texto.
                // Busca un patrón que empiece con 2 letras y siga con números y letras (típico de un IBAN).
                const ibanMatch = cellText.match(/\b([A-Z]{2}\d{2}[A-Z0-9\s-]{10,30})\b/i);

                if (ibanMatch) {
                    // El texto encontrado ya está limpio, solo lo ponemos en mayúsculas.
                    row[ibanColumnName] = ibanMatch[0].replace(/[\s-]/g, '').toUpperCase();
                } else {
                    // Opcional: Si no se encuentra un IBAN, se limpia la celda.
                    row[ibanColumnName] = '';
                }
            }
        });
    }

    // 4. Crear una nueva hoja de cálculo con los datos corregidos
    const newWorksheet = xlsx.utils.json_to_sheet(data);

    // 5. Forzar el formato de la columna IBAN a Texto ('@')
    // Esto es crucial para que Excel no lo interprete como número
    const range = xlsx.utils.decode_range(newWorksheet['!ref']);
    const headerIndex = Object.keys(data[0]).indexOf(ibanColumnName);

    if (headerIndex !== -1) {
        for (let R = range.s.r + 1; R <= range.e.r; ++R) { // Empezamos en +1 para saltar la cabecera
            const cellAddress = xlsx.utils.encode_cell({c: headerIndex, r: R});
            if (newWorksheet[cellAddress]) {
                newWorksheet[cellAddress].t = 's'; // 's' significa tipo string
            }
        }
    }

    // 6. Crear un nuevo libro de trabajo y guardar el fichero
    const newWorkbook = xlsx.utils.book_new();
    xlsx.utils.book_append_sheet(newWorkbook, newWorksheet, sheetName);
    xlsx.writeFile(newWorkbook, outputFilePath);

    console.log(`¡Proceso completado! ✨`);
    console.log(`Fichero corregido guardado como: ${outputFile}`);

} catch (error) {
    console.error('❌ Error al procesar el fichero:', error.message);
    if (error.code === 'ENOENT') {
        console.error(
            `Asegúrate de que el fichero "${inputFile}" existe en la misma carpeta que el script.`);
    }
}