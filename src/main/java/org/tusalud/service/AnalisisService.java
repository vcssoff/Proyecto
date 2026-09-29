package org.tusalud.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.tusalud.model.Medicion;
import org.tusalud.model.Recomendacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AnalisisService implements Promedio {

    @Override
    public double calcularCardiaco(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) return 0.0;
        int count = 0;
        double sum = 0;
        for (Medicion m : mediciones) {
            if (m.getFrecuenciaCardiaca() != null) {
                sum += m.getFrecuenciaCardiaca();
                count++;
            }
        }
        return count > 0 ? (sum / count) : 0.0;
    }

    @Override
    public double calcularGlucosa(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) return 0.0;
        int count = 0;
        double sum = 0;
        for (Medicion m : mediciones) {
            if (m.getGlucosaSangre() != null) {
                sum += m.getGlucosaSangre();
                count++;
            }
        }
        return count > 0 ? (sum / count) : 0.0;
    }

    @Override
    public double calcularIMC(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) return 0.0;
        // Tomar la medición más reciente que contenga peso y altura válidos
        for (int i = mediciones.size() - 1; i >= 0; i--) {
            Medicion m = mediciones.get(i);
            if (m.getPeso() != null && m.getAltura() != null && m.getAltura() > 0) {
                return m.getPeso() / (m.getAltura() * m.getAltura());
            }
        }
        return 0.0;
    }

    public List<Recomendacion> generarRecomendaciones(int idUsuario, List<Medicion> mediciones) {
        List<Recomendacion> recs = new ArrayList<>();
        if (mediciones == null || mediciones.isEmpty()) {
            recs.add(new Recomendacion(idUsuario, null, "GENERAL", "Comienza registrando tus mediciones biométricas para recibir un análisis personalizado."));
            return recs;
        }

        double imc = calcularIMC(mediciones);
        if (imc > 0) {
            if (imc < 18.5) {
                recs.add(new Recomendacion(idUsuario, null, "IMC", String.format("Tu IMC es %.1f (Bajo peso). Considera una consulta nutricional para un plan calórico adecuado.", imc)));
            } else if (imc < 24.9) {
                recs.add(new Recomendacion(idUsuario, null, "IMC", String.format("Tu IMC es %.1f (Peso normal). ¡Excelente! Mantén tus hábitos saludables.", imc)));
            } else if (imc < 29.9) {
                recs.add(new Recomendacion(idUsuario, null, "IMC", String.format("Tu IMC es %.1f (Sobrepeso). Se recomienda actividad física regular y cuidar el balance calórico.", imc)));
            } else {
                recs.add(new Recomendacion(idUsuario, null, "IMC", String.format("Tu IMC es %.1f (Obesidad). Consulta a un profesional de la salud para seguimiento.", imc)));
            }
        }

        double fcPromedio = calcularCardiaco(mediciones);
        if (fcPromedio > 0) {
            if (fcPromedio < 60) {
                recs.add(new Recomendacion(idUsuario, null, "CARDIACO", String.format("Frecuencia cardíaca promedio baja (%.0f bpm). Si no eres atleta de alto rendimiento, consulta a tu médico.", fcPromedio)));
            } else if (fcPromedio <= 100) {
                recs.add(new Recomendacion(idUsuario, null, "CARDIACO", String.format("Frecuencia cardíaca promedio normal (%.0f bpm en reposo). Ritmo adecuado.", fcPromedio)));
            } else {
                recs.add(new Recomendacion(idUsuario, null, "CARDIACO", String.format("Frecuencia cardíaca promedio elevada (%.0f bpm). Intenta reducir fuentes de estrés o consulta al cardiólogo.", fcPromedio)));
            }
        }

        double glucosaPromedio = calcularGlucosa(mediciones);
        if (glucosaPromedio > 0) {
            if (glucosaPromedio < 70) {
                recs.add(new Recomendacion(idUsuario, null, "GLUCOSA", String.format("Glucosa promedio baja (%.1f mg/dL). Monitorea posibles síntomas de hipoglucemia.", glucosaPromedio)));
            } else if (glucosaPromedio <= 100) {
                recs.add(new Recomendacion(idUsuario, null, "GLUCOSA", String.format("Glucosa promedio normal en ayunas (%.1f mg/dL). Niveles saludables.", glucosaPromedio)));
            } else if (glucosaPromedio <= 125) {
                recs.add(new Recomendacion(idUsuario, null, "GLUCOSA", String.format("Glucosa promedio en rango de prediabetes (%.1f mg/dL). Cuida la ingesta de azúcares refinados.", glucosaPromedio)));
            } else {
                recs.add(new Recomendacion(idUsuario, null, "GLUCOSA", String.format("Glucosa promedio elevada (%.1f mg/dL). Es importante consultar a tu médico para una evaluación.", glucosaPromedio)));
            }
        }

        return recs;
    }

    public JFreeChart generarGraficaEvolucion(List<Medicion> mediciones) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

        for (Medicion m : mediciones) {
            String fecha = sdf.format(m.getFechaHora());
            if (m.getPeso() != null) {
                dataset.addValue(m.getPeso(), "Peso (kg)", fecha);
            }
            if (m.getFrecuenciaCardiaca() != null) {
                dataset.addValue(m.getFrecuenciaCardiaca(), "Pulsaciones (bpm)", fecha);
            }
            if (m.getGlucosaSangre() != null) {
                dataset.addValue(m.getGlucosaSangre(), "Glucosa (mg/dL)", fecha);
            }
        }

        return ChartFactory.createLineChart(
                "Evolución de Mediciones Biométricas",
                "Fecha",
                "Valor",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }
}
