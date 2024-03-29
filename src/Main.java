import extensions.CSVFile;


class Main extends Program{

    String[][] questionReponse = fetchQuestion("./ressources/Question.csv");
    String[][] questionReponseBoss = fetchQuestion("./ressources/QuestionB.csv");
    final Piece[] PIECES = new Piece[]{
            newPiece('V', 0.0, new int[0][0]),
            newPiece('S', 0.0, fetchApparence("./ressources/startRoom.csv")),
            newPiece('R', 0.6, fetchApparence("./ressources/standardRoom.csv")),
            newPiece('H', 0.0, fetchApparence("./ressources/hintRoom.csv")),
            newPiece('B', 0.0, fetchApparence("./ressources/bossRoom.csv")),
            newPiece('U', 0.0, fetchApparence("./ressources/stairRoom.csv"))
    };
    int[][] colorPiece, colorCarte, carte = fetchApparence("./ressources/Map.csv");

    Donjon newDonjon() {
        //Constructeur du type Donjon
        Donjon donjon = new Donjon();
        donjon.etageActuel = new Piece[length(carte, 1)][length(carte, 2)];
        for (int i = 0; i < length(carte, 1); i++) {
            for (int j = 0; j < length(carte, 2); j++) {
                donjon.etageActuel[i][j] = PIECES[carte[i][j]];
            }
        }
        donjon.numeroEtage = 0;
        return donjon;
    }

    Piece newPiece (char type, double spawnRate, int[][] apparence) {
        //Constructeur du type Piece
        Piece piece = new Piece();
        piece.type = type;
        piece.spawnRate = spawnRate;
        piece.apparence = apparence;
        return piece;
    }

    void fetchColorPiece () {
        CSVFile file = loadCSV("./ressources/0-colors.csv");
        colorPiece = new int[rowCount(file)][columnCount(file)];
        for (int i = 0; i < rowCount(file); i++){
            for (int j = 0; j < columnCount(file); j++){
                colorPiece[i][j] = stringToInt(getCell(file, i, j));
            }
        }
    }

    void fetchColorCarte () {
        CSVFile file = loadCSV("./ressources/1-colors.csv");
        colorCarte = new int[rowCount(file)][columnCount(file)];
        for (int i = 0; i < rowCount(file); i++){
            for (int j = 0; j < columnCount(file); j++){
                colorCarte[i][j] = stringToInt(getCell(file, i, j));
            }
        }
    }

    int[][] fetchApparence(String filename) {
        CSVFile file = loadCSV(filename);
        int [][] apparence = new int[rowCount(file)][columnCount(file)];
        for (int i = 0; i < rowCount(file); i++){
            for (int j = 0; j < columnCount(file); j++){
                apparence[i][j] = stringToInt(getCell(file, i, j));
            }
        }
        return apparence;
    }


    String[][] fetchQuestion (String filename){
        //Nous chargeons le fichier possédant les questions, les réponses.
        CSVFile file = loadCSV(filename);
        //initialisation du tableau avec la taille du fichier.
        String[][] questions= new String[rowCount(file)][columnCount(file)];
        //Remplissage du tableau
        for (int i=0;i<rowCount(file);i++){
            for (int j=0;j<columnCount(file);j++){
                questions[i][j]= getCell(file,i,j);
            }
        }
        return questions;
    }

    
    boolean gameOver(int life){
        //si le joueur n'a plus de vie
        // partie finie
        return life < 1;
    }

    Player newPlayer(String nickname){
        //Création d'un nouvel élément de la classe joueur
        Player p = new Player();
        //Initialisation du pseudo
		p.nickname = nickname;
        //Initialisation du nombre de vies
		p.life = 3;
        //Initialisation du nombre d'indices
        p.hint = 0;
        //Initialisation de la position du joueur
        p.x = 10;
        p.y = 10;
		return p;
    }

    boolean pieceValide(Donjon donjon, int x, int y){
        return (x >= 0 && x < length(donjon.etageActuel, 2) && (y >= 0 && y < length(donjon.etageActuel, 1))) && donjon.etageActuel[y][x].type != 'V';
    }

    void action(Player p, Donjon donjon){
        boolean stop = false;
        String rep;
        // Tant que le déplacement n'est pas effectué
        while (!stop){
            println("Ou voulez vous aller ? ");
            println("Appuyer sur Z pour aller en haut,\nQ pour aller a gauche,\nS pour aller en bas,\net D pour aller a droite");
            println("E pour sauvegarder en dehors d'une salle de boss et \nU pour monter d'étage si vous êtes dans la bonne pièce \nM pour afficher la carte");
            rep = readString();
            //Déplacement vers le haut
            if ((equals(rep, "Z") || equals(rep, "z")) && (pieceValide(donjon, p.x, p.y - 1))){
                stop = true;
                p.y--;
            }
            //Déplacement vers la gauche
            else if ((equals(rep, "Q") || equals(rep, "q")) && (pieceValide(donjon, p.x - 1, p.y))){
                stop = true;
                p.x--;
            }
            //Déplacement vers le bas
            else if ((equals(rep, "S") || equals(rep, "s")) && (pieceValide(donjon, p.x, p.y + 1))){
                stop = true;
                p.y++;
            }
            //Déplacement vers la droite
            else if ((equals(rep, "D") || equals(rep, "d")) && (pieceValide(donjon, p.x + 1, p.y))){
                stop = true;
                p.x++;
            }
            // Sauvegarde
            else if ((equals(rep, "E") || equals(rep, "e")) && (donjon.etageActuel[p.y][p.x].type != 'B')){
                saveDonjon(donjon);
                savePlayer(p);
                clearScreen();
                afficherPiece(donjon,p);
                println("Sauvegarde effectué");
            }
            //Afficher la carte
            else if ((equals(rep, "M") || equals(rep, "m")) && (donjon.etageActuel[p.y][p.x].type != 'B')){
                afficherCarte(donjon,p);
            }
            else if ((equals(rep, "U") || equals(rep, "u")) && (donjon.etageActuel[p.y][p.x].type == 'U')){
                up(donjon,p);
            }
            // On recommence la saisie de touche, car la touche ne correspond pas au déplacement
            else {
                afficherPiece(donjon, p);
                println("Veuillez taper une touche valide. ");
            }
            
        }
    }

    void question(String[][] QR, int ligne, Player p){
        boolean stop = false;
        String rep;
        //Tant que la question n'a pas été répondu ou que le joueur a des vies
        while (!stop && p.life>0){
            //Affichage de la question
            clearScreen();
            println("Tu as " + p.life + " vie restante");
            println("Question : " + QR[ligne][0]);
            //récupération de la réponse
            rep = readString();
            //Si la réponse du joueur est bonne
            if (equals(rep,QR[ligne][1])){
                println("Bien joué\nAppuyer sur entrer pour continuer");
                //On arrête la boucle
                stop = true;
                readString();
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
                addLife(p);
            }
            //10% de chance d'obtenir un indice contre un boss
            else if(r<2){
                addHint(p);
            }
        }
    }

    void up(Donjon donjon, Player p){
        donjon.numeroEtage++;
        p.x=10;
        p.y=10;
        afficherPiece(donjon,p);
    }

    void questionBoss(String[][] QR, int ligne, Player p){
        boolean stop = false, hintOn = false;
        String rep;
        char repHint;
        //Tant que la question n'a pas été répondu ou que le joueur à des vies
        while (!stop && p.life>0){
            //Affichage de la question
            println("Attention BOSS!!\nQuestion : " + QR[ligne][0]);
            //Si le joueur n'a pas encore utilisé d'indice
            if(!hintOn){
                //On lui demande s'il veut en utiliser
                println("Voulez vous un indice ? o/n");
                repHint = readChar();
                //Si oui alors, on lui consomme un indice
                if (repHint == 'o'){
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
                println("Bien joué\nAppuyer sur entrer pour continuer");
                stop = true;
                hintOn = false;
                readString();
            }
            else{
                //Sinon, on lui enlève une vie et il recommence
                println("Rater");
                p.life--;
            }
        }
    }

    void addHint(Player p){
        //On rajoute un indice au joueur
        p.hint++;
    }

    void addLife(Player p){
        //On rajoute un indice au joueur
        p.life++;
    }

    int random(int min, int max){
        //On récupère un nombre random entre 0 et max exclu
        int range = max - min;
        return (int)(random()*range)+min;
    }

    void fin(Player p){
        clearScreen();
        if(gameOver(p.life)){
            println("VOUS AVEZ PERDUE");
        }
        else{
            println("VOUS AVEZ GAGNÉ!!!");
        }
        
    }

    void afficherCarte(Donjon donjon, Player p){
        //Fonction permettant de l'affichage de la carte du donjon
        clearScreen();
        println();
        println(p.nickname);
        for (int i = 0; i < length(donjon.etageActuel, 1); i++) {
            for (int j = 0; j < length(donjon.etageActuel, 2); j++) {
                if (i == p.y && j == p.x) {
                    printPixel(RGBToANSI(colorCarte[carte[i][j]], false), "PP");
                } else {
                    printPixel(RGBToANSI(colorCarte[carte[i][j]], true), "  ");
                }
            }
            println();
        }
        println("Appuyer sur entrer pour fermer la carte");
        readString();
        clearScreen();
        afficherPiece(donjon,p);
    }

    String RGBToANSI(int[] rgb, boolean background) {
        return "\u001b[" + (background ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
    }

    void printPixel(String color, String smallText) {
        print(color + smallText + ANSI_RESET);
    }

    void afficherPiece(Donjon donjon, Player p) {
        clearScreen();
        println(p.nickname);
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                printPixel(RGBToANSI(colorPiece[donjon.etageActuel[p.y][p.x].apparence[i][j]], true), "  ");
            }
            println();
        }
        println("Nombre de Vie : " + p.life + "        Étage :" + donjon.numeroEtage + "          Nombre d'Indice : " + p.hint + "\n");
    }

    void savePlayer(Player p) {
        saveCSV(new String[][]{{p.nickname, "" + p.life, "" + p.hint, "" + p.x, "" + p.y}}, "./ressources/SavedPlayer.csv");
    }

    void saveDonjon(Donjon donjon) {
        String[][] savedDonjon = new String[length(donjon.etageActuel, 1) + 1][length(donjon.etageActuel, 2)];
        for (int i = 0; i < length(savedDonjon, 1) - 1; i++) {
            for (int j = 0; j < length(savedDonjon, 2); j++) {
                savedDonjon[i][j] = "" + donjon.etageActuel[i][j].type;
            }
        }
        savedDonjon[length(donjon.etageActuel, 1)][0] = "" + donjon.numeroEtage;
        saveCSV(savedDonjon, "./ressources/SavedDonjon.csv");
    }

    Player loadPlayer() {
        Player loadedPlayer = newPlayer("");
        CSVFile file = loadCSV("./ressources/SavedPlayer.csv");
        loadedPlayer.nickname = getCell(file, 0, 0);
        loadedPlayer.life = stringToInt(getCell(file, 0, 1));
        loadedPlayer.hint = stringToInt(getCell(file, 0, 2));
        loadedPlayer.x = stringToInt(getCell(file, 0, 3));
        loadedPlayer.y = stringToInt(getCell(file, 0, 4));
        return loadedPlayer;
    }

    Donjon loadDonjon() {
        Donjon loadedDonjon = newDonjon();
        CSVFile file = loadCSV("./ressources/SavedDonjon.csv");
        String[][] tempDonjon = new String[rowCount(file)][columnCount(file)];
        loadedDonjon.etageActuel = new Piece[rowCount(file) - 1][columnCount(file)];
        for (int i=0;i<rowCount(file);i++){
            for (int j=0;j<columnCount(file);j++){
                tempDonjon[i][j]=getCell(file,i,j);
            }
        }
        loadedDonjon.numeroEtage = stringToInt(tempDonjon[rowCount(file) - 1][0]);
        for (int i = 0; i < length(tempDonjon, 1) - 1; i++) {
            for (int j = 0; j < length(tempDonjon, 2); j++) {
                if (equals(tempDonjon[i][j], "V")) {
                    loadedDonjon.etageActuel[i][j] = PIECES[0];
                } else if (equals(tempDonjon[i][j], "S")) {
                    loadedDonjon.etageActuel[i][j] = PIECES[1];
                } else if (equals(tempDonjon[i][j], "R")) {
                    loadedDonjon.etageActuel[i][j] = PIECES[2];
                } else if (equals(tempDonjon[i][j], "H")) {
                    loadedDonjon.etageActuel[i][j] = PIECES[3];
                } else if (equals(tempDonjon[i][j], "B")) {
                    loadedDonjon.etageActuel[i][j] = PIECES[4];
                } else {
                    loadedDonjon.etageActuel[i][j] = PIECES[5];
                }
            }
        }
        return loadedDonjon;
    }

    void rencontre(Donjon donjon,Player p){
        if(donjon.etageActuel[p.y][p.x].type == 'B'){
            questionBoss(questionReponseBoss,random(0,length(questionReponseBoss,1)),p);
        }
        else if (donjon.etageActuel[p.y][p.x].type == 'H') {
            addHint(p);
            println("Vous avez gagné un indice\nAppuyer sur entrer pour continuer");
            readString();
        }
        else if (random() < donjon.etageActuel[p.y][p.x].spawnRate){
            question(questionReponse, random(0,length(questionReponse,1)), p);
        }

    }


 void algorithm(){
        clearScreen();
        fetchColorPiece();
        fetchColorCarte();
        int tmp;
        Player p = newPlayer("");
        String pseudo;
        boolean fini = false;
        println("================================================================");
        println("||~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||");
        println("||~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||");
        println("||~~~~~~~~~~~~~~~~~~~~~~~~~EduCrawler~~~~~~~~~~~~~~~~~~~~~~~~~||");
        println("||~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||");
        println("||~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||");
        println("================================================================");
        println("Nouvelle partie : 1                     Reprendre une partie : 2");
        tmp = readInt();
        Donjon donjon = newDonjon();

        if (tmp == 1) {
            clearScreen();
            println("Entrez votre pseudo : ");
            pseudo = readString();
            p.nickname = pseudo;
            clearScreen();
            afficherPiece(donjon, p);
        } else if (tmp == 2) {
            clearScreen();
            p = loadPlayer();
            donjon = loadDonjon();
            afficherPiece(donjon, p);
        }
        while (!fini) {
            action(p, donjon);
            rencontre(donjon, p);
            afficherPiece(donjon, p);
            if(donjon.numeroEtage == 5 || p.life<=0){
                fini = true;
            }
        }
        fin(p);
    }


    void testPieceValide(){
        Donjon donjon = newDonjon();
        Player p = newPlayer("pseudo");
        assertTrue(pieceValide(donjon, p.x, p.y++));
        assertFalse(pieceValide(donjon, p.x++, p.y));
    }
}
