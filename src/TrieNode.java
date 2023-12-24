class TrieNode {

    boolean isEnd;
    TrieNode[] childs = new TrieNode[26];

    TrieNode(boolean isEnd) {
        this.isEnd = isEnd;
    }

}
