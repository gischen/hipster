/*
 * Copyright 2014 CITIUS <http://citius.usc.es>, University of Santiago de Compostela.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package es.usc.citius.hipster.algorithm;

import es.usc.citius.hipster.model.Node;
import es.usc.citius.hipster.model.function.NodeExpander;

import java.util.Stack;

/**
 * @author Pablo Rodríguez Mier <<a href="mailto:pablo.rodriguez.mier@usc.es">pablo.rodriguez.mier@usc.es</a>>
 */
public class DepthFirstSearch<A,S,N extends Node<A,S,N>> extends Algorithm<A,S,N> {
    private N initialNode;
    private NodeExpander<A,S,N> expander;

    // TODO; Remove duplicates with IDAStar

    public DepthFirstSearch(N initialNode, NodeExpander<A, S, N> expander) {
        this.expander = expander;
        this.initialNode = initialNode;
    }

    private class StackFrameNode {
        // Iterable used to compute neighbors of the current node
        java.util.Iterator<N> successors;
        // Current search node
        N node;
        // Boolean value to check if the node is still unvisited
        // in the stack or not
        boolean visited = false;
        // Boolean to indicate that this node is fully processed
        boolean processed = false;

        StackFrameNode(java.util.Iterator successors, N node) {
            this.successors = successors;
            this.node = node;
        }

        StackFrameNode(N node) {
            this.node = node;
            this.successors = expander.expand(node).iterator();
        }
    }

    public class Iterator implements java.util.Iterator<N> {
        private Stack<StackFrameNode> stack = new Stack<StackFrameNode>();
        private StackFrameNode next;

        private Iterator(){
            this.stack.add(new StackFrameNode(initialNode));
        }


        @Override
        public boolean hasNext() {
            if (next == null){
                // Compute next
                next = nextUnvisited();
                if (next == null) return false;
            }
            return true;
        }

        @Override
        public N next(){
            if (next != null){
                StackFrameNode e = next;
                // Compute the next one
                next = null;
                // Return current node
                return e.node;
            }
            // Compute next
            StackFrameNode nextUnvisited = nextUnvisited();
            if (nextUnvisited!=null){
                return nextUnvisited.node;
            }
            return null;

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }


        private StackFrameNode nextUnvisited(){
            StackFrameNode nextNode;
            do {
                nextNode = processNextNode();
            } while(nextNode != null && (nextNode.processed || nextNode.visited));

            if (nextNode != null){
                nextNode.visited = true;
            }
            return nextNode;
        }


        private StackFrameNode processNextNode(){

            if (stack.isEmpty()) return null;

            // Take current node in the stack but do not remove
            StackFrameNode current = stack.peek();
            // Find a successor
            if (current.successors.hasNext()){
                N successor = current.successors.next();
                // push the node;
                stack.add(new StackFrameNode(successor));
                return current;
            } else {
                // Visited?
                if (current.visited){
                    current.processed = true;
                }
                return stack.pop();
            }
        }
    }
    @Override
    public java.util.Iterator<N> iterator() {
        return new Iterator();
    }
}