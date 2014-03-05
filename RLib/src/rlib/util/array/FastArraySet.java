package rlib.util.array;

/**
 * Модель уникального множества на основе быстрго массива.
 * 
 * @author Ronn
 */
public class FastArraySet<E> extends FastArray<E> {

	private static final long serialVersionUID = 1L;

	public FastArraySet(Class<E> type) {
		super(type);
	}

	public FastArraySet(Class<E> type, int size) {
		super(type, size);
	}

	@Override
	public FastArray<E> add(E element) {

		if(contains(element)) {
			return this;
		}

		return super.add(element);
	}
}
