/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public class ProsesKuyruk implements IProsesKuyruk {
	private Node<IOzelProses> front;
	private Node<IOzelProses> back;

	public ProsesKuyruk() {
		front = back = null;
	}

	@Override
	public void enqueue(IOzelProses process) {
		Node<IOzelProses> tmp = new Node<IOzelProses>(process);
		if (isEmpty()) {
			front = back = tmp;
		} else {
			tmp.next = back;
			back.prev = tmp;
			back = tmp;
		}
	}

	@Override
	public IOzelProses dequeue() {
		if (!isEmpty()) {
			var deletedNode = front;
			front = front.prev;
			if (front != null)
				front.next = null;
			else
				back = null;
			return deletedNode.data;
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return front == null;
	}

	@Override
	public IOzelProses getFirstItem() {
		return this.front.data;
	}

	// Kuyruktaki tüm proseslerin bekleme zamanını 1 artırır. Bekleme süresi 20
	// saniyeyi aşan prosesleri döndürür.
	@Override
	public IProsesKuyruk increaseWaitingTime() {
		IProsesKuyruk timeOutProcesses = new ProsesKuyruk();
		Node<IOzelProses> processNode = front;

		while (processNode != null) {
			if (processNode.data.increaseWaitingTime()) {
				timeOutProcesses.enqueue(processNode.data);
			}
			processNode = processNode.prev;
		}

		return timeOutProcesses;
	}

	@Override
	public IProsesKuyruk search(int destinationTime) {
		IProsesKuyruk foundedProcesses = new ProsesKuyruk();
		if (this.isEmpty())
			return foundedProcesses;
		Node<IOzelProses> processNode = front;
		while (processNode.data.getDestinationTime() <= destinationTime) {
			if (processNode.data.getDestinationTime() == destinationTime)
				foundedProcesses.enqueue(processNode.data);
			processNode = processNode.prev;
			if (processNode == null)
				break;
		}
		return foundedProcesses;
	}

	@Override
	public void delete(IOzelProses process) {
		Node<IOzelProses> deletedNode = front;
		while (deletedNode.data != process) {
			deletedNode = deletedNode.prev;
			if (deletedNode == null)
				return;
		}

		if (deletedNode == front)
			front = front.prev;
		else if (deletedNode == back)
			back = back.prev;
		else {
			deletedNode.prev.next = deletedNode.next;
			deletedNode.next.prev = deletedNode.prev;
		}
	}
}
