package invaded.cc.core.poll;

import invaded.cc.core.Spotify;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PollHandler {

    private final Spotify plugin;
    private List<Poll> polls = new ArrayList<>();
    private PollThread thread;

    public PollHandler(Spotify plugin) {
        this.plugin = plugin;

        thread = new PollThread();
        thread.start();
    }

    public void shutdown() {
        thread.stop();
    }

    public void create(Poll poll) {
        this.polls.add(poll);
    }

    public Poll getPoll(String id) {
        for(Poll poll : polls)
            if(poll.getId().equalsIgnoreCase(id)) return poll;

        return null;
    }
}
