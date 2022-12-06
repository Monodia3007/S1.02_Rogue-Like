class Main extends Program {
    void initialiserDonjon () {

    }

    String RGBToANSI(int[] rgb, boolean backgroundColor) {
        return "\u001b[" + (backgroundColor ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
    }

    void algorithm () {
        int largeur = 0, longueur = 0;
        println("Quelle taille de donjon voulez vous ?");
        while (largeur <= 1 && longueur <= 1) {
            print("Longueur : ");
            longueur = readInt();
            print("Largeur : ");
            largeur = readInt();
        }
        int[][] donjon = new int[longueur][largeur];
    }
}