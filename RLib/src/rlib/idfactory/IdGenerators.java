package rlib.idfactory;

import java.util.concurrent.ScheduledExecutorService;

import rlib.database.ConnectFactory;


/**
 * Фабрика ид генераторов.
 * 
 * @author Ronn
 */
public class IdGenerators
{
	/**
	 * Получение генератора ид на основе BitSet.
	 * 
	 * @param connects фабрика подключения к БД.
	 * @param executor исполнитель.
	 * @param tables таблицы извлекаемых ид из БД.
	 * @return новый генератор.
	 */
	public static IdGenerator newBitSetIdGeneratoe(ConnectFactory connects, ScheduledExecutorService executor, String[][] tables)
	{
		return new BitSetIdGenerator(connects, executor, tables);
	}
	
	/**
	 * Создание простого генератора ид в указанном промежутке.
	 * 
	 * @param start стартовый ид.
	 * @param end конечный ид.
	 * @return новый генератор.
	 */
	public static IdGenerator newSimpleIdGenerator(int start, int end)
	{
		return new SimpleIdGenerator(start, end);
	}
	
	private IdGenerators()
	{
		throw new IllegalArgumentException();
	}
}
