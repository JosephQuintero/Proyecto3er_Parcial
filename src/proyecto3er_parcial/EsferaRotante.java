package proyecto3er_Parcial;

import java.awt.geom.Point2D;
import java.awt.Color;

class EsferaRotante extends Esfera3D {

    private double angulo = 0;
    private double radioOrbit;
    private cilindro2 parent;
    private boolean rellenarEsfera = false;

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
        if (rellenarEsfera) {
            for (int i = 0; i < steps; i++) {
                for (int j = 0; j < steps; j++) {
                    double theta1 = 2 * Math.PI * i / steps;
                    double theta2 = 2 * Math.PI * (i + 1) / steps;
                    double phi1 = Math.PI * j / steps;
                    double phi2 = Math.PI * (j + 1) / steps;

                    double[] p0 = {getRadius() * Math.sin(phi1) * Math.cos(theta1) + getCenter()[0], getRadius() * Math.sin(phi1) * Math.sin(theta1) + getCenter()[1], getRadius() * Math.cos(phi1) + getCenter()[2]};
                    double[] p1 = {getRadius() * Math.sin(phi2) * Math.cos(theta1) + getCenter()[0], getRadius() * Math.sin(phi2) * Math.sin(theta1) + getCenter()[1], getRadius() * Math.cos(phi2) + getCenter()[2]};
                    double[] p2 = {getRadius() * Math.sin(phi1) * Math.cos(theta2) + getCenter()[0], getRadius() * Math.sin(phi1) * Math.sin(theta2) + getCenter()[1], getRadius() * Math.cos(phi1) + getCenter()[2]};
                    double[] p3 = {getRadius() * Math.sin(phi2) * Math.cos(theta2) + getCenter()[0], getRadius() * Math.sin(phi2) * Math.sin(theta2) + getCenter()[1], getRadius() * Math.cos(phi2) + getCenter()[2]};

                    Point2D point0 = parent.punto3D_a_2D(p0[0], p0[1], p0[2]);
                    Point2D point1 = parent.punto3D_a_2D(p1[0], p1[1], p1[2]);
                    Point2D point2 = parent.punto3D_a_2D(p2[0], p2[1], p2[2]);
                    Point2D point3 = parent.punto3D_a_2D(p3[0], p3[1], p3[2]);

                    parent.rellenarTriangulo(buffer, (int) point0.getX(), (int) point0.getY(), (int) point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY(), Color.GREEN);
                    parent.rellenarTriangulo(buffer, (int) point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY(), (int) point3.getX(), (int) point3.getY(), Color.GREEN);
                }
            }
        } else {
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

    public void toggleRellenarEsfera() {
        rellenarEsfera = !rellenarEsfera;
    }
}
