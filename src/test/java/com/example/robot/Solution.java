package com.example.robot;

import org.junit.jupiter.api.Test;

import java.util.*;

public class Solution {
    public class TreeNode {
       int val;
       TreeNode left;
       TreeNode right;
       TreeNode() {}
       TreeNode(int val) { this.val = val; }
       TreeNode(int val, TreeNode left, TreeNode right) {
           this.val = val;
           this.left = left;
           this.right = right;
       }
   }
    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<TreeNode>();
        while(root!=null||!stack.isEmpty()) {
            while (root!=null){
                stack.push(root);
                root = root.left;
            }
            root=stack.pop();
            k--;
            if (k==0){
                break;
            }
            root=root.right;
        }
        return root.val;
    }

    @Test
    public void main(){
        StringBuilder sb = new StringBuilder("abc");
        System.out.println(sb.delete(0,sb.length()));
    }
}
