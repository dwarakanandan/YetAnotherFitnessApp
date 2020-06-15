package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import java.util.Comparator;

public class ParticipantComparator implements Comparator<ContestItemParticipant> {
    @Override
    public int compare(ContestItemParticipant o1, ContestItemParticipant o2) {
        return (int)Math.round(o2.getValue() - o1.getValue());
    }
}
