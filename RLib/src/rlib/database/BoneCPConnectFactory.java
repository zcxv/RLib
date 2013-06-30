package rlib.database;

import java.sql.Connection;
import java.sql.SQLException;

import rlib.logging.Logger;
import rlib.logging.Loggers;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;


/**
 * Фабрика подключений к БД.
 *
 * @author Ronn
 * @created 27.03.2012
 */
public final class BoneCPConnectFactory extends ConnectFactory
{
	private static final Logger log = Loggers.getLogger(BoneCPConnectFactory.class);
	
	/** основной комбо пул подключений */
	private BoneCP source;

	/**
	 * Выключение работы сервера с бд.
	 */
	public synchronized void close()
	{
		// закрываем подключения
		source.close();
		// завершаем пул
		source.shutdown();
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return source.getConnection();
	}

	/**
	 * Инициализация фабрики подключений к БД.
	 * 
	 * @param config настройка пула подключений.
	 * @param driver драйвер БД.
	 * @throws SQLException
	 */
	public synchronized void init(BoneCPConfig config, String driver) throws SQLException
	{
		try
		{
			Class.forName(driver).newInstance();

			source = new BoneCP(config);
			source.getConnection().close();
		}
		catch(Exception e)
		{
			log.warning(new SQLException("could not init DB connection:" + e));
		}
	}
}