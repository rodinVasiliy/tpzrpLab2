package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ChordNode {

    private BigInteger position;
    private List<Finger> fingersTable;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(position.toString());
        sb.append('\n');
        sb.append(fingersTableString());
        return new String(sb);
        //        return "not implemented!\n";
    }

    private String fingersTableString() {
        StringBuilder result = new StringBuilder();

        for (Finger finger : fingersTable) {
            result.append("\n   {");
            result.append("\n       Start = ").append(finger.getStart().toString());
            result.append("\n       Interval = ").append(finger.getInterval());
            result.append("\n       Successor position = ").append(finger.getSuccessor().getPosition().toString());
            result.append("\n       Predecessor position = ").append(finger.getPredecessor().getPosition().toString());
            result.append("\n   }\n");
        }
        return result.toString();
    }
}
