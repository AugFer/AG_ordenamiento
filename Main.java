import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static java.lang.Math.random;

public class Main {
    private static int[][] cargarCajas() {
        ArrayList<Integer> c_alto = new ArrayList<>();
        ArrayList<Integer> c_largo = new ArrayList<>();
        try {
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Augus\\Desktop/Instancia.txt"), StandardCharsets.UTF_8));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Augus\\IdeaProjects\\CorteBidimensional\\src/Instancia.txt"), StandardCharsets.UTF_8));
            String linea;
            br.readLine();
            while((linea = br.readLine()) != null){
                String[] medidas = linea.split(",");
                c_largo.add(Integer.parseInt(medidas[0]));
                c_alto.add(Integer.parseInt(medidas[1]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[][] cajas = new int[c_alto.size()][2]; // filas (cajas) y columnas (propiedades: 0 - alto, 1 - largo)

        for (int i = 0; i < cajas.length; i++) {
            cajas[i][0] = c_alto.get(i);
            cajas[i][1] = c_largo.get(i);
        }

        return cajas;
    }

    private static int[] crearMascara(int dimension) {
        int[] mascara = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            if(random() > 0.5) {
                mascara[i] = 1;
            }
            else {
                mascara[i] = 0;
            }
        }
        return mascara;
    }

    private static void generarPoblacion(List<OrdenDeCajas> poblacion, int tamPoblacion, int cantCajas) {
        // Crear un individuo base con la cantidad de cortes, mezclar 100 veces y se agregar a la poblacion
        for (int i = 0; i < tamPoblacion; i++) {
            OrdenDeCajas r = new OrdenDeCajas(cantCajas);
            for (int j = 0; j < 100; j++) {
                Collections.shuffle(r.getCajas());
            }
            poblacion.add(r);
        }
    }

    private static void fitness(List<OrdenDeCajas> poblacion, int altoSuperficie, int largoSuperficie, int[][] cajas, int individuosAnalizados, OrdenDeCajas mejorOrden) {
        double sumFitness = 0;
        for (OrdenDeCajas actual : poblacion) { // Recorrer los ordenes de corte de la poblacion
            int[][] tela = new int[altoSuperficie][largoSuperficie];
            int cortesPosibles = 0;
            int areaCubierta = 0;
            int filaUltimoCorte = 0;
            for (int c = 0; c < cajas.length; c++) { // Recorrer los cajas de una orden
                int corte = actual.getCajas().get(c);
                int corteAlto = cajas[corte - 1][0];
                int corteLargo = cajas[corte - 1][1];
                boolean comprobacion = false;
                for (int fila = 0; fila < tela.length && !comprobacion; fila++) { // Recorrer la matriz buscando dónde colocar el corte
                    for (int columna = 0; columna < tela[0].length; columna++) {
                        if (tela[fila][columna] == 0 && fila >= filaUltimoCorte) {
                            if (comprobarEspacio(corteAlto, corteLargo, fila, columna, tela)) {
                                filaUltimoCorte = fila;
                                comprobacion = true;
                                cortesPosibles++;
                                if (actual.getPresencia().size() != cajas.length) {
                                    actual.setPresencia(1);
                                }
                                areaCubierta += (corteAlto * corteLargo);
                                for (int j = fila; j < (fila + corteAlto); j++) { // Recorrer la matriz llendo las posiciones que ocupa el corte
                                    for (int k = columna; k < (columna + corteLargo); k++) {
                                        tela[j][k] = corte;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if (!comprobacion) {
                    if (actual.getPresencia().size() != cajas.length) {
                        actual.setPresencia(0);
                    }
                }
            }
            // Asignar contadores de Cortes Posibles y Area Cubierta
            actual.setAreaCubierta(areaCubierta);
            actual.setCajasColocadas(cortesPosibles);
            if(!actual.equals(mejorOrden)){
                individuosAnalizados++;
                actual.setNroEvaluacion(individuosAnalizados);
            }
            // Calcular fitness
            double fitness = cortesPosibles * ((double) areaCubierta / (altoSuperficie * largoSuperficie)); // Fitness normal
            //double fitness = Math.pow(cortesPosibles * ((double) areaCubierta / (altoSuperficie * largoSuperficie)), 5); // Fitness mejorado
            actual.setFitness(fitness);
            sumFitness += fitness;
        }
        // Calcular probabilidad de selección
        for (OrdenDeCajas actual : poblacion) {
            actual.setProbabilidad(actual.getFitness() / sumFitness);
        }
    }

    private static boolean comprobarEspacio(int cajaAlto, int cajaLargo, int filaInicial, int columnaInicial, int[][] superficie) {
        boolean largo = true;
        if (cajaLargo <= (superficie[0].length - columnaInicial)) {
            for (int k = 1; k < cajaLargo; k++) {
                if (superficie[filaInicial][columnaInicial + k] != 0) {
                    largo = false;
                    break;
                }
            }
        }
        else {
            largo = false;
        }

        boolean alto = true;
        if (cajaAlto <= (superficie.length - filaInicial)) {
            for (int k = 1; k < cajaAlto; k++){
                if (superficie[filaInicial + k][columnaInicial] != 0) {
                    alto = false;
                    break;
                }
            }
        }
        else {
            alto = false;
        }

        return largo && alto;
    }

    private static OrdenDeCajas buscarMejor(List<OrdenDeCajas> poblacion, OrdenDeCajas mejorOrden, int generacion, int altoTela, int largoTela) {
        boolean encontrado = false;
        for (OrdenDeCajas actual : poblacion) {
            if (actual.getFitness() > mejorOrden.getFitness()) {
                mejorOrden = actual;
                encontrado = true;
            }
        }
        if (encontrado) {
            //imprimirResumen(generacion, mejorOrden, altoTela, largoTela);
        }
        return mejorOrden;
    }

    private static List<Integer> seleccion(List<OrdenDeCajas> poblacion) {
        int index = 0;
        double r = random();

        while (r > 0) {
            r = r - poblacion.get(index).getProbabilidad();
            if (r > 0 ) {
                index++;
            }
        }

        // Test de convergencia entre probabilidad de ser elegido y ocurrencias de selección
        //poblacion.get(index).count++;

        return poblacion.get(index).getCajas();
    }

    private static void orderCrossover(List<Integer> padreA, List<Integer> padreB, List<Integer> hijo) {
        Random r = new Random();
        int indiceCross1 = r.nextInt(padreA.size() - 1); // Generar indice entre [0, (padre.size - 1)]
        int indiceCross2;
        do {
            indiceCross2 = r.nextInt(padreA.size() - 1);
        } while (indiceCross2 == indiceCross1);

        if (indiceCross1 > indiceCross2) {
            int temp = indiceCross2;
            indiceCross2 = indiceCross1;
            indiceCross1 = temp;
        }

        // Tomar la sección entre los indices (incluidos) del PadreA y colocarla al inicio del hijo
        for (int i = indiceCross1; i <= indiceCross2; i++) {
            hijo.add(padreA.get(i));
        }

        // Recorrer PadreB y se agregar los cortes faltantes segun su orden de aparición
        for (Integer gen : padreB) {
            if (!hijo.contains(gen)) {
                hijo.add(gen);
            }
        }
    }

    private static void maskedCrossover(List<Integer> padreA, List<Integer> padreB, List<Integer> hijo, int[] mascara) {
        for (int i = 0; i < mascara.length; i++) {
            if(mascara[i] == 1) {
                for (int j = 0; j <= padreA.size() ; j++) {
                    if (!hijo.contains(padreA.get(j))) {
                        hijo.add(padreA.get(j));
                        break;
                    }
                }
            }
            else {
                for (int j = 0; j <= padreB.size(); j++) {
                    if (!hijo.contains(padreB.get(j))) {
                        hijo.add(padreB.get(j));
                        break;
                    }
                }
            }
        }
    }

    private static void mutacion(List<Integer> hijo, double probMutacion) {
        boolean mutado = false; // Bandera que permite que mute como máximo un alelo
        int permutar;
        int temp;
        Random r = new Random();
        for (int j = 0; j < hijo.size() && !mutado; j++) {
            if (random() <= probMutacion) {
                mutado = true;
                permutar = r.nextInt(hijo.size() - 1); // Generar indice entre [0, (padre.size - 1)]
                temp = hijo.get(j);
                hijo.set(j, hijo.get(permutar));
                hijo.set(permutar, temp);
            }
        }
    }

    private static void imprimirPoblacion(List<OrdenDeCajas> poblacion, int generacion) {
        double sumaP = 0;
        System.out.println("Generación " + generacion);
        for (OrdenDeCajas actual : poblacion) {
            System.out.println("Orden: " + actual.getCajas());
            System.out.println(" - Fitness: " + actual.getFitness());
            //System.out.println(" - Fitness: " + Math.pow(actual.getFitness(), 1.0 / 4));
            System.out.println(" - Probabilidad: " + actual.getProbabilidad());
            sumaP += actual.getProbabilidad();
        }
        System.out.println("Suma de probabilidades: " + sumaP);
        System.out.println();
    }

    private static void imprimirResumen(int generacion, OrdenDeCajas mejorOrden, int altoSuperficie, int largoSuperficie) {
        System.out.println("Generación: " + generacion);
        System.out.println("Mejor aproximación");
        System.out.println(" - Fitness: " + mejorOrden.getFitness());
        //System.out.println(" - Fitness: " + Math.pow(mejorOrden.getFitness(), 1.0 / 4));
        System.out.println(" - Cajas colocadas: " + mejorOrden.getCajasColocadas());
        System.out.println(" - Area cubierta: " + mejorOrden.getAreaCubierta() + "/" + (altoSuperficie * largoSuperficie) + " (" + ((mejorOrden.getAreaCubierta() * 100) / (altoSuperficie * largoSuperficie)) + "%)");
        System.out.println();
    }

    private static void imprimirFinal(List<OrdenDeCajas> poblacion, int generacion, OrdenDeCajas mejorOrden, int generacionMejorOrden, int altoSuperficie, int largoSuperficie, int[][] cortes) {
        System.out.println("----------------------------------");
        System.out.println("Generaciones analizadas: " + generacion);
        System.out.println("Individuos analizados: " + (poblacion.size() * generacion));
        System.out.println("Cajas requeridas: " + Arrays.deepToString(cortes));
        System.out.println("----------------------------------");
        System.out.println("Mejor aproximación");
        System.out.println(" - Origen: generación " + generacionMejorOrden);
        System.out.println(" - Fitness: " + mejorOrden.getFitness());
        //System.out.println(" - Fitness: " + Math.pow(mejorOrden.getFitness(), 1.0 / 4));
        System.out.println(" - Orden de cajas:  " + mejorOrden.getCajas());
        System.out.println(" - Presencia:       " + mejorOrden.getPresencia());
        System.out.println(" - Cajas colocadas: " + mejorOrden.getCajasColocadas());
        System.out.println(" - Area cubierta: " + mejorOrden.getAreaCubierta() + "/" + (altoSuperficie * largoSuperficie) + " (" + ((mejorOrden.getAreaCubierta() * 100) / (altoSuperficie * largoSuperficie)) + "%)");
        System.out.println(" - Distribución: ");
        int[][] superficie = new int[altoSuperficie][largoSuperficie];
        for (int c = 0; c < mejorOrden.getCajas().size(); c++) { // Recorrer los cortes
            int caja = mejorOrden.getCajas().get(c);
            int cajaAlto = cortes[caja - 1][0];
            int cajaLargo = cortes[caja - 1][1];
            boolean comprobacion = false;
            for (int fila = 0; fila < superficie.length && !comprobacion; fila++) { // Recorrer la matriz buscando donde colocar el caja
                for (int columna = 0; columna < superficie[0].length; columna++) {
                    if (superficie[fila][columna] == 0) {
                        if (comprobarEspacio(cajaAlto, cajaLargo, fila, columna, superficie)) {
                            comprobacion = true;
                            for (int j = fila; j < (fila + cajaAlto); j++) { // Recorrer la matriz llendo las posiciones que ocupa el caja
                                for (int k = columna; k < (columna + cajaLargo); k++) {
                                    superficie[j][k] = caja;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        // Imprimir la matriz con la disposición de cortes
        for (int[] fila : superficie) {
            System.out.println();
            System.out.print("  ");
            for (int corte : fila) {
                System.out.printf("%3d", corte);
                System.out.print(" | ");
            }
        }
        System.out.println();
        System.out.println();
    }

    private static void testProbabilidad(List<OrdenDeCajas> poblacion) {
        for (int i = 0; i < 10000000; i++) {
            List<Integer> test = seleccion(poblacion);
        }
        for (OrdenDeCajas actual : poblacion) {
            System.out.println("Orden: " + actual.getCajas());
            System.out.println(" - Probabilidad     : " + actual.getProbabilidad());
            System.out.println(" - Contador/10000000: " + actual.count / 10000000);
        }
    }

    private static void escribirResultados(PrintWriter pw, OrdenDeCajas mejorOrden, int generacionMejorOrden, long t_inicio, long t_fin) {
        pw.print(String.format("%.3f",(t_fin - t_inicio) / 1000d) + "\t");
        pw.print(String.format("%.5f", mejorOrden.getFitness()) + "\t");
        //pw.print(String.format("%.5f", Math.pow(mejorOrden.getFitness(),1.0/5)) + "\t");
        pw.print(generacionMejorOrden + "\t");
        pw.print(mejorOrden.getNroEvaluacion() + "\t");
        pw.println(mejorOrden.getCajasColocadas());
    }

    public static void main(String[] args) throws FileNotFoundException {
        // - - - - - - \\ Variables  // - - - - - - \\

        int altoSuperficie = 27;
        int largoSuperficie = 5;
        int tamPoblacion = 200;
        int limiteAnalisis = 30000;
        int crossover = 2; // 1 = Order Crossover (Modificado) con dos puntos de corte, 2 = Masked Crossover
        double probMutacion = 0.05;

        // - - - - - - - - - \\// - - - - - - - - - \\

        PrintWriter pw = new PrintWriter(new File("C:\\Users\\Augus\\IdeaProjects\\CorteBidimensional\\src/Resultados.txt"));
        pw.print("Tiempo Ejecución" + "\t");
        pw.print("Mejor Fitness" + "\t");
        pw.print("Generacion" + "\t");
        pw.print("Evaluación" + "\t");
        pw.println("Cajas Colocadas");

        int[][] cajas = cargarCajas();
        int[] mascara = crearMascara(cajas.length);
        int corridas = 1;
        while(corridas <= 30) {
            int generacion = 1;
            int generacionMejorOrden = 1;
            int individuosAnalizados = 0;
            List<OrdenDeCajas> poblacion = new ArrayList<>();
            List<OrdenDeCajas> nuevaPoblacion;
            List<Integer> padreA, padreB, hijo;
            OrdenDeCajas nuevoOrden, resultadoBM;
            OrdenDeCajas mejorOrden = new OrdenDeCajas(cajas.length); // Inicializar mejorOrden con un dummy (fitness = 0)
            long t_inicio = System.currentTimeMillis();

            // Generar poblacion aleatoria inicial
            generarPoblacion(poblacion, tamPoblacion, cajas.length);

            // Calcular fitness de poblacion inicial
            fitness(poblacion, altoSuperficie, largoSuperficie, cajas, individuosAnalizados, mejorOrden);
            individuosAnalizados += tamPoblacion;

            // Buscar mejor individuo incial
            resultadoBM = buscarMejor(poblacion, mejorOrden, generacion, altoSuperficie, largoSuperficie);
            mejorOrden = resultadoBM;

            // Imprimir poblacion inicial
            //imprimirPoblacion(poblacion, generacion);

            // Iniciar proceso evolutivo
            while (individuosAnalizados < limiteAnalisis) {
                generacion++;
                nuevaPoblacion = new ArrayList<>();
                nuevaPoblacion.add(mejorOrden);
                for (int i = 0; i < (tamPoblacion - 1); i++) { // -1 porque un lugar ya lo ocupa el mejor individuo de la generacion anterior
                    // Selección
                    padreA = seleccion(poblacion);
                    padreB = seleccion(poblacion);

                    // Crossover
                    hijo = new ArrayList<>();
                    if (crossover == 1) {
                        orderCrossover(padreA, padreB, hijo);
                    } else {
                        maskedCrossover(padreA, padreB, hijo, mascara);
                    }

                    // Mutación
                    mutacion(hijo, probMutacion);

                    // Crear y añadir el nuevo individuo en la nueva población
                    nuevoOrden = new OrdenDeCajas(hijo);
                    nuevaPoblacion.add(nuevoOrden);
                }
                // Calcular fitness de nueva poblacion
                fitness(nuevaPoblacion, altoSuperficie, largoSuperficie, cajas, individuosAnalizados, mejorOrden);
                individuosAnalizados += tamPoblacion;

                // Buscar mejor individuo en nueva poblacion
                resultadoBM = buscarMejor(nuevaPoblacion, mejorOrden, generacion, altoSuperficie, largoSuperficie);
                if (!resultadoBM.equals(mejorOrden)) {
                    mejorOrden = resultadoBM;
                    generacionMejorOrden = generacion;
                }

                // Reemplazar poblacion anterior por nueva poblacion
                poblacion = nuevaPoblacion;
            }
            // Imprimir población final
            //imprimirPoblacion(poblacion, generacion);

            //Imprimir en detalle la mejor solución alcanzada
            //imprimirFinal(poblacion, generacion, mejorOrden, generacionMejorOrden, altoSuperficie, largoSuperficie, cajas);

            // Imprimir tiempo de ejecución
            long t_fin = System.currentTimeMillis();
            System.out.println("Tiempo de ejecución: " + (t_fin - t_inicio) / 1000d + " segundos");

            //Guardar los resultados de la corrida actual
            escribirResultados(pw, mejorOrden, generacionMejorOrden, t_inicio, t_fin);
            corridas++;
        }
        pw.close();

        // Test de probabilidad de eleccion y ocurrencias del metodo seleccion
        //testProbabilidad(poblacion);
    }
}