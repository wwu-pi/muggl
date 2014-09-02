package test.preconditions;

public class PreCond {

	public boolean precondf(int x, int y) {
		return y>x;
	}

	public int f(int x,int y) {
		// do something
		int z = y-x;
		
		for (int i=0; i<z; i++)
			System.out.println(i);
		System.out.println("Hi");
			
		return z;
	}
	
	public int thisMethodFails() {
		System.out.println("Boo!");
		return 0;
	}
		
	public int g(int a, int b, int c){
		int r = 0;
		if (a>b) 
			r = f(b,a);
		else if (c>=0) 
			r = f(0,b);
		else
			r= a+b+c;
		
		return r;
	}
	
	public int h(int x) {
		
		if (x>0)
			return g(x-3,x-3,x-3);
		else 
			return x;
	}
	public int gmeetsprecondf(int a, int b, int c) {
		//int r=0;
		if (a>b){
			// r = f(b,a);
			if (!precondf(b,a))
				return 1;
		   // r = f(b,a);
		}
		else if (c>=0){
			if (!precondf(0,b))
				return 2;
		//	r = f(0,b);
		}
			
		//else 
		//	r = a+b+c;
		
		return 0;
		
	}

	public int hmeetsprecondf(int x) {
		
		if (x>0) {
			if (gmeetsprecondf(x-3,x-3,x-3)!=0)
				return 1;
			
			return 0;
		}
		else 
			return 0;
	}


}
