import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageWhiter {
    public static void main(String args[])throws IOException {
        BufferedImage img = null;
        File f = null;

        try {
            f = new File("foto.jpg");
            img = ImageIO.read(f);
        }
        catch(IOException e) {
            System.out.println(e);
        }

        //get image width and height
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage imgBlanca = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int paso = 100;
        //int promedio = promedioImagen(img);
        for (int j = 0; j < height; j+=paso) {
            for(int i = 0; i < width; i+=paso) {
                emblanquecer(img, imgBlanca, i, j, paso);
            }
        }

        //write image
        try {
            File nuevoF = new File("white.jpg");
            ImageIO.write(imgBlanca, "jpg", nuevoF);
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    public static int to_rgb(int r, int g, int b) {
        return (r<<16) | (g<<8) | b;
    }

    public static int promedioRGB5x5(BufferedImage img, int x, int y) {
        int sumaR = 0, sumaG = 0, sumaB = 0;
        int n = 0;
        for(int i = x; i < x+5 && i < img.getWidth(); i++) {
            for(int j = y; j < y+5 && j < img.getHeight(); j++) {
                n+=1;
                int color = img.getRGB(i, j);

                int r = (color>>16) & 0xff;
                int g = (color>>8) & 0xff;
                int b = color & 0xff;

                sumaR += r;
                sumaG += g;
                sumaB += b;
            }
        }
        return to_rgb(sumaR/n, sumaG/n, sumaB/n);
    }

    public static int promedioRGBnxn(BufferedImage img, int x, int y, int tam) {
        int sumaR = 0, sumaG = 0, sumaB = 0;
        int n = 0;
        for(int i = x; i < x+tam && i < img.getWidth(); i++) {
            for(int j = y; j < y+tam && j < img.getHeight(); j++) {
                n+=1;
                int color = img.getRGB(i, j);

                int r = (color>>16) & 0xff;
                int g = (color>>8) & 0xff;
                int b = color & 0xff;

                sumaR += r;
                sumaG += g;
                sumaB += b;
            }
        }
        return to_rgb(sumaR/n, sumaG/n, sumaB/n);
    }

    public static int promedioImagen(BufferedImage img) {
        int sumaR = 0, sumaG = 0, sumaB = 0;
        int n = 0;
        for(int i = 0; i < img.getWidth(); i++) {
            for(int j = 0; j < img.getHeight(); j++) {
                n+=1;
                int color = img.getRGB(i, j);

                int r = (color>>16) & 0xff;
                int g = (color>>8) & 0xff;
                int b = color & 0xff;

                sumaR += r;
                sumaG += g;
                sumaB += b;
            }
        }
        return to_rgb(sumaR/n, sumaG/n, sumaB/n);
    }


    public static void emblanquecer(BufferedImage original, BufferedImage blanca, int x, int y, int tam) {
        // int promedio = promedioRGB5x5(original, x, y);
        int promedio = promedioRGBnxn(original, x, y, tam);
        int menor = menornxn(original, x, y, tam);
        int mayor = mayornxn(original, x, y, tam);

        int min_r = (menor>>16) & 0xff;
        int min_g = (menor>>8) & 0xff;
        int min_b = menor & 0xff;

        int max_r = (mayor>>16) & 0xff;
        int max_g = (mayor>>8) & 0xff;
        int max_b = mayor & 0xff;

        int prom_r = (promedio>>16) & 0xff;
        int prom_g = (promedio>>8) & 0xff;
        int prom_b = promedio & 0xff;

        float mitad = (prom_r + prom_g + prom_b)/3;

        for(int i = x; i < x+tam && i < original.getWidth(); i++) {
            for(int j = y; j < y+tam && j < original.getHeight(); j++) {

                int color = original.getRGB(i, j);
                int r = (color>>16) & 0xff;
                int g = (color>>8) & 0xff;
                int b = color & 0xff;

                float mitad_pixel = (r + g + b)/3;
                int nuevo = to_rgb(
                    dispersion(r, min_r, max_r),
                    dispersion(g, min_g, max_r),
                    dispersion(b, min_b, max_r));
                // if (mitad_pixel > mitad) {
                //     nuevo = to_rgb(255,255,255);
                //     // System.out.println("blanco");
                // } else {
                //     nuevo = to_rgb(0,0,0);
                // }

                blanca.setRGB(i,j, nuevo);
            }
        }
    }

    public static int menornxn(BufferedImage img, int x, int y, int tam) {
        int menor = to_rgb(255, 255, 255);
        // int min_r = 255;
        // int min_g = 255;
        // int min_b = 255;

        for(int i = x; i < x+tam && i < img.getWidth(); i++) {
            for(int j = y; j < y+tam && j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);

                int r = (color>>16) & 0xff;
                int g = (color>>8) & 0xff;
                int b = color & 0xff;
                if (color < menor) {
                    menor = color;
                }
                // min_r = Math.min(min_r, r);
                // min_g = Math.min(min_g, g);
                // min_b = Math.min(min_b, b);
            }
        }
        return menor;
    }
    public static int mayornxn(BufferedImage img, int x, int y, int tam) {
        int mayor = to_rgb(0, 0, 0);

        // int min_r = 255;
        // int min_g = 255;
        // int min_b = 255;
        for(int i = x; i < x+tam && i < img.getWidth(); i++) {
            for(int j = y; j < y+tam && j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);

                if (color > mayor) {
                    mayor = color;
                }
            }
        }
        return mayor;
    }

    public static int dispersion(int x, int max, int min) {
        return (x - min) * (max - min) * 1/256;
    }
}
