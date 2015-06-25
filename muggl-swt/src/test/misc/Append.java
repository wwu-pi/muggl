package test.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("all")
public class Append {
	
	public static void main(String... args) {
		File[] files = new File[8];
		files[0] = new File("");
		File file = new File("");
		
		try {
			OutputStream out = new FileOutputStream(file);
			for (File lese : files) {
				InputStream in = new FileInputStream(lese);
				byte[] b = new byte[255];
				while (true) {
					int len = in.read(b);
					if (len == -1)  {
						break;
					}
					out.write(b, 0, len);
				}
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
