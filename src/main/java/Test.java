import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Test  {
    public static void main(String[] args) {
        INDArray tens = Nd4j.ones(2,2);
        INDArray b= Nd4j.create(new float[][]{{1, 0, 0}, {0, 2, 0}, {0, 0, 3}});
        INDArray a = Nd4j.create(new float[][]{{1}, {2}, {3}});
        INDArray c = Nd4j.create(new float[][]{{2}, {4}, {3}});
        prints(b.shape());
        prints(a.shape());
        System.out.println(b.mmul(a));
        System.out.println(a.sub(c));
   
    }

    private static void prints(int[] s){
        System.out.println(s[0] + ", " + s[1]);
    }
    
    private static void printHello() {
    	System.out.println("hell0");
    }
}
