package Reports;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
/**
 *
 * @author Gabriel
 */
public class Reportes extends Datos {
    
    public void ejecutarReporte(String ruta, Map parametros) throws Exception {
        try {
            String archivo = ruta;
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                System.out.println("No se encuentra el archivo.");
                System.exit(2);
            }
            InputStream reporte = null;
            try {
                reporte = this.getClass().getClassLoader().getResourceAsStream(ruta);
            } catch (Exception e) {
                System.out.println("Error cargando el reporte => " + e.getMessage());
                System.exit(3);
            }
            Map parametro = parametros;        
            Abrir();
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametro, conexion);
            Cerrar();
            //Se lanza el Viewerde Jasper, no termina aplicación al salir
            JasperViewer jviewer = new JasperViewer(jasperPrint, false); 
            JasperExportManager.exportReportToPdfFile(jasperPrint,"E:\\reporte.pdf");
            //Abriendo Fichero pdf desde Java
            File path = new File("E:\\reporte.pdf");
            Desktop.getDesktop().open(path);
            ///////////////////////////////////
            jviewer.setAlwaysOnTop(true);
            jviewer.setVisible(false);
        } catch (Exception j) {
            System.out.println("Mensaje de Error:" + j.getMessage());
            JOptionPane.showMessageDialog(null,"Debe cerrar el PDF de reporte para poder visualizar el siguiente ");
        }
    }
    
    public void ejecutarReporte(String ruta, Map parametros,JRBeanCollectionDataSource coleccion) throws Exception {       
        try {
            String archivo = ruta;
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                System.out.println("No se encuentra el archivo.");
                System.exit(2);
            }
            InputStream reporte = null;
            try {
                reporte = this.getClass().getClassLoader().getResourceAsStream(ruta);
            } catch (Exception e) {
                System.out.println("Error cargando el reporte => "+e.getMessage());
                System.exit(3);
            }           
            Map parametro = parametros;
            JasperPrint jasperPrint;
            jasperPrint = JasperFillManager.fillReport(reporte, parametro, coleccion);
            //Se lanza el Viewerde Jasper, no termina aplicación al salir
            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setAlwaysOnTop(true);
            jviewer.setVisible(true);
        } catch (Exception j) {
            System.out.println("Mensaje de Error:" + j.getMessage());
        }
    }
}
