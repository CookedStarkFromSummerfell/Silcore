package pl.kalishak.silcore.api.world.material.gas;

/**
 * Implement this interface as a capability which should handle fluids, generally storing them in
 * one or more internal objects.
 * <p>
 * A reference implementation is provided {@link pl.kalishak.silcore.impl.world.material.gas.GasTank}.
 */
public interface IGasTank {
    default boolean isEmpty() {
        for (int i = 0; i < getTanks(); i++) {
            if (!getGasInTank(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the number of gas storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    int getTanks();

    /**
     * Returns the GasStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This GasStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED GASSTACK</em></strong>
     * </p>
     *
     * @param index Tank to query.
     * @return GasStack in a given tank. {@link GasStack#EMPTY empty} otherwise.
     */
    GasStack getGasInTank(int index);

    /**
     * Retrieves the maximum pressure for a given tank.
     *
     * @param index Tank to query.
     * @return The maximum pressure held by the tank.
     */
    float getMaxPressure(int index);

    /**
     * {@return Retrieves the maximum gas amount for a given tank. }
     *
     * @param index Tank to query.
     */
    int getTankCapacity(int index);

    /**
     * {@return Whether tank or its implementation(Block, Entity or Item) should be damaged.}
     * @param index Tank to query.
     *
     */
    boolean doesOverpressureDamageTank(int index);

    /**
     * This function is a way to determine which gases can exist inside a given handler. General purpose tanks will
     * basically always return <b>true</b> for this.
     *
     * @param index Tank to query for validity
     * @param stack Stack to test with for validity
     * @return <b>true</b> if the tank can hold the GasStack, not considering current state.
     */
    boolean isGasValid(int index, GasStack stack);

    /**
     * Fills gas into internal tanks, distribution is left entirely to the IGasTank.
     *
     * @param resource GasStack representing the Gas and maximum amount of gas to be filled.
     * @param simulate If simulated, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(GasStack resource, float withPressure, boolean simulate);

    /**
     * Drains gas out of internal tanks, distribution is left entirely to the IGasTank.
     *
     * @param resource FasStack representing the Gas and maximum amount of gas to be drained.
     * @param simulate If simulated, drain will only be simulated.
     * @return GasStack representing the Gas and amount that was (or would have been, if
     *         simulated) drained.
     */
    GasStack drain(GasStack resource, float withPressure, boolean simulate);

    /**
     * Drains gas out of internal tanks, distribution is left entirely to the IGasTank.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of gas to drain.
     * @param simulate If simulated, drain will only be simulated.
     * @return GasStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    GasStack drain(int maxDrain, float minPressure, boolean simulate);
}
