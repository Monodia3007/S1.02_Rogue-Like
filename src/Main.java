class Main extends Program {
    void initialiserDonjon () {

    }

    void algorithm () {
        int largeur = 0, longueur = 0;
        println("Quelle taille de donjon voulez vous ? (1 n'est pas une taille valide)");
        while (largeur <= 1 && longueur <= 1) {
            print("Longueur : ");
            longueur = readInt();
            print("Largeur : ");
            largeur = readInt();
        }
        int[][] donjon = new int[longueur][largeur];
    }
}