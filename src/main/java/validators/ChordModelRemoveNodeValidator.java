package validators;

import model.ChordModel;

import java.math.BigInteger;
import java.util.Set;

public class ChordModelRemoveNodeValidator {
    public static void validate(ChordModel model, Integer position) {
        Set<BigInteger> nodesPositions = model.getModel().keySet();

        BigInteger pos = BigInteger.valueOf(position);
        if (!nodesPositions.contains(pos)) {
            throw new RuntimeException("[ERROR] Trying to delete not existing node");
        }
    }
}