/**
 * 
 */
package fr.xebia.hadoop.mrunit;

import org.apache.hadoop.mrunit.types.Pair;

/**
 * Stuff for testing
 * 
 * @author Tom
 * 
 */
public class MapReduceTestUtils {

    public static <T, K> Pair<T, K> buildExpectedOutput(T expectedKey, K expectedValue) {
        return new Pair<T, K>(expectedKey, expectedValue);
    }

}
