package proyecto3er_Parcial;

import java.awt.geom.Point2D;
import java.awt.Color;

class EsferaRotante extends Esfera3D {

    private double angulo = 0;
    private double radioOrbit;
    private cilindro2 parent;

    public EsferaRotante(double radius, cilindro2 parent, double radioOrbit) {
        super(radius, parent, true, false);
        this.parent = parent;
        this.radioOrbit = radioOrbit; // Inicializar el radio de la órbita
    }

    @Override
    public void run() {
        while (isAnimacionActiva()) {
            angulo += 0.02; // Ajustar la velocidad de rotación aquí
            double[] center = getCenter();
            center[0] = parent.getWidth() / 2 + radioOrbit * Math.cos(angulo);

            // Ajustar la coordenada Z de la esfera para que pase por delante y detrás del cilindro
            double zOffset = Math.sin(angulo) * (parent.getWidth() / 2);
            center[2] = (parent.getHeight() / 2) + zOffset;

            // Asegurar que la coordenada Z se mantenga dentro de los márgenes del JFrame
            center[2] = Math.max(0, Math.min(parent.getHeight(), center[2]));

            parent.repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                //Logger.getLogger(EsferaRotante.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void paintSphere(Graficos3D buffer) {
        int steps = 50;
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double theta = 2 * Math.PI * i / steps;
                double phi = Math.PI * j / steps;
                double x = getRadius() * Math.sin(phi) * Math.cos(theta) + getCenter()[0];
                double y = getRadius() * Math.sin(phi) * Math.sin(theta) + getCenter()[1];
                double z = getRadius() * Math.cos(phi) + getCenter()[2];
                Point2D p = parent.punto3D_a_2D(x, y, z);
                buffer.putPixel((int) p.getX(), (int) p.getY(), z, Color.GREEN);
            }
        }
    }
}





