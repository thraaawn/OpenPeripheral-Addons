package openperipheral.api;

/**
 * Set of common methods from architectures. Used as argument of types marked with {@link Env}
 *
 */
public interface IArchitectureAccess {

	public String architecture();

	public String peripheralName();

	public boolean signal(String name, Object... args);

	/**
	 * Convert object to Lua object with callable methods
	 */
	public Object wrapObject(Object target);
}
