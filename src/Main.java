import extensions.CSVFile;

class Main extends Program{

    String[][] questionReponse;
    String[][] questionReponseBoss;
    final int COTE = 21;
    final Piece[] PIECES = new Piece[]{
            newPiece('V', 0.0),
            newPiece('S', 0.0),
            newPiece('R', 0.6),
            newPiece('H', 0.0),
            newPiece('B', 0.0)
    };

    final int NB_PIECE_PAR_ETAGE = 10;

    Donjon newDonjon() {
        //Constructeur du type Donjon
        Donjon donjon = new Donjon();
        donjon.etageActuel = new Piece[][] {
                {PIECES[4], PIECES[0]},
                {PIECES[2], PIECES[3]},
                {PIECES[2], PIECES[0]},
                {PIECES[2], PIECES[2]},
                {PIECES[0], PIECES[2]},
                {PIECES[0], PIECES[1]}
        };
        donjon.numeroEtage = 0;
        return donjon;
    }

    Piece newPiece (char type, double spawnRate) {
        //Constructeur du type Piece
        Piece piece = new Piece();
        piece.type = type;
        piece.spawnRate = spawnRate;
        return piece;
    }

    void fetchQR(String filename){
        //Nous chargeons le fichier possédant les questions, les réponses et les indice.
        CSVFile file = loadCSV(filename);
        //initialisation du tableau avec la taille du fichier.
        questionReponse = new String[rowCount(file)][columnCount(file)];
        //Remplissage du tableau
        for (int i=0;i<rowCount(file);i++){
            for (int j=0;j<columnCount(file);j++){
                questionReponse[i][j]=getCell(file,i,j);
            }
        }
    }

    void fetchQRB(String filename){
        //Nous chargeons le fichier possédant les questions, les réponses et les indice du boss.
        CSVFile file = loadCSV(filename);
        //initialisation du tableau avec la taille du fichier.
        questionReponseBoss = new String[rowCount(file)][columnCount(file)];
        //Remplissage du tableau
        for (int i=0;i<rowCount(file);i++){
            for (int j=0;j<columnCount(file);j++){
                questionReponseBoss[i][j]=getCell(file,i,j);
            }
        }
    }
    
    boolean gameOver(int life){
        //si le joueur n'a plus de vie
        if (life<1){
            // partie finie
            return true;
        }
        else{
            return false;
        }
    }

    Player newPlayer(String nickname){
        //Création d'un nouvel élément de la classe joueur
        Player p = new Player();
        //Initialisation du pseudo
		p.nickname=nickname;
        //Initialisation du nombre de vies
		p.life=3;
        //Initialisation du nombre d'indices
        p.hint=0;
        //Initialisation de la position du joueur
        p.x=1;
        p.y=5;
		return p;
    }

    boolean pieceValide(Donjon donjon, int x, int y){
        if((x < 0 || x >= length(donjon.etageActuel, 2) || (y < 0 || y >= length(donjon.etageActuel, 1))) || donjon.etageActuel[y][x].type == 'V'){
            return false;
        }
        return true;
    }

    void deplacement(Player p, Donjon donjon){
        boolean stop = false;
        char rep = ' ';
        // Tant que le déplacement n'est pas effectué
        while (!stop){
            println("Ou voulez vous aller ? ");
            println("Appuyer sur Z pour aller en haut, Q pour aller a gauche, S pour aller en bas, et D pour aller a droite");
            rep = readChar();
            //Déplacement vers le haut
            if ((rep == 'Z' || rep == 'z') && (pieceValide(donjon, p.x, p.y - 1))){
                stop = true;
                p.y--;
            }
            //Déplacement vers la gauche
            else if ((rep == 'Q' || rep == 'q') && (pieceValide(donjon, p.x - 1, p.y))){
                stop = true;
                p.x--;
            }
            //Déplacement vers le bas
            else if ((rep == 'S' || rep == 's') && (pieceValide(donjon, p.x, p.y + 1))){
                stop = true;
                p.y++;
            }
            //Déplacement vers la droite
            else if ((rep == 'D' || rep == 'd') && (pieceValide(donjon, p.x + 1, p.y))){
                stop = true;
                p.x++;
            }
            // On recommence la saisie de touche, car la touche ne correspond pas au déplacement
            else {
                afficher(donjon, p);
                println("Veuillez taper une touche valide. ");
            }
            
        }
    }

    void question(String[][] QR, int ligne, Player p){
        boolean stop = false;
        String rep = "";
        //Tant que la question n'a pas été répondu ou que le joueur a des vies
        while (!stop && p.life>0){
            //Affichage de la question
            println("Question : " + QR[ligne][0]);
            //récupération de la réponse
            rep = readString();
            //Si la réponse du joueur est bonne
            if (equals(rep,QR[ligne][1])){
                println("Bien jouer");
                //On arrête la boucle
                stop = true;
            }
            else{
                //Sinon, on lui enlève une vie et il recommence
                println("Rater");
                p.life--;
            }
        }
        // Donne une chance d'obtenir un bonus
        if (stop){
            int r = random(1,11);
            //30% de chance de d'obtenir un point de vie en plus
            if(r>7){
                getLife(p);
            }
            //10% de chance d'obtenir un indice contre un boss
            else if(r<2){
                getHint(p);
            }
        }
    }

    void questionBoss(String[][] QR, int ligne, Player p){
        boolean stop = false;
        String rep = "";
        char repHint = ' ';
        boolean hintOn = false;
        //Tant que la question n'a pas été répondu ou que le joueur à des vies
        while (!stop && p.life>0){
            //Affichage de la question
            println("Question : " + QR[ligne][0]);
            //Si le joueur n'a pas encore utilisé d'indice
            if(hintOn=false){
                //On lui demande s'il veut en utiliser
                println("Voulez vous un indice ? o/n");
                repHint = readChar();
                //Si oui alors on lui consomme un indice
                if (repHint=='o'){
                    hintOn=true;
                    p.hint--;
                }
            }
            clearScreen();
            //On re-affiche la question
            println("Question : " + QR[ligne][0]);

            //Si le joueur a un indice d'actif
            if(hintOn){
                //On affiche l'indice
                println("Indice : " + QR[ligne][2]);
            }

            //On récupère la response
            rep = readString();

            //Si la réponse du joueur est bonne
            if (equals(rep,QR[ligne][1])){
                //On dit au combat de s'arrêter
                println("Bien jouer");
                stop = true;
                hintOn = false;
            }
            else{
                //Sinon, on lui enlève une vie et il recommence
                println("Rater");
                p.life--;
            }
        }
    }

    void getHint(Player p){
        //On rajoute un indice au joueur
        p.hint++;
    }

    void getLife(Player p){
        //On rajoute un indice au joueur
        p.life++;
    }

    int random(int min, int max){
        //On récupère un nombre random entre 0 et max exclu
        int range = max - min;
        return (int)(random()*range)+min;
    }

    void fin(Player p, Donjon donjon, int nbEtage){
        clearScreen();
        if(gameOver(p.life)){
            println("VOUS AVEZ PERDUE");
        }
        else if (donjon.numeroEtage >= nbEtage){
            println("VOUS AVEZ GAGNÉ!!!");
        }
        
    }

    void afficherCarte(Donjon donjon, Player p){
        //Fonction permettant de l'affichage de la carte du donjon
        clearScreen();
        println(p.nickname);
        for (int i=0; i<length(donjon.etageActuel,1); i++){
            for (int j=0; j<length(donjon.etageActuel,2); j++){
                if(p.x == j && p.y==i){
                    print('P');
                }
                else{
                    print(donjon.etageActuel[i][j].type);
                }
            }
            println();
        }
        for(int i = 0; i<3; i++){
            println();
        }
        println("Nombre de Vie : " + p.life + "                                          Nombre d'Indice : " + p.hint);
    }

    String RGBToANSI(int[] rgb, boolean backgroundColor) {
        return "\u001b[" + (backgroundColor ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
    }


    void algorithm(){
        int tmp = 0;
        Player p;
        String pseudo = "";
        int nbEtages = 1;
        boolean fini = false;

        println("Nouvelle partie : 1                     Reprendre une partie : 2");
        tmp = readInt();
        Donjon donjon = newDonjon();

        if (tmp == 1){
            clearScreen();
            println("Entrez votre pseudo : ");
            pseudo = readString();
            p = newPlayer(pseudo);

            afficherCarte(donjon,p);
            while (!fini) {
                deplacement(p, donjon);
                afficherCarte(donjon, p);
            }
        }
    }


    void testPieceValide(){
        Donjon donjon = newDonjon();
        Player p = newPlayer("pseudo");
        assertTrue(pieceValide(donjon, p.x, p.y++));
        assertFalse(pieceValide(donjon, p.x++, p.y));
    }
}
