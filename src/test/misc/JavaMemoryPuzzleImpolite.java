package test.misc;

@SuppressWarnings("all")
public class JavaMemoryPuzzleImpolite {
	  private final int dataSize =
	      (int) (Runtime.getRuntime().maxMemory() * 0.6);

	  public void f() {
	    {
	      byte[] data = new byte[dataSize];
	    }

		{
		  int a = 0;
		}
		
		byte[] data2 = new byte[dataSize];
	  }

	  public static void main(String[] args) {
	    JavaMemoryPuzzleImpolite jmp = new JavaMemoryPuzzleImpolite();
	    jmp.f();
	    System.out.println("No OutOfMemoryError");
	  }
	}