//bin/sessionjc tests/src/benchmarks/rmi/BinaryTree.sj -d tests/classes/

package benchmarks.rmi;

import java.io.*;

public class BinaryTree implements Serializable{

public int value = 0 ;
public noalias BinaryTree left = null;
public noalias BinaryTree right = null;

public BinaryTree (noalias BinaryTree left, 
noalias BinaryTree right,
int value) {
this.left = left;
this.right = right;
this.value = value;
}

public BinaryTree (int value) {
this.left = null;
this.right = null;
this.value = value;
}

public boolean isNode () {
return (this.left == null);
}

public void inc () {
this.value++;
if (this.left != null) {
this.left.inc();
this.right.inc();
}
}

public void print () {
if (this.isNode()) {
System.out.print("val:"+this.value);
} else {
System.out.print("val:"+this.value+" left:(");
this.left.print();
System.out.print(")");
System.out.print(" right:(");
this.right.print();
System.out.print(")");
}
}

static public noalias BinaryTree createBinaryTree(int i) {
noalias BinaryTree left = null;
noalias BinaryTree right = null; 
noalias BinaryTree res = null;
if (i==0) {
res = new BinaryTree(left, right, i);
} else {
left = createBinaryTree(i-1);
right = createBinaryTree(i-1);
res = new BinaryTree(left, right, i);
}
return res;
}
} 
