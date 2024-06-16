package proyecto3er_Parcial;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class cilindro2 extends JPanel implements KeyListener, Runnable {

    private Graficos3D buffer = new Graficos3D(800, 800);

    private double anguloMaximo = 2 * Math.PI;
    private int numPuntos = 40;
    private double anguloIncremento = anguloMaximo / numPuntos;
    private double escala = 50;

    private boolean animacionActiva = false;
    private boolean rellenarCilindro = false; // Bandera para rellenar el cilindro

    private ArrayList<double[]> vertices = new ArrayList<>();
    private double[] puntoCubo = {400, 200, 100};
    private double[] puntoFuga = {400, 400, 1000};

    private double faseOnda = 0;

//    private Esfera3D rotatingSphere;
    private Esfera3D movingSphere;

    // Variable para mantener el estado del movimiento
    private boolean goingUp = true;
    private double anguloRotacionY = 0; // Ángulo de rotación acumulado
    private int direccionRotacion = 0; // 0: sin rotación, -1: izquierda, 1: derecha

    public cilindro2() {
        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.BLACK);

        // Configurar el panel y agregarlo al frame
        this.setBackground(Color.BLACK); // También establecer el fondo negro

        generarVertices();

//        rotatingSphere = new Esfera3D(25, this, true, true); // Rotating sphere with radius 25
        movingSphere = new Esfera3D(30, this, false, false); // Moving sphere with radius 25 and rotating on axis
    }

    private void generarVertices() {
        System.out.println("Generando vértices...");
        vertices.clear();
        for (double alpha = 0; alpha < anguloMaximo; alpha += anguloIncremento) {
            for (double beta = 0; beta < anguloMaximo; beta += anguloIncremento) {
                double[] vertice = new double[3];
                double radio = 2 + Math.cos(alpha + faseOnda); // Añadir la faseOnda aquí
                vertice[0] = radio * Math.cos(beta);
                vertice[2] = radio * Math.sin(beta); // Intercambiamos Z e Y
                vertice[1] = alpha; // Usamos Y para el ángulo alpha
                vertices.add(vertice);
            }
        }
        System.out.println("Vértices generados: " + vertices.size());
        if (vertices.size() != numPuntos * numPuntos) {
            System.out.println("Error: El tamaño de vertices no coincide con numPuntos * numPuntos.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Verificar que vertices no esté vacío antes de acceder a sus elementos
        if (vertices.isEmpty()) {
            System.out.println("El array vertices está vacío.");
            return;
        }

        // Rotar vértices antes de trasladarlos
        rotarVertices();

        double[][] verticesTrasladados = new double[vertices.size()][3];
        for (int i = 0; i < vertices.size(); i++) {
            double[] vertice = vertices.get(i);
            verticesTrasladados[i] = vertice;
        }

        for (int i = 0; i < vertices.size(); i++) {
            double[] v = verticesTrasladados[i];
            double[] trasladado = {
                (v[0] * escala) + puntoCubo[0],
                (v[1] * escala) + puntoCubo[1],
                (v[2] * escala) + puntoCubo[2]
            };
            verticesTrasladados[i] = trasladado;
        }

        if (!rellenarCilindro) {
            // Dibujar la esfera que se mueve primero
            movingSphere.paintSphere(buffer);
        }

        // Dibujar el cilindro con colores del arcoiris
        for (int i = 0; i < numPuntos - 1; i++) {
            for (int j = 0; j < numPuntos; j++) {
                int index0 = i * numPuntos + j;
                int index1 = (i + 1) * numPuntos + j;
                int index2 = i * numPuntos + (j + 1) % numPuntos;
                int index3 = (i + 1) * numPuntos + (j + 1) % numPuntos;

                // Verificar que los índices estén dentro de los límites del array
                if (index0 < verticesTrasladados.length
                        && index1 < verticesTrasladados.length
                        && index2 < verticesTrasladados.length
                        && index3 < verticesTrasladados.length) {

                    double[] v0 = verticesTrasladados[index0];
                    double[] v1 = verticesTrasladados[index1];
                    double[] v2 = verticesTrasladados[index2];
                    double[] v3 = verticesTrasladados[index3];

                    Point2D p0 = punto3D_a_2D(v0[0], v0[1], v0[2]);
                    Point2D p1 = punto3D_a_2D(v1[0], v1[1], v1[2]);
                    Point2D p2 = punto3D_a_2D(v2[0], v2[1], v2[2]);
                    Point2D p3 = punto3D_a_2D(v3[0], v3[1], v3[2]);

                    Color color = ColorUtils.getRainbowColor((float) i / numPuntos);

                    if (rellenarCilindro) {
                        // Rellenar triángulos del cilindro
                        rellenarTriangulo(buffer, (int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), color);
                        rellenarTriangulo(buffer, (int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), (int) p3.getX(), (int) p3.getY(), color);
                    } else {
                        // Dibujar líneas del cilindro
                        buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY(), v0[2], v1[2], color);
                        buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p2.getX(), (int) p2.getY(), v0[2], v2[2], color);
                        buffer.drawLine((int) p1.getX(), (int) p1.getY(), (int) p3.getX(), (int) p3.getY(), v1[2], v3[2], color);
                        buffer.drawLine((int) p2.getX(), (int) p2.getY(), (int) p3.getX(), (int) p3.getY(), v2[2], v3[2], color);
                    }
                } else {
                    System.out.println("Índice fuera de los límites: index0=" + index0 + ", index1=" + index1 + ", index2=" + index2 + ", index3=" + index3);
                }
            }
        }

        // Dibujar la esfera que rota después del cilindro
//        rotatingSphere.paintSphere(buffer);
        g.drawImage(buffer.getBuffer(), 0, 0, null);
        buffer.resetBuffer();
    }

    private void rotarVertices() {
        for (int i = 0; i < vertices.size(); i++) {
            double[] vertice = vertices.get(i);
            vertices.set(i, rotarY(vertice, anguloRotacionY));
        }
    }

    private double[] rotar(double[] punto, double anguloX, double anguloY, double anguloZ) {
        double[] rotadoX = rotarX(punto, anguloX);
        double[] rotadoY = rotarY(rotadoX, anguloY);
        return rotarZ(rotadoY, anguloZ);
    }

    private double[] rotarX(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0];
        result[1] = point[1] * Math.cos(Math.toRadians(angle)) - point[2] * Math.sin(Math.toRadians(angle));
        result[2] = point[1] * Math.sin(Math.toRadians(angle)) + point[2] * Math.cos(Math.toRadians(angle));
        return result;
    }

    private double[] rotarY(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0] * Math.cos(Math.toRadians(angle)) + point[2] * Math.sin(Math.toRadians(angle));
        result[1] = point[1];
        result[2] = -point[0] * Math.sin(Math.toRadians(angle)) + point[2] * Math.cos(Math.toRadians(angle));
        return result;
    }

    private double[] rotarZ(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0] * Math.cos(Math.toRadians(angle)) - point[1] * Math.sin(Math.toRadians(angle));
        result[1] = point[0] * Math.sin(Math.toRadians(angle)) + point[1] * Math.cos(Math.toRadians(angle));
        result[2] = point[2];
        return result;
    }

    private void rellenarTriangulo(Graficos3D buffer, int x0, int y0, int x1, int y1, int x2, int y2, Color color) {
        int[] xs = {x0, x1, x2};
        int[] ys = {y0, y1, y2};
        int ymin = Math.min(y0, Math.min(y1, y2));
        int ymax = Math.max(y0, Math.max(y1, y2));

        for (int y = ymin; y <= ymax; y++) {
            int[] intersections = new int[3];
            int count = 0;
            for (int i = 0; i < 3; i++) {
                int j = (i + 1) % 3;
                if ((ys[i] <= y && y < ys[j]) || (ys[j] <= y && y < ys[i])) {
                    intersections[count++] = xs[i] + (y - ys[i]) * (xs[j] - xs[i]) / (ys[j] - ys[i]);
                }
            }

            if (count == 2) {
                int xstart = Math.min(intersections[0], intersections[1]);
                int xend = Math.max(intersections[0], intersections[1]);
                for (int x = xstart; x <= xend; x++) {
                    buffer.putPixel(x, y, 0, color);
                }
            }
        }
    }

    public Point2D punto3D_a_2D(double x, double y, double z) {
        double u = -puntoFuga[2] / (z - puntoFuga[2]);

        double px = puntoFuga[0] + (x - puntoFuga[0]) * u;
        double py = puntoFuga[1] + (y - puntoFuga[1]) * u;

        return new Point2D.Double(px, py);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();

        switch (key) {
            case KeyEvent.VK_SPACE:
                animacionActiva = !animacionActiva;
                if (animacionActiva) {
                    new Thread(this).start();
//                    rotatingSphere.startAnimation();
                    movingSphere.startAnimation();
                } else {
//                    rotatingSphere.stopAnimation();
                    movingSphere.stopAnimation();
                }
                break;
            case KeyEvent.VK_R:
                rellenarCilindro = !rellenarCilindro;
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                anguloRotacionY -= 1;
                repaint();
                break;
            case KeyEvent.VK_RIGHT:
                anguloRotacionY += 1;
                repaint();
                break;
            case KeyEvent.VK_G:
                if(animacionActiva = false){
                animacionActiva = true; // Iniciar la animación si no está ya activa
                direccionRotacion = (direccionRotacion == 0) ? 1 : direccionRotacion; // Rotar a la derecha si no hay rotación
                new Thread(this).start();
                }else
                direccionRotacion = (direccionRotacion == 0) ? 1 : direccionRotacion; // Rotar a la derecha si no hay rotación
                new Thread(this).start();
                break;
            case KeyEvent.VK_S:
                direccionRotacion = 0; // Detener la rotación
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    /**
     *
     * @param ke
     */
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void run() {
        while (animacionActiva) {
            faseOnda += 0.1; // Incrementar la fase de la onda para crear el efecto de expansión y contracción
            generarVertices(); // Regenerar los vértices con la nueva fase de la onda

            // Calcular el nuevo radio para la esfera en movimiento
            double nuevoRadio = 25 + 10 * Math.sin(faseOnda); // Ajustar los valores según sea necesario
            movingSphere.setRadius(nuevoRadio);

            // Mover la esfera
            if (goingUp) {
                movingSphere.moveUp();
                if (movingSphere.getCenter()[1] <= movingSphere.getRadius()) {
                    goingUp = false;
                }
            } else {
                movingSphere.moveDown();
                if (movingSphere.getCenter()[1] >= getHeight() - movingSphere.getRadius()) {
                    goingUp = true;
                }
            }

//             Actualizar ángulo de rotación basado en la dirección
            if (direccionRotacion != 0) {
                anguloRotacionY += direccionRotacion * 2; // Ajustar velocidad de rotación según sea necesario
            }

            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                // Logger.getLogger(cilindro2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Color getRainbowColor(float ratio) {
        // ratio is between 0 and 1
        int hue = (int) (ratio * 360); // hue from 0 to 360
        return Color.getHSBColor(hue / 360f, 1.0f, 1.0f); // full saturation and brightness
    }
}
