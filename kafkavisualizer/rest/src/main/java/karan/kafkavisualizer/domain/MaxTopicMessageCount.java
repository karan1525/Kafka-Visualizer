package karan.kafkavisualizer.domain;

public class MaxTopicMessageCount extends IntegerMicroType {

    public static final MaxTopicMessageCount EMPTY = new MaxTopicMessageCount(EMPTY_VALUE);

    public MaxTopicMessageCount(int value) {
        super(value);
    }
}
