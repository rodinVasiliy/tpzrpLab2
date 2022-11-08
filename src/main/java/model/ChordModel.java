package model;

import lombok.Data;
import validators.ChordModelAddNodeValidator;
import validators.ChordModelInitValidator;
import validators.ChordModelSearchNodeValidator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static constants.Constants.*;

@Data
public class ChordModel {

    private Map<BigInteger, ChordNode> model;
    private BigInteger modelSize;
    private int bitsCount;
    private List<BigInteger> positions = new ArrayList<>();

    public ChordModel(int bitsCount, List<Integer> nodesPositions) {
        ChordModelInitValidator.validate(bitsCount, nodesPositions);

        this.bitsCount = bitsCount;
        this.modelSize = BigInteger.TWO.pow(bitsCount);
        this.model = new TreeMap<>();

        for (Integer pos : nodesPositions) {
            this.positions.add(BigInteger.valueOf(pos));
        }

        for (Integer pos : nodesPositions) {
            addNodeToNewSystem(pos);
        }

        updateFingerTables();
    }

    private void updateFingerTables() {
        for (BigInteger position : positions) {
            ChordNode chordNode = model.get(position);
            updateFingerTable(chordNode);
        }
    }

    public void addNodeToNewSystem(Integer position) {
        BigInteger positionBigInteger = BigInteger.valueOf(position);
        ChordNode chordNode = ChordNode.builder().position(positionBigInteger).build();

        model.put(positionBigInteger, chordNode);
    }

    public void addNode(Integer position) {
        ChordModelAddNodeValidator.validate(this, position);
        BigInteger positionBigInteger = BigInteger.valueOf(position);
        positions.add(positionBigInteger);
        Collections.sort(positions);
        addNodeToNewSystem(position);

        updateFingerTables();
    }

    public void removeNode(Integer position) {
        model.remove(BigInteger.valueOf(position));
        positions.remove(BigInteger.valueOf(position));
        updateFingerTables();

    }

    public ChordNode findNode(String startPos, String targetPos) {
        ChordModelSearchNodeValidator.validate(this, startPos, targetPos);
        BigInteger currentPos = new BigInteger(startPos);
        BigInteger targetPosB = new BigInteger(targetPos);

        int transitionsCount = 0;

        if (Objects.equals(currentPos, targetPosB)) {
            return model.get(currentPos);
        }

        while (!Objects.equals(currentPos, targetPosB)) {
            ChordNode currentNode = model.get(currentPos);
            System.out.printf(currentPos + " -> ");
            ++transitionsCount;

            for (Finger currentFinger : currentNode.getFingersTable()) {
                Interval currentInterval = currentFinger.getInterval();

                if (currentInterval.isNumInCyclic(targetPosB)) {
                    currentPos = currentFinger.getSuccessor().getPosition();
                    break;
                }
            }
        }

        return model.get(currentPos);

    }

    private void updateFingerTable(ChordNode node) {
        List<Finger> fingersTable = new ArrayList<>();
        for (int i = 1; i <= bitsCount; ++i) {
            BigInteger startInterval = calculateFingerStart(node, i);
            BigInteger endInterval = calculateFingerStart(node, i + 1);
            Interval interval = Interval.builder().start(startInterval).end(endInterval).build();
            ChordNode successor = getSuccessor(interval);
            BigInteger successorPositon = successor.getPosition();
            ChordNode predecessor = getPredecessor(successorPositon);
            Finger newFinger = Finger.builder().interval(interval).start(startInterval).successor(successor)
                    .predecessor(predecessor).build();
            fingersTable.add(newFinger);
        }
        node.setFingersTable(fingersTable);
    }

    private ChordNode getSuccessor(Interval interval) {
        BigInteger startPosition = interval.getStart();
        for (BigInteger position : positions) {
            if (position.compareTo(startPosition) >= 0) {
                return model.get(position);
            }
        }
        // в случае например если интервал от 7 до 3
        return model.get(positions.get(0));
    }

    private ChordNode getPredecessor(BigInteger successorPosition) {
        int successorIndex = positions.indexOf(successorPosition);
        int predecessorIndex;
        if (successorIndex == 0) {
            predecessorIndex = positions.size() - 1;
        } else {
            predecessorIndex = --successorIndex;
        }
        BigInteger predecessorPosition = positions.get(predecessorIndex);
        return model.get(predecessorPosition);
    }

    private void updateReferences() {

    }

    private BigInteger calculateFingerStart(ChordNode node, int fingerNumber) {
        BigInteger fingerStart = node.getPosition();
        fingerStart = fingerStart.add(BigInteger.TWO.pow(fingerNumber - 1));
        return fingerStart.mod(BigInteger.TWO.pow(bitsCount));
    }

    public static void main(String[] args) {

        // 1. Create system
        ChordModel chordModel = new ChordModel(INITIAL_SYSTEM_BIT_DIMENSION, INITIAL_NODES_POSITION);
        chordModel.printSystem("#1. Created system");

        Integer nodePositionToAddInteger = Integer.parseInt(NODE_POSITION_TO_ADD);
        chordModel.addNode(nodePositionToAddInteger);
        chordModel.printSystem("#2. System with new node");

        Integer nodePositionToRemoveInteger = Integer.parseInt(NODE_POSITION_TO_REMOVE);
        chordModel.removeNode(nodePositionToRemoveInteger);
        chordModel.printSystem("#3. System without one node");

        // 4. Search node
        ChordNode node = chordModel.findNode(NODE_POSITION_START_SEARCH, NODE_POSITION_TO_SEARCH);
        System.out.println(node);
    }

    public void printSystem(String header) {
        System.out.println("======== " + header + " ========");
        System.out.println("=======================================");
        System.out.println(this);
        System.out.println("=======================================");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<BigInteger, ChordNode> curPos : model.entrySet()) {
            result.append("Position: ").append(curPos.getKey()).append("; ChordNode: \n").append(curPos.getValue());
        }
        return result.toString();
    }
}