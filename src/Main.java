class Main extends Program {

    final int COTE = 21;
    final Piece[] PIECES = new Piece[]{
            newPiece('V', 0.0),
            newPiece('S', 0.0),
            newPiece('R', 0.6),
            newPiece('H', 0.0),
            newPiece('B', 0.0)
    }
    final int NB_PIECE_PAR_ETAGE = 10;

    int random(int min, int max) {
        int range = max - min;
        return (int) (random() * range) + min;
    }

    Donjon newDonjon() {
        Donjon donjon = new Donjon();
        donjon.etageActuel = new Piece[COTE][COTE];
        donjon.numeroEtage = 0;
        return donjon;
    }

    Piece newPiece (char type, double spawnRate) {
        Piece piece = new Piece();
        piece.type = type;
        piece.spawnRate = spawnRate;
        return piece;
    }

    void initialiserDonjon (Donjon donjon) {

    }

    String RGBToANSI(int[] rgb, boolean backgroundColor) {
        return "\u001b[" + (backgroundColor ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
    }

    void algorithm () {
        Donjon donjon = newDonjon();
    }
}