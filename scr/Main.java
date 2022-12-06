import extensions.CSVFile;

class Main extends Program{

    String[][] questionReponse;

    void fetchQR(String filename){
        CSVFile file = loadCSV(filename);
        questionReponse = new String[rowCount(file)][columnCount(file)];
        for (int i=0;i<rowCount(file);i++){
            for (int j=0;j<columnCount(file);j++){
                questionReponse[i][j]=getCell(file,i,j);
            }
        }
    }
    
    boolean gameOver(int life){
        if (life<1){
            return true;
        }
        else{
            return false;
        }
    }

    Player newPlayer(String nickname){
        Player p = new Player();
		p.nickname=nickname;
		p.life=3;
        p.hint= new int[10];
		return p;
    }

    void deplacement(Player p){
        boolean stop = false;
        char rep= ' ';
        while (!stop){
            println("Ou voulez vous aller ? ");
            println("Appuyer sur Z pour aller en haut, Q pour aller a gauche, S pour aller en bas, et D pour aller a droite");
            rep = readChar();
            if (rep== 'Z' || rep == 'z'){
                stop = true;
            }
            else if (rep == 'Q' || rep == 'q'){
                stop = true;
            }
            else if (rep == 'S' || rep == 's'){
                stop = true;
            }
            else if (rep == 'D' || rep == 'd'){
                stop = true;
            }
            else{
                println("Veuillez taper une touche valide. ");
            }
            
        }
    }


    void question(String Q, String R, Player p){
        boolean stop = false;
        String rep = "";
        while (!stop && p.life>0){
            println(Q);
            rep = readString();
            if (equals(rep,R)){
                println("Bien jouer");
                stop = true;
            }
            else{
                println("Rater");
                p.life--;
            }
        }
    }

    

}