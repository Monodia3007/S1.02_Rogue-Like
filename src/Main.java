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
    }
    final int NB_PIECE_PAR_ETAGE = 10;
    Donjon donjonAlpha = newDonjon();
    donjon.etageActuel = new Piece[][] {
            {PIECES[4], PIECES[0]},
            {PIECES[2], PIECES[3]},
            {PIECES[2], PIECES[0]},
            {PIECES[2], PIECES[2]},
            {PIECES[0], PIECES[2]},
            {PIECES[0], PIECES[1]}
    };
    donjon.numeroEtage = 0;

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

    void fetchQR(String filename){
        //Nous chargeon le fichier possédant les question, les réponses et les indice.
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
        //Nous chargeon le fichier possédant les question, les réponses et les indice du boss.
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
        //si le joueur na plus de vie
        if (life<1){
            // partie fini
            return true;
        }
        else{
            return false;
        }
    }

    void initPosition(Player p){
        p.x=10;
        p.y=10;
    }


    Player newPlayer(String nickname){
        //Création d'un nouvelle éléement de la classe joueur
        Player p = new Player();
        //Iniialisation du pseudo
		p.nickname=nickname;
        //Iniialisation du noumbre de vie
		p.life=3;
        //Iniialisation du nombre d'indice
        p.hint=0;
        //Iniialisation de la position du joueur
        initPosition(p);
		return p;
    }

    void deplacement(Player p){
        boolean stop = false;
        char rep= ' ';
        // Tant que le déplacement n'est pas éffectuer
        while (!stop){
            println("Ou voulez vous aller ? ");
            println("Appuyer sur Z pour aller en haut, Q pour aller a gauche, S pour aller en bas, et D pour aller a droite");
            rep = readChar();
            //Déplacement vers le haut
            if ((rep== 'Z' || rep == 'z') && (p.y<21)){
                stop = true;
            }
            //Déplacement vers la gauche
            else if ((rep == 'Q' || rep == 'q') && (p.x>0)){
                stop = true;
            }
            //Déplacement vers le bas
            else if ((rep == 'S' || rep == 's') && (p.y>0)){
                stop = true;
            }
            //Déplacement vers la droite
            else if ((rep == 'D' || rep == 'd') && (p.x<21)){
                stop = true;
            }
            // On recommence la saisie de touche car la touche ne correspond pas au déplacement
            else{
                println("Veuillez taper une touche valide. ");
            }
            
        }
    }


    void question(String[][] QR, int ligne, Player p){
        boolean stop = false;
        String rep = "";
        //Tant que la question n'a pas été répondu ou que le joueur a des vie
        while (!stop && p.life>0){
            //Affichage de la question
            println("Question : " + QR[ligne][0]);
            //récupération de la réponsse
            rep = readString();
            //Si la réponse du joueur est bonne
            if (equals(rep,QR[ligne][1])){
                println("Bien jouer");
                //On arrète la boucle
                stop = true;
            }
            else{
                //Sinon on lui enlève une vie et il recommence
                println("Rater");
                p.life--;
            }
        }
        // Donne une chance d'obtenire un bonus
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
        //Tant que la question n'a pas été répondu ou que le joueur à des vie
        while (!stop && p.life>0){
            //Affichage de la question
            println("Question : " + QR[ligne][0]);
            //Si le joueur n'a pas encore utiliser d'indice 
            if(hintOn=false){
                //On lui demande si il veut en utiliser
                println("Voulez vous un indice ? o/n");
                repHint = readChar();
                //Si oui alors on lui conssomme un indice
                if (repHint=='o'){
                    hintOn=true;
                    p.hint--;
                }
            }
            clearScreen();
            //On re-affhiche la question
            println("Question : " + QR[ligne][0]);

            //Si le joueura un l'indice d'actif
            if(hintOn){
                //On affiche l'indice
                println("Indice : " + QR[ligne][2]);
            }

            //On récupère la réponssse
            rep = readString();

            //Si la réponse du joueur est bonne
            if (equals(rep,QR[ligne][1])){
                //On dit au combat de s'arrèté
                println("Bien jouer");
                stop = true;
                hintOn = false;
            }
            else{
                //Sinon on lui enlève une vie et il recommence
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
        //On récupère un nombre randome entre 0 et max exclu
        int range = max - min;
        return (int)(random()*range)+min;
    }

    void Fin(Player p){
        clearScreen();
        if(gameOver(p.life)){
            println("VOUS AVEZ PERDUE");
        }
        else{
            println("VOUS AVEZ GAGNER!!!");
        }
        
    }

    void algorithm(){
        int tmp = 0;
        Player p;
        String pseudo = "";
        println("Nouvelle partie : 1                     Reprendre une partie : 2");
        tmp = readInt();
        donjon = new
        if (tmp == 1){
            println("Entrez votre pseudo : ");
            pseudo = readString();
            p = newPlayer(pseudo);
        }

    }
}