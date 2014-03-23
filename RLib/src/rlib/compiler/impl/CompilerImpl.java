package rlib.compiler.impl;

import java.io.File;

import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import rlib.compiler.Compiler;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация обертки над java компилятором для удобной компиляции java кода в
 * runtime.
 * 
 * @author Ronn
 */
public class CompilerImpl implements Compiler {

	private static final Logger LOGGER = Loggers.getLogger(Compiler.class);

	/** слушатель ошибок компиляций */
	private final CompileListener listener;

	/** компилятор java кода */
	private final JavaCompiler compiler;

	/** загрузчик скомпилированных классов */
	private final CompileClassLoader loader;

	/** менедж по компилируемым ресурсам */
	private final CompileJavaFileManager fileManager;

	/** отображать ли ошибки компиляции */
	private final boolean showDiagnostic;

	/**
	 * @param showDiagnostic отображать ли ошибки компиляции.
	 */
	public CompilerImpl(boolean showDiagnostic) {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.listener = new CompileListener();
		this.loader = new CompileClassLoader();

		StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(listener, null, null);

		this.fileManager = new CompileJavaFileManager(standardJavaFileManager, loader);
		this.showDiagnostic = showDiagnostic;
	}

	@Override
	public Class<?>[] compile(File... files) {

		if(files.length < 1) {
			return null;
		}

		Array<JavaFileObject> javaSource = ArrayFactory.newArray(JavaFileObject.class, files.length);

		for(File file : files) {
			javaSource.add(new JavaFileSource(file));
		}

		return compile(null, javaSource);
	}

	/**
	 * Компиляция списка объектов.
	 * 
	 * @param options опции компиляции.
	 * @param source список исходников.
	 * @return список скомпиленых классов.
	 */
	protected synchronized Class<?>[] compile(Iterable<String> options, Iterable<? extends JavaFileObject> source) {

		JavaCompiler compiler = getCompiler();

		CompileJavaFileManager fileManager = getFileManager();
		CompileListener listener = getListener();
		CompileClassLoader loader = getLoader();

		try {

			CompilationTask task = compiler.getTask(null, fileManager, listener, options, null, source);
			task.call();

			Diagnostic<JavaFileObject>[] diagnostics = listener.getDiagnostics();

			if(isShowDiagnostic() && diagnostics.length > 1) {

				LOGGER.warning("errors:");

				for(Diagnostic<JavaFileObject> diagnostic : diagnostics) {
					LOGGER.warning(String.valueOf(diagnostic));
				}
			}

			Array<Class<?>> result = ArrayFactory.newArray(Class.class);

			String[] classNames = fileManager.getClassNames();

			for(String className : classNames) {
				try {
					Class<?> cs = Class.forName(className, false, loader);
					result.add(cs);
				} catch(ClassNotFoundException e) {
					LOGGER.warning(e);
				}
			}

			return result.toArray(new Class[result.size()]);
		} finally {
			listener.clear();
			fileManager.clear();
		}
	}

	/**
	 * Скомпилировать классы в дериктории.
	 * 
	 * @param container контейнер классов.
	 * @param directory дериктория.
	 */
	private void compileDirectory(Array<Class<?>> container, File directory) {

		File[] files = directory.listFiles();

		for(File file : files) {
			if(file.isDirectory()) {
				compileDirectory(container, file);
			} else if(file.getName().endsWith(Compiler.SOURCE_EXTENSION)) {
				container.addAll(compile(file));
			}
		}
	}

	@Override
	public Class<?>[] compileDirectory(File... files) {

		Array<Class<?>> container = ArrayFactory.newArray(Class.class);

		for(File directory : files) {

			if(!directory.exists() || !directory.isDirectory()) {
				continue;
			}

			compileDirectory(container, directory);
		}

		return container.toArray(new Class[container.size()]);
	}

	/**
	 * @return компилятор java кода.
	 */
	protected JavaCompiler getCompiler() {
		return compiler;
	}

	/**
	 * @return менедж по компилируемым ресурсам.
	 */
	protected CompileJavaFileManager getFileManager() {
		return fileManager;
	}

	/**
	 * @return слушатель ошибок компиляций.
	 */
	protected CompileListener getListener() {
		return listener;
	}

	/**
	 * @return загрузчик скомпилированных классов.
	 */
	protected CompileClassLoader getLoader() {
		return loader;
	}

	/**
	 * @return отображать ли диагностику.
	 */
	private boolean isShowDiagnostic() {
		return showDiagnostic;
	}
}