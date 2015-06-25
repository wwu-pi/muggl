package test.problematic;

/*
 * In the valid test case, the output array equals the input array whereas it should be different.
 */

public class Blur2 {
	public static int[] simpleBlur (int[] picture) throws IllegalArgumentException{
		//Bild groß genug?
		if (picture==null || picture.length < 16){throw new IllegalArgumentException();}
		//Seitenlänge bestimmen
		int width = 4;
		while (width*width < picture.length){width++;}
		if (width*width != picture.length){throw new IllegalArgumentException(String.valueOf(width));}
		//In 2D + Rand kopieren
		int[][] source = new int[width+2][width+2];
		for (int i=0; i<width+2; i++){
			for (int j=0; j<width+2; j++){
				if (i==0 || j==0 || i==width+1 || j==width+1){
					source[i][j]=0;
				}
				else{
					source[i][j] = picture[(j-1)*width+i-1];
				}
			}
		}
		//blurren in Quelle
		for (int i=0; i<width; i++){
			for (int j=0; j<width; j++){
				int center = 4;
				if (i==0 || i==width-1){center++;}
				if (j==0 || j==width-1){center++;}
				picture[j*width+i] = (center*source[i+1][j+1]+source[i+1][j]+source[i+1][j+2]+source[i][j+1]+source[i+2][j+1]+4)/8; //+4 für mathematisches Runden
				}
		}
		// Output für Debugging
		/*
		  for (int j=0; j<width; j++){
			String s = "";
			for (int i=0; i<width; i++){
				s = s+String.valueOf(picture[j*width+i])+" ";
			}
			System.out.println(s);
		}
		*/
		return picture;
	}
}
