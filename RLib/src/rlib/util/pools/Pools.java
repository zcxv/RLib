package rlib.util.pools;

/**
 * Фабрика пулов.
 * 
 * @author Ronn
 */
public final class Pools {

	/**
	 * Создание нового быстрого объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newConcurrentFoldablePool(Class<? extends Foldable> type) {
		return new ConcurrentFoldablePool<T>(10, type);
	}

	/**
	 * Создание нового быстрого объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newConcurrentFoldablePool(Class<? extends Foldable> type, int size) {
		return new ConcurrentFoldablePool<T>(size, type);
	}

	/**
	 * Создание нового быстрого объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newFoldablePool(Class<? extends Foldable> type) {
		return new FastFoldablePool<T>(10, type);
	}

	/**
	 * Создание нового быстрого объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newFoldablePool(Class<? extends Foldable> type, int size) {
		return new FastFoldablePool<T>(size, type);
	}

	/**
	 * Создание нового мульти конкурентного объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newMultiConcurrentFoldablePool(int size, Class<? extends Foldable> type) {
		return new MultiConcurrentFoldablePool<T>(size, type);
	}

	/**
	 * Создание нового синхронизированного объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newSynchronizedFoldablePool(Class<? extends Foldable> type) {
		return new SynchronizedFoldablePool<T>(10, type);
	}

	/**
	 * Создание нового синхронизированного объектного пула.
	 * 
	 * @param type тип объектов пула.
	 * @return новый объектный пул.
	 */
	public static final <T extends Foldable> FoldablePool<T> newSynchronizedFoldablePool(Class<? extends Foldable> type, int size) {
		return new SynchronizedFoldablePool<T>(size, type);
	}

	private Pools() {
		throw new IllegalArgumentException();
	}
}
