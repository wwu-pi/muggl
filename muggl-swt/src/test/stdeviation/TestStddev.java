package test.stdeviation;

import junit.framework.*;

public class TestStddev extends TestCase{
  
  public TestStddev(String name){super(name);}
  
  public static Test suite(){
    return new TestSuite(TestStddev.class);}
  
  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());}
  
  public void testSqrt(){
	assertTrue(2.00001-StdDev.sqrt(4.0)<0.0001);
  }
  
  public void testStddev(){
    double[] data = {4.5,5.0,5.5,6.0,6.5,7.0};
	//double[] data = {4.0,4.0,4.0,4.0};
    assertTrue(StdDev.stddev(data)-0.853912<0.00001);
  } 
}

