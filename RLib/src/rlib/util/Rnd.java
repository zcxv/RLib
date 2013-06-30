package rlib.util;

import java.util.Random;

/**
 * Класс для работы со случайными значениями.
 * 
 * @author Ronn
 */
public abstract class Rnd
{
	private static final Random rnd = new Random();

	/**
	 * Генерирование байтового массива со случайными значениями.
	 * 
	 * @param size размер случайного массива.
	 * @return новый случайный массив.
	 */
	public static byte[] byteArray(int size)
	{
		byte[] result = new byte[size];
		
		for(int i = 0; i < size; i++)
			result[i] = (byte) Rnd.nextInt(128);
		
		return result;
	}
	
	/**
	 * Рассчет срабатывания шанса.
	 * 
	 * @param chance шанс от 0.0 до 100.0.
	 * @return сработал ли шанс.
	 */
	public static boolean chance(float chance)
	{
		if(chance < 0F)
			return false;
		
		if(chance > 99.999999F)
			return true;
		
		return nextFloat() * nextInt(100) <= chance;
	}
	
	/**
	 * Рассчет срабатывания шанса.
	 * 
	 * @param chance шанс от 0 до 100.
	 * @return сработал ли шанс.
	 */
	public static boolean chance(int chance)
	{
		if(chance < 1)
			return false;
		
		if(chance > 99)
			return true;
		
		return nextInt(99) <= chance;
	}

	/**
	 * Генерация случайного вещественного числа.
	 * 
	 * @return число от 0.0 до 1.0
	 */
	public static float nextFloat()
	{
		return rnd.nextFloat();
	}
	
	/**
	 * Генерация случайного целого числа.
	 * 
	 * @return число от -2.5ккк до 2.5ккк
	 */
	public static int nextInt()
	{
		return rnd.nextInt();
	}

	/**
	 * Возвращает случайное число [0, max].
	 * 
	 * @param max максимальное число.
	 * @return случайное число [0, max]
	 */
	public static int nextInt(int max)
	{
		return rnd.nextInt(max);
	}
	
	/**
	 * Возвращает случайное число [min, max].
	 * 
	 * @param min минимальное число.
	 * @param max максимальное число.
	 * @return случайное число [min, max]
	 */
	public static int nextInt(int min, int max)
	{
		return min + nextInt(Math.abs(max - min) + 1);
	}
	
	/**
	 * Возвращает случайное число [min, max].
	 * 
	 * @param min минимальное число.
	 * @param max максимальное число.
	 * @return случайное число [min, max]
	 */
	public static long nextLong(long min, long max)
	{
		return min + Math.round(nextFloat() * Math.abs(max - min) + 1);
	}
}