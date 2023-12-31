/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public class Node<A> {
	public Node<A> next;
	public Node<A> prev;
	public A data;

	public Node(A data) {
		this.data = data;
		this.next = null;
		this.prev = null;
	}
}
