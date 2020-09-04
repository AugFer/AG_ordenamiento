import java.util.ArrayList;
import java.util.List;

public class OrdenDeCajas {
    private List<Integer> cajas = new ArrayList<>();
    private List<Integer> presencia = new ArrayList<>();
    private int areaCubierta;
    private int cajasColocadas;
    private double fitness;
    private int nroEvaluacion;
    private double probabilidad;
    double count = 0; // Usado para test de convergencia probabilidad-elección

    public OrdenDeCajas(int cantCajas) {
        for (int i = 1; i <= cantCajas; i++) {
            cajas.add(i);
        }
        this.areaCubierta = 0;
        this.cajasColocadas = 0;
        this.fitness = 0;
        this.nroEvaluacion = 0;
        this.probabilidad = 0;
    }

    public OrdenDeCajas(List<Integer> ordenDeCajas) {
        cajas.addAll(ordenDeCajas);
        this.areaCubierta = 0;
        this.cajasColocadas = 0;
        this.fitness = 0;
        this.nroEvaluacion = 0;
        this.probabilidad = 0;
    }

    public List<Integer> getCajas() {
        return cajas;
    }

    public List<Integer> getPresencia() {
        return presencia;
    }

    public int getAreaCubierta() {
        return areaCubierta;
    }

    public int getCajasColocadas() {
        return cajasColocadas;
    }

    public double getFitness() {
        return fitness;
    }

    public int getNroEvaluacion() {
        return nroEvaluacion;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    public void setPresencia(int p) {
        this.presencia.add(p);
    }

    public void setAreaCubierta(int areaCubierta) {
        this.areaCubierta = areaCubierta;
    }

    public void setCajasColocadas(int cajasColocadas) {
        this.cajasColocadas = cajasColocadas;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setNroEvaluacion(int nroEvaluacion) {
        this.nroEvaluacion = nroEvaluacion;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }

}