package com.example;

public class Board {
    private long whitePawns, blackPawns;
    private long whiteRooks, blackRooks;
    private long whiteKnights, blackKnights;
    private long whiteBishops, blackBishops;
    private long whiteQueens, blackQueens;
    private long whiteKings, blackKings;

    public Board() {
        initialize();
    }
    public Board(boolean isBlack) {
        initializeBlack();
    }
    public void setPiece(int row, int col, Piece newPiece) {
        long bitMask = 1L << (row * 8 + col);
    
        // Retrieve the current piece at the position
        Piece currentPiece = getPiece(row, col);
    
        // Clear the bit from the bitboard of the current piece if it exists
        if (currentPiece != null) {
            clearBitFromPiece(currentPiece, bitMask);
        }
    
        // Set the bit in the bitboard for the new piece if it exists
        if (newPiece != null) {
            if (newPiece instanceof Pawn) {
                if (newPiece.isWhite()) {
                    whitePawns |= bitMask;
                } else {
                    blackPawns |= bitMask;
                }
            } else if (newPiece instanceof Rook) {
                if (newPiece.isWhite()) {
                    whiteRooks |= bitMask;
                } else {
                    blackRooks |= bitMask;
                }
            } else if (newPiece instanceof Knight) {
                if (newPiece.isWhite()) {
                    whiteKnights |= bitMask;
                } else {
                    blackKnights |= bitMask;
                }
            } else if (newPiece instanceof Bishop) {
                if (newPiece.isWhite()) {
                    whiteBishops |= bitMask;
                } else {
                    blackBishops |= bitMask;
                }
            } else if (newPiece instanceof Queen) {
                if (newPiece.isWhite()) {
                    whiteQueens |= bitMask;
                } else {
                    blackQueens |= bitMask;
                }
            } else if (newPiece instanceof King) {
                if (newPiece.isWhite()) {
                    whiteKings |= bitMask;
                } else {
                    blackKings |= bitMask;
                }
            }
        }
    }
    
    private void clearBitFromPiece(Piece piece, long bitMask) {
        if (piece instanceof Pawn) {
            if (piece.isWhite()) {
                whitePawns &= ~bitMask;
            } else {
                blackPawns &= ~bitMask;
            }
        } else if (piece instanceof Rook) {
            if (piece.isWhite()) {
                whiteRooks &= ~bitMask;
            } else {
                blackRooks &= ~bitMask;
            }
        } else if (piece instanceof Knight) {
            if (piece.isWhite()) {
                whiteKnights &= ~bitMask;
            } else {
                blackKnights &= ~bitMask;
            }
        } else if (piece instanceof Bishop) {
            if (piece.isWhite()) {
                whiteBishops &= ~bitMask;
            } else {
                blackBishops &= ~bitMask;
            }
        } else if (piece instanceof Queen) {
            if (piece.isWhite()) {
                whiteQueens &= ~bitMask;
            } else {
                blackQueens &= ~bitMask;
            }
        } else if (piece instanceof King) {
            if (piece.isWhite()) {
                whiteKings &= ~bitMask;
            } else {
                blackKings &= ~bitMask;
            }
        }
    }
    
    public long getWhitePieces() {
        return whitePawns | whiteRooks | whiteKnights | whiteBishops | whiteQueens | whiteKings;
    }

    // Method to get the bitboard of all black pieces
    public long getBlackPieces() {
        return blackPawns | blackRooks | blackKnights | blackBishops | blackQueens | blackKings;
    }

    public long getWhitePawns() {return whitePawns;}
    public void setWhitePawns(long whitePawns) { this.whitePawns = whitePawns;}
    public long getBlackPawns() {return blackPawns;}
    public void setBlackPawns(long blackPawns) {this.blackPawns = blackPawns;}
    public long getWhiteRooks() {return whiteRooks;}
    public void setWhiteRooks(long whiteRooks) {this.whiteRooks = whiteRooks;}
    public long getBlackRooks() { return blackRooks;}
    public void setBlackRooks(long blackRooks) {this.blackRooks = blackRooks;}
    public long getWhiteKnights() {return whiteKnights;}
    public void setWhiteKnights(long whiteKnights) {this.whiteKnights = whiteKnights;}
    public long getBlackKnights() { return blackKnights;}
    public void setBlackKnights(long blackKnights) {this.blackKnights = blackKnights;}
    public long getWhiteBishops() { return whiteBishops; }
    public void setWhiteBishops(long whiteBishops) {this.whiteBishops = whiteBishops;}
    public long getBlackBishops() {return blackBishops;}
    public void setBlackBishops(long blackBishops) {this.blackBishops = blackBishops;}
    public long getWhiteQueens() {return whiteQueens;}
    public void setWhiteQueens(long whiteQueens) { this.whiteQueens = whiteQueens;}
    public long getBlackQueens() {return blackQueens;}
    public void setBlackQueens(long blackQueens) {this.blackQueens = blackQueens;}
    public long getWhiteKings() {return whiteKings;}
    public void setWhiteKings(long whiteKings) {this.whiteKings = whiteKings;}
    public long getBlackKings() {return blackKings;}
    public void setBlackKings(long blackKings) {this.blackKings = blackKings; }

    public Board copyBoard() {
        Board copy = new Board();
        copy.setWhitePawns(this.whitePawns);
        copy.setBlackPawns(this.blackPawns);
        copy.setWhiteRooks(this.whiteRooks);
        copy.setBlackRooks(this.blackRooks);
        copy.setWhiteKnights(this.whiteKnights);
        copy.setBlackKnights(this.blackKnights);
        copy.setWhiteBishops(this.whiteBishops);
        copy.setBlackBishops(this.blackBishops);
        copy.setWhiteQueens(this.whiteQueens);
        copy.setBlackQueens(this.blackQueens);
        copy.setWhiteKings(this.whiteKings);
        copy.setBlackKings(this.blackKings);
        return copy;
    }

   

    public void initialize() {
        whitePawns =  0x00FF000000000000L;// 8 white pawns
        blackPawns = 0x000000000000FF00L; // 8 black pawns
       
        whiteRooks = 0x8100000000000000L; // White rooks
        blackRooks = 0x0000000000000081L; // Black rooks

        whiteKnights = 0x4200000000000000L; // White knights
        blackKnights = 0x0000000000000042L; // Black knights

        whiteBishops = 0x2400000000000000L; // White bishops
        blackBishops = 0x0000000000000024L; // Black bishops

        whiteQueens = 0x0800000000000000L; // White queen
        blackQueens = 0x0000000000000008L; // Black queen

        whiteKings = 0x1000000000000000L; // White king
        blackKings = 0x0000000000000010L; // Black king
    }
    public void initializeBlack() {
        whitePawns =  0x000000000000FF00L;// 8 white pawns 
        blackPawns = 0x00FF000000000000L; // 8 black pawns
       
        whiteRooks = 0x0000000000000081L; // White rooks
        blackRooks = 0x8100000000000000L; // Black rooks

        whiteKnights = 0x0000000000000042L; // White knights
        blackKnights = 0x4200000000000000L;// Black knights

        whiteBishops = 0x0000000000000024L; // White bishops
        blackBishops = 0x2400000000000000L; // Black bishops

        whiteQueens = 0x0000000000000008L; // White queen
        blackQueens = 0x0800000000000000L; // Black queen

        whiteKings = 0x0000000000000010L; // White king0x1000000000000000L;
        blackKings = 0x1000000000000000L;// Black king
    }

    public Piece getPiece(int row, int col) {
        int index = row * 8 + col;
        long bitMask = 1L << index;
    
        // Check white pawns
        if ((whitePawns & bitMask) != 0) {
            return new Pawn(row, col, true);
        }
    
        // Check black pawns
        if ((blackPawns & bitMask) != 0) {
            return new Pawn(row, col, false);
        }
    
        // Check white rooks
        if ((whiteRooks & bitMask) != 0) {
            return new Rook(row, col, true);
        }
    
        // Check black rooks
        if ((blackRooks & bitMask) != 0) {
            return new Rook(row, col, false);
        }
    
        // Check white knights
        if ((whiteKnights & bitMask) != 0) {
            return new Knight(row, col, true);
        }
    
        // Check black knights
        if ((blackKnights & bitMask) != 0) {
            return new Knight(row, col, false);
        }
    
        // Check white bishops
        if ((whiteBishops & bitMask) != 0) {
            return new Bishop(row, col, true);
        }
    
        // Check black bishops
        if ((blackBishops & bitMask) != 0) {
            return new Bishop(row, col, false);
        }
    
        // Check white queens
        if ((whiteQueens & bitMask) != 0) {
            return new Queen(row, col, true);
        }
    
        // Check black queens
        if ((blackQueens & bitMask) != 0) {
            return new Queen(row, col, false);
        }
    
        // Check white kings
        if ((whiteKings & bitMask) != 0) {
            return new King(row, col, true);
        }
    
        // Check black kings
        if ((blackKings & bitMask) != 0) {
            return new King(row, col, false);
        }
    
        // If no piece is found at the given position
        return null;
    }

      // Method to convert the board state to FEN notation
      public String toFEN(boolean whiteToMove, String castlingRights, String enPassant) {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {  // Iterate from row 0 to 7
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {  // Columns remain in the same left-to-right order
                Piece piece = getPiece(row, col);
        
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);  // Append number of empty squares
                        emptyCount = 0;
                    }
                    fen.append(getFENCharForPiece(piece));  // Append the piece character
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row < 7) {  // Separate rows with a '/'
                fen.append("/");
            }
        }
        

        // Add active color
        fen.append(" ").append(whiteToMove ? "w" : "b");

        // Add castling rights
        // implement
        fen.append(" ").append(castlingRights.isEmpty() ? "-" : castlingRights);

        // Add en passant target square
        // implement later
        fen.append(" ").append(enPassant.isEmpty() ? "-" : enPassant);


        return fen.toString();
    }

    private char getFENCharForPiece(Piece piece) {
        if (piece instanceof Pawn) {
            return piece.isWhite() ? 'P' : 'p';
        } else if (piece instanceof Rook) {
            return piece.isWhite() ? 'R' : 'r';
        } else if (piece instanceof Knight) {
            return piece.isWhite() ? 'N' : 'n';
        } else if (piece instanceof Bishop) {
            return piece.isWhite() ? 'B' : 'b';
        } else if (piece instanceof Queen) {
            return piece.isWhite() ? 'Q' : 'q';
        } else if (piece instanceof King) {
            return piece.isWhite() ? 'K' : 'k';
        }
        return ' ';
    }
    
}

