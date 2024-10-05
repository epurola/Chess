package com.example;

import java.util.HashMap;
import java.util.Map;

public class ChessOpeningMap {
    private Map<String, String> fenToOpeningMap;

    public ChessOpeningMap() {
        fenToOpeningMap = new HashMap<>();
        initializeOpenings();
    }

    // Method to insert a FEN string and the corresponding opening name
    public void insert(String fen, String openingName) {
        fenToOpeningMap.put(fen, openingName);
    }

    // Method to search for an opening based on the FEN string
    public String search(String fen) {
        return fenToOpeningMap.getOrDefault(fen, "Unknown Opening");
    }

    // Method to initialize the common openings
    private void initializeOpenings() {
        insert("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b", "King's Pawn Opening");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w ", "Open Game");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b", "King's Knight Opening");
        insert("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w", "King's Knight Opening");
        insert("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R b", "Three Knights Game");
        insert("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w", "Four Knights Game");
        insert("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/2N2N2/PPPP1PPP/R1BQK2R b", "Spanish Four Knights Game");
        insert("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b", "Italian Game");
        insert("r1bqkbnr/pppp1ppp/8/4p3/2BnP3/5N2/PPPP1PPP/RNBQK2R w", "Blackburne Shilling Gambit");
        insert("rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b", "Bishop's Opening 2");
        insert("rnbqk1nr/pppp1ppp/8/2b1p3/2B1P3/8/PPPP1PPP/RNBQK1NR w", "Bishop's Opening Classical Defence");
        insert("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w", "Bishop's Opening ");
        insert("rnbqkbnr/ppppp1pp/8/5p2/4P3/8/PPPP1PPP/RNBQKBNR w", "Duras Gambit/Fred Defence");
        insert("rnbqkbnr/pp1ppppp/8/2p5/4P3/2N5/PPPP1PPP/R1BQKBNR b", "Closed Sicilian");
        insert("rnbqkbnr/pp1ppppp/8/2p5/2B1P3/8/PPPP1PPP/RNBQK1NR b", "Bowdler Attack");
        insert("rnbqkbnr/pp1ppppp/8/2p5/3PP3/8/PPP2PPP/RNBQKBNR b", "Smith-Morra Gambit");
        insert("rnbqkbnr/pp1ppppp/8/2p5/4PP2/8/PPPP2PP/RNBQKBNR b", "Sicilian - Grand Prix Attack");
        insert("rnbqkbnr/pp1ppppp/8/2p5/1P2P3/8/P1PP1PPP/RNBQKBNR b", "Sicilian Wing Gambit");
        insert("rnbqkbnr/pppp1ppp/8/4p3/3PP3/8/PPP2PPP/RNBQKBNR b", "Center Game");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4PP2/8/PPPP2PP/RNBQKBNR b", "King's Gambit");
        insert("rnbqkbnr/pppp1ppp/8/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR b", "Wayward Queen Attack/Parham Attack");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/3P4/PPP2PPP/RNBQKBNR b", "Leonardis Variation");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/2P5/PP1P1PPP/RNBQKBNR b", "Centre Pawn Opening/MacLeod Attack/Lopez Opening");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/5P2/PPPP2PP/RNBQKBNR b", "King's Pawn Game: King's Head Opening");
        insert("rnbqkbnr/ppp1pppp/8/3p4/4P3/5N2/PPPP1PPP/RNBQKB1R b", "Tennison Gambit");
        insert("rnbqkbnr/ppp1pppp/8/3p4/3PP3/8/PPP2PPP/RNBQKBNR b", "Blackmar-Diemer Gambit 2.");
        insert("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w", "French Defence");
        insert("rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR b", "French Defense: Knight Variation");
        insert("rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w", "Caro-Kann Defence");
        insert("rnbqkbnr/pp1ppppp/2p5/8/2B1P3/8/PPPP1PPP/RNBQK1NR b", "Hillbilly Attack/Shrek");
        insert("rnbqkbnr/pp1ppppp/2p5/8/4P3/3P4/PPP2PPP/RNBQKBNR b", "Caro-Kann Defence, Breyer Variation");
        insert("r1bqkbnr/pppppppp/2n5/8/4P3/8/PPPP1PPP/RNBQKBNR w", "Nimzowitsch Defence");
        insert("r1bqkbnr/pppppppp/2n5/8/4P3/5N2/PPPP1PPP/RNBQKB1R b", "Nimzowitsch Defence");
        insert("rnbqkbnr/ppp1pppp/3p4/8/4P3/8/PPPP1PPP/RNBQKBNR w", "Pirc Defense");
        insert("rnbqkbnr/ppp1pppp/3p4/8/4PP2/8/PPPP2PP/RNBQKBNR b", "Rat Defense");
        

        // e4 Openings
        insert("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b", "King's Pawn Opening");
        insert("r1bqkbnr/pppp1ppp/2n5/4p3/3PP3/5N2/PPP2PPP/RNBQKB1R b", "Scotch Game");
        insert("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w", "Scandinavian Defense");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4PP2/8/PPPP2PP/RNBQKBNR b", "King's Gambit");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w", "Philidor Defense");
        insert("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w", "Scandinavian Defense");
        insert("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b", "French Defense");
        insert("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w", "Scandinavian Defense");
        insert("rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w", "Alekhine's Defense");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w", "Open Game");
        insert("rnbqkb1r/pppp1ppp/4pn2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "Indian Defenses");


        // Ruy Lopez
        insert("rnbqkbnr/pppp1ppp/8/4p3/3PP3/8/PPP2PPP/RNBQKBNR b", "Ruy Lopez");
        insert("r1bqkbnr/pppp1ppp/2n5/1B2p3/3PP3/8/PPP2PPP/RNBQK1NR b", "Ruy Lopez: Exchange Variation");
        insert("rnbqkbnr/pppp1ppp/8/4p3/3PP3/5N2/PPP1P1PP/RNBQKB1R b", "Ruy Lopez: Berlin Defense");
        insert("rnbqk1nr/ppppbppp/8/4P3/2PP4/8/PPP1P1PP/RNBQKB1R w", "Ruy Lopez: Schliemann Defense");

        // d4 Openings
        insert("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b", "Queen's Pawn Opening");
        insert("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "Indian Defenses");
        insert("rnbqkb1r/pppp1ppp/4pn2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "Queen's Gambit Declined");
        insert("rnbqkbnr/pppp1ppp/8/3p4/3PP3/8/PPP2PPP/RNBQKBNR b", "Slav Defense");
        insert("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b", "Catalan Opening");
        insert("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "Grünfeld Defense");
        insert("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "King's Indian Defense");
        insert("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w", "Nimzo-Indian Defense");

        // Other Openings
        insert("rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b", "English Opening");
        insert("rnbqkbnr/pppp1ppp/8/4p3/4P3/2N5/PPPP1PPP/R1BQKBNR b", "Vienna Game");
        insert("rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w", "Bishop's Opening");
        insert("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b", "Bird's Opening");
        insert("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b", "London System");

    }

}
