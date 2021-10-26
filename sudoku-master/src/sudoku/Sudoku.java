package sudoku;   // package name


public class Sudoku {
	
	public static void main(String[] args) {
		//generates a random puzzle
		char[][] puzzle = SudokuP.puzzle();
	   
	   System.out.println("the unsolved puzzle is: ");
	   System.out.println();
	   print_puzzle(puzzle);
	   System.out.println();
	   
	   //if the puzzle is not already invalid, try to solve it
	   if (check(puzzle)) {
		   System.out.println("the solved puzzle is: ");
		   System.out.println();
		   solve(puzzle);
	   }
	   else 
	   	   System.out.println("this puzzle is invalid");
	    
	}
	
	
	public static boolean solveSudoku(char[][] puzzle) {

		//traverses looking for blank spaces to fill
		for(int row=0;row<9;row++) {
			for(int col=0;col<9;col++) {   
				if(puzzle[row][col]=='.') {
					
	                // we found an empty cell. Try filling it with each of the 9 digits:
					for( int i=1; i<=9;i++) {
						char num=Integer.toString(i).charAt(0);   
						puzzle[row][col]=num;   //places a number in puzzle
						
						// Check if adding the number in the puzzle would cause the puzzle to be invalid
						if (guess_check(row, col, puzzle)) {
							if(solveSudoku(puzzle))     
								return true;           //we should keep pursuing this path
						}
						puzzle[row][col]='.';     // we failed to solve on the current path, so restore and resume recursion
				    }
					return false;     //we ran out of guesses, there are no numbers that will be valid in this cell, so backtrack
			     }
			 }
		 }
		 return true;     // this is the last time, its out of the blank space checking for loop 
		                  //so all the cells must be filled and the fact that it got to this point means they are all valid
	}
	
	
		public static void solve(char[][] puzzle) {   
			//wrapper method
			if(puzzle == null || puzzle.length == 0)               
				return;           
		
			if(solveSudoku(puzzle))
				print_puzzle(puzzle);
			else
				System.out.println("no solution");
	
		}
		
	
	    public static boolean guess_check(int row, int col, char[][] puzzle) {
		    //this function validates individual boxes, rows, and columns for the solving purpose
	    	 if(validRow(puzzle[row])==false) 
	    		 return false;
	    	 if(validCol(col, puzzle)==false)
	    		 return false;
	    	 if (validBox(row-row%3,col-col%3,puzzle)==false)    //this should get the valid starting position for the traverising 
	    		 return false;
	    	 
	    	 return true;
	     }
	    
			
		public static boolean check(char[][] puzzle) {   
			//check automatically all rows and columns and boxes of the 9x9 this is for validating
			//the entire initial puzzle
			
			//checks each row
			for(int i=0; i<9;i++ ) {		
				if(!validRow(puzzle[i]))       
					return false;
			}
			//check each column
			for(int i=0; i<9;i++ ) {
				if(!validCol(i, puzzle))  
					return  false;
			}			
			
			//checks each box 9 boxes total
			int i=0;
			while(i<=6) {
				int j=0;
				while(j<=6) {
					if(!validBox(i,j,puzzle)) {    //i and j are the starting location of the box
						return false;
				     }
					j=j+3;
				}
			    i=i+3;
			}
			return true;
		}
			
		
		
		//the variable boolarray is used
		//when a number is found, the element of boolarray at the index of that number will be set to true
		//if it ever runs into an element that has already been set to true then valid will
		//be false because it will have already found that number
		
		public static boolean validCol(int Col, char[][] puzzle) {
			//traverses through one column and checks validity
			int index;
			boolean[] boolarray= {false,false,false,false,false,false,false,false,false};
				for(int i=0; i<9; i++) {
					if(puzzle[i][Col]!='.') {
						index=Character.getNumericValue(puzzle[i][Col]-1);
						
						if(boolarray[index] == false) {
							boolarray[index] = true;  
							}
						else 
							return false;  //means it already has seen that number
						
					}	
				}
				return true;
			}
		
		 
		
		public static boolean validRow(char[] row) {
			//traverses through one row and checks validity 
			boolean[] boolarray= {false,false,false,false,false,false,false,false,false};
			int index;
			    
				for(int i=0; i<9; i++) { //for each character in the row
					if(row[i]!='.') {  //if the character is not a period/blank
						index=Character.getNumericValue(row[i])-1; //change the character to an int value-1
						
						if(boolarray[index] == false) { //changes the index of boolarray to true each time a number is found
							boolarray[index] = true;
						}
						else {       //else boolarray at an index is already true return false
							return false;
						}
						
					}
						
				}
				return true;//if the rows are valid return true
		 }
		
		
		 
		public static boolean validBox(int i,int j,char[][] puzzle) {   
			//at any given i,j location this function will be called with i-i%3 and j-j%3 
			//because this will give us the top left most corner location of the box so we
			//know where to start traversing 
			boolean[] boolarray= {false,false,false,false,false,false,false,false,false};
			int index;
		
			for(int row=i;row<i+3;row++) {          //checks 3x3 box
				for(int column=j;column<j+3;column++) {
					if(puzzle[row][column]!='.') {
						index=Character.getNumericValue(puzzle[row][column]-1);
						if(boolarray[index] == false) {
							boolarray[index] = true;
						}
						else 
							return false;							    
					}		
			    }
			}
			return true;
		}
	
		
		
		

		public static void print_puzzle(char[][] puzzle){
	        //prints the puzzle 
	        for (int x = 0; x < 9; x++) {
	            for (int y = 0; y < 9; y++) {
	                
	                System.out.print(puzzle[x][y]+"  ");
	            }
	          
	            System.out.println();
	        }
	        
	    }
	
}
		
			
		
		
	
			
			
		
	
		

