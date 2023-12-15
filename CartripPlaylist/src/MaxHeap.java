class MaxHeap {
    Song[] heap;
    int size;
    int capacity;
    String category;

    public MaxHeap(int capacity, String category) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new Song[capacity];
        this.category = category;
    }

    public void insert(Song song) {
        if (size == capacity) return;
        heap[size] = song;
        heapifyUp(size++);
    }

    public Song extractMax() {
        if (size == 0) return null;
        Song max = heap[0];
        heap[0] = heap[--size];
        heapifyDown(0);
        return max;
    }

    // percolateUp
    private void heapifyUp(int index) {
        int parentIndex;

        // this part seems a bit complicated but otherwise there will be so much lines
        // I preferred this
        // if score is bigger or score is the same but name is smaller -> up
        while (index > 0 && (heap[index].getScore(category) > heap[(parentIndex = (index - 1) / 2)].getScore(category)
                || (heap[index].getScore(category) == heap[parentIndex].getScore(category)
                && heap[index].name.compareTo(heap[parentIndex].name) < 0))) {
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    // percolateDown
    private void heapifyDown(int index) {
        int largest = index;
        int leftChildIndex = 2 * index + 1;
        int rightChildIndex = 2 * index + 2;

        if (leftChildIndex < size && isLeftChildLarger(leftChildIndex, largest))
            largest = leftChildIndex;
        if (rightChildIndex < size && isRightChildLarger(rightChildIndex, largest))
            largest = rightChildIndex;

        if (index != largest) {
            swap(index, largest);
            heapifyDown(largest);
        }
    }

    // check which child is larger
    private boolean isLeftChildLarger(int leftChildIndex, int largest) {
        return heap[leftChildIndex].getScore(category) > heap[largest].getScore(category)
                || (heap[leftChildIndex].getScore(category) == heap[largest].getScore(category)
                && heap[leftChildIndex].name.compareTo(heap[largest].name) < 0);
    }

    private boolean isRightChildLarger(int rightChildIndex, int largest) {
        return heap[rightChildIndex].getScore(category) > heap[largest].getScore(category)
                || (heap[rightChildIndex].getScore(category) == heap[largest].getScore(category)
                && heap[rightChildIndex].name.compareTo(heap[largest].name) < 0);
    }

    private void swap(int i, int j) {
        Song temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
