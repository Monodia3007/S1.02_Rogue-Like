class Main extends Program {

    final int COTE = 21;

    int random(int min, int max) {
        int range = max - min;
        return (int) (random() * range) + min;
    }
    void initialiserDonjon (char[][] donjon) {

    }

    String RGBToANSI(int[] rgb, boolean backgroundColor) {
        return "\u001b[" + (backgroundColor ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
    }

    void algorithm () {
        char[][] donjon = new char[COTE][COTE];
    }
}