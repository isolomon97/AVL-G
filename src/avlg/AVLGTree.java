package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

import java.util.concurrent.TimeoutException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Isaac Solomon!
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */

        public class Node {//class for a node
            T key;
            int height;
            Node left, right, parent;

            Node(T element){
                key = element;
                height = 0;
                left = null;
                right = null;
                parent = null;


            }

        }
        private int maxImbalance;
        static public int treeSize;


        public Node root;


    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
        if (maxImbalance < 1){
            throw new InvalidBalanceException("invalid balance");
        }
        else{
            this.maxImbalance = maxImbalance;
            root = new Node(null);
            treeSize = 0;
        }
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
        Node newNode = new Node(key);
        int balance;

        if (treeSize == 0){//empty tree
            root = new Node(key);
            root.height = 0;


        }
        else if(treeSize == 1){//stub tree
            if (key.compareTo(root.key) >= 0){//greater or equal to root
                root.right = newNode;
            }
            else{
                root.left = newNode;
            }
            root.height = 1;
            newNode.parent = root;
        }
        else{
            root = insertHelper(key, root);

        }




        treeSize++;
    }

    public Node insertHelper(T key, Node currRoot){
        Node newNode = new Node(key);
        int currBalance;


        if (currRoot == null){//insert here
            return newNode;
        }
        else if (key.compareTo(currRoot.key) >= 0) {//greater or equal to root

                currRoot.right = insertHelper(key, currRoot.right);

        }
        else {//less than root
                currRoot.left = insertHelper(key, currRoot.left);

        }


        currRoot.height = heightHelp(currRoot);//update parent of new nodes height

        currBalance = balance(currRoot);

        if ((currBalance > maxImbalance) && (key.compareTo(currRoot.left.key)) < 0){// moves only right from grandchild to root, need right rotation
            return rightRotate(currRoot);
        }

        if ((currBalance < -maxImbalance) && (key.compareTo(currRoot.right.key)) > 0) {// moves only left from grandchild to root, need left rotation
            return leftRotate(currRoot);
        }

        if ((currBalance > maxImbalance) && (key.compareTo(currRoot.left.key)) > 0){// moves left then right from grandchild to root, need LR rotation
            currRoot.left = leftRotate(currRoot.left);
            return rightRotate(currRoot);
        }

        if ((currBalance < -maxImbalance) && (key.compareTo(currRoot.right.key)) < 0){//need RL rotation
            currRoot.right = rightRotate(currRoot.right);
            return leftRotate(currRoot);

        }


        return currRoot;
    }

    public Node rightRotate(Node target){

        if (target == null){
            return target;
        }
        Node temp = target.left;


            target.left = temp.right;


        temp.right = target;


        target.height = heightHelp(target);
        temp.height = heightHelp(temp);



        return temp;
    }

    public Node leftRotate(Node target){

        if (target == null){
            return target;
        }
        Node temp = target.right;

        target.right = temp.left;


        temp.left = target;



        target.height = heightHelp(target);
        temp.height = heightHelp(temp);


        return temp;
    }

    public int balance (Node x){
        if (x == null){
            return 0;
        }
        else{
            return (heightHelp(x.left)-heightHelp(x.right));
        }
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
        T toReturn = null;

        if (treeSize == 0){
            throw new EmptyTreeException("empty tree");
        }
        else if (treeSize == 1){//stub tree
            if (root.key == key){//root has correct key
                toReturn = key;
                root = new Node(null);

                treeSize --;
            }
        }
        else if (treeSize == 2){
            if (root.key == key){
                toReturn = key;
                if (root.left == null){
                    root.key = root.right.key;
                    root.right = null;
                }
                else {
                    root.key = root.left.key;
                    root.left = null;
                }
                treeSize--;
            }
            else if (root.left != null){//check left
                if (root.left.key == key) {
                    toReturn = key;
                    root.left = null;
                    treeSize--;
                }
            }
            else if(root.right.key == key){//check right
                toReturn = key;
                root.right = null;
                treeSize--;
            }



        }
        else{//treesize of 3 or more
            if (search(key) != null){//found, go delete
                root = deleteHelp(key, root);
                treeSize--;
                toReturn = key;
            }

        }



        return toReturn;
    }

    public Node deleteHelp(T key, Node currRoot)  {
        int currBalance;
        T successor;

        if (currRoot == null){
            return currRoot;
        }


        if (key.compareTo(currRoot.key) == 0){//found target
          /*  if (currRoot.right == null && currRoot.left != null){//no normal successor, need to just delete and connect parent with left child
                    currRoot = currRoot.left;

            }

            else if (currRoot.left == null && currRoot.right == null){//has no children, just make null
                currRoot = null;
                return null;


            }
            else{//has children to the right

                if (heightHelp(currRoot.right) == 0){//has only 1 child, just set here
                    currRoot.key = currRoot.right.key;
                    currRoot.right = null;
                }

                else {
                    currRoot.key = getSuccessor(currRoot.right);
                }

            }


*/
            if (currRoot.left == null || currRoot.right == null){
                Node temp = null;

                if (temp == currRoot.left){
                    temp = currRoot.right;
                }
                else {
                    temp = currRoot.left;
                }

                if (temp == null){
                    temp = currRoot;
                    currRoot = null;
                }
                else{
                    currRoot = temp;
                }
            }
            else{
                Node temp = getSuccessor(currRoot.right);

                currRoot.key = temp.key;

                currRoot.right = deleteHelp(temp.key, currRoot.right);
            }
        }

        else if (key.compareTo(currRoot.key) > 0){//keep looking right
            if (currRoot.right != null){
                currRoot.right = deleteHelp(key, currRoot.right);
            }

        }

        else{//keep looking left
            if (currRoot.left != null){
                currRoot.left = deleteHelp(key, currRoot.left);
            }

        }

        if (currRoot == null){
            return currRoot;
        }

        currRoot.height = heightHelp(currRoot);//update parent of new nodes height


        currBalance = balance(currRoot);

        if ((currBalance > maxImbalance) && balance(currRoot.left) >= 0){// moves only right from grandchild to root, need R rotation
            return rightRotate(currRoot);
        }

        if ((currBalance > maxImbalance) && balance(currRoot.left) < 0){// moves left then right from grandchild to root, need LR rotation
            currRoot.left = leftRotate(currRoot.left);
            return rightRotate(currRoot);
        }

        if ((currBalance < -maxImbalance) && balance(currRoot.right) <= 0) {// moves only left from grandchild to root, need L rotation
            return leftRotate(currRoot);
        }


        if ((currBalance < -maxImbalance) && balance(currRoot.right) > 0){//need RL rotation
            currRoot.right = rightRotate(currRoot.right);
            return leftRotate(currRoot);

        }


        return currRoot;
    }


    public Node getSuccessor(Node root){//for when parents has more than 1 child
        Node curr = root;



        while (curr.left != null){
            curr = curr.left;

        }




        return curr;
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        T toReturn = null;

        if (treeSize == 0){
            throw new EmptyTreeException("Empty tree");
        }
        else{
            if (searchHelp(key, root) == true){
                toReturn = key;
            }
        }


        return toReturn;
    }

    public boolean searchHelp(T key, Node curr){
        boolean found = false;

        if (key.compareTo(curr.key) == 0){ //found element
            found = true;
        }
        else if (key.compareTo(curr.key) > 0){//greater, search right
           if (curr.right != null) {
              found = searchHelp(key, curr.right);
           }
        }
        else {//less, search left
            if (curr.left != null) {
               found = searchHelp(key, curr.left);
            }
        }



        return found;
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        int height = -1;
        if (treeSize == 0){
            height = -1;
        }
        else if (treeSize == 1){
            height = 0;
        }
        else{
            height = heightHelp(root);

        }

        return height;
    }

    public int heightHelp(Node curr){
        int toReturn = 0;


        if (curr == null){//null
            toReturn = -1;
        }
        else if (curr.left != null && curr.right != null){//two children
            toReturn = 1 + Math.max(heightHelp(curr.left), heightHelp(curr.right));
        }

        else if (curr.left != null && curr.right == null){//only left child
            toReturn = 1 + heightHelp(curr.left);

        }

        else if (curr.left == null && curr.right != null){//only right child
            toReturn = 1 + heightHelp(curr.right);
        }
        else if (curr != null && curr.left == null && curr.right == null){//no children
            toReturn = 0;
        }

        return toReturn;
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        if (treeSize == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
        if (treeSize == 0){
            throw new EmptyTreeException("Empty Tree");
        }
        else {
            return root.key;
        }
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
        boolean isBST = true;

        if (treeSize == 0){
            return false;
        }


        isBST = bstHelp(root);



        return isBST;
    }

    public boolean bstHelp(Node currRoot){
        boolean isBST = true;

        if (currRoot == null){//being null is valid
            isBST = true;
        }

        else if (currRoot != null){
            if (currRoot.left != null && currRoot.right != null){//has both children
                if (currRoot.left.key.compareTo(currRoot.key) < 0 && currRoot.right.key.compareTo(currRoot.key) >= 0){//still bst, keep checking

                    if (bstHelp(currRoot.left) == false || bstHelp(currRoot.right) == false){
                        isBST = false;
                    }

                }
            }
            else if (currRoot.left != null && currRoot.right == null){//only left child
                if (currRoot.left.key.compareTo(currRoot.key) < 0){ //still bst

                    if (bstHelp(currRoot.left) == false){
                        isBST = false;
                    }

                }

            }
            else if (currRoot.left == null && currRoot.right != null){//only right child
                if (currRoot.right.key.compareTo(currRoot.key) >= 0) {

                    if (bstHelp(currRoot.right) == false){
                        isBST = false;
                    }
                }

            }

        }





        return isBST;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        boolean isBalanced = true;

        if (treeSize == 0 || treeSize == 1){
            return true;
        }

        isBalanced = balanceHelper(root);



        return isBalanced;
    }

    public boolean balanceHelper(Node currRoot){
        boolean isBalanced = true;

        if (currRoot == null){
            isBalanced = true;
        }

        else if (currRoot != null){

            if (currRoot.left != null && currRoot.right != null) {//has both children
                if (balance(currRoot) <= maxImbalance) {
                    if (balance(currRoot.left) > maxImbalance || balance(currRoot.right) > maxImbalance) {
                        isBalanced = false;
                    }
                }
            }

            else if (currRoot.left != null && currRoot.right == null){
                if (balance(currRoot) <= maxImbalance){
                    if (balance(currRoot.left) > maxImbalance){
                        isBalanced = false;
                    }
                }

            }
            else if (currRoot.left == null && currRoot.right != null){
                if (balance(currRoot) <= maxImbalance){
                    if (balance(currRoot.right) > maxImbalance){
                        isBalanced = false;
                    }
                }

            }

        }



        return isBalanced;
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        root.left = null;
        root.right = null;
        root = new Node(null);
        treeSize = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return treeSize;
    }
}
